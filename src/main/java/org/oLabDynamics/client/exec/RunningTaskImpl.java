package org.oLabDynamics.client.exec;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.client.data.ExecShareImpl;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.data.Publication;
import org.oLabDynamics.client.data.ServerException;
import org.oLabDynamics.rest.Resource;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Benoit Charroux
 *
 */
public class RunningTaskImpl extends org.oLabDynamics.rest.Resource implements RunningTask, Runnable{
	
	Future<?> future;
	ExecShareImpl execShare;
	String href;
	
	Thread dialogWithServerThread;
	CountDownLatch cancelLatch; 
	long taskId;
	State state;
	String failureInfo;
	Collection<Indicator> indicators;
	List<OutputData> outputs;
	UPDATE_STATE_FREQUENCY updateResultFrequency = UPDATE_STATE_FREQUENCY.NORMAL; 
	
	List<RunningTaskListener> taskListeners;
	
	public RunningTaskImpl(){
		super();
		taskListeners = new ArrayList<RunningTaskListener>();
		indicators = new ArrayList<Indicator>();
	}
	/**
	 * 
	 * @author Benoit Charroux
	 *
	 */
	class TaskListenerThread implements Runnable{

		RunningTaskImpl runningTask;
		
		public TaskListenerThread(RunningTaskImpl runningTask) {
			super();
			this.runningTask = runningTask;
		}

		@Override
		public void run() {
			for(int i=0; i<taskListeners.size(); i++){
				taskListeners.get(i).onProgress(state, runningTask);
			}
			
		}
		
	}
	
	public List<OutputData> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<OutputData> outputs) {
		this.outputs = outputs;
	}

	public void setExecShare(ExecShareImpl execShare){
		this.execShare = execShare;
	}
	
	@Override
	public void addRunningTaskListener(RunningTaskListener listener) {
		if(taskListeners.contains(listener) == false){
			taskListeners.add(listener);
		}
	}
	
	@Override
	public void removeRunningTaskListener(RunningTaskListener listener){
		taskListeners.remove(listener);
	}
	
	@Override
	public synchronized List<OutputData> getResult() throws InterruptedException, org.oLabDynamics.client.exec.ExecutionException{
		return this.getResult(UPDATE_STATE_FREQUENCY.NORMAL);
	}
	
	@Override
	public List<OutputData> getResult(UPDATE_STATE_FREQUENCY frequency) throws InterruptedException, org.oLabDynamics.client.exec.ExecutionException {
		
		while(state==State.PENDING || state==State.RUNNING){
			wait();
		}
		
		if(state == State.SUCCEEDED){
			return outputs;
		}
		
		if(state == State.FAILED){
			throw new org.oLabDynamics.client.exec.ExecutionException(this.failureInfo);
		}
		
		if(state == State.CANCELLED_BY_USER){
			throw new org.oLabDynamics.client.exec.ExecutionException("Cancelled by user");
		}
		
		if(state == State.KILLED_BY_ADMINISTRATOR){
			throw new org.oLabDynamics.client.exec.ExecutionException("Killed by administrator");
		}
		
		return outputs;
	}

	@Override
	public List<OutputData> getResult(long timeout, TimeUnit unit) throws InterruptedException, org.oLabDynamics.client.exec.ExecutionException, org.oLabDynamics.client.exec.TimeoutException{
		return null;
	}

	@Override
	public List<OutputData> getResult(long timeout, TimeUnit unit,
			UPDATE_STATE_FREQUENCY frequency) throws InterruptedException,
			org.oLabDynamics.client.exec.ExecutionException,
			org.oLabDynamics.client.exec.TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		
		// first stop the thread that ask the server current running state...
	   	
		if(mayInterruptIfRunning==false && state==State.RUNNING){
			return false;
		}
		cancelLatch = new CountDownLatch(1);
		if(dialogWithServerThread!=null && dialogWithServerThread.isAlive()){
			dialogWithServerThread.interrupt();
		}
		try {
			cancelLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		cancelLatch = null;
		
		// ...then send the rest server a cancel command
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
		String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
   	
		byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
		headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
   	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
   	 	RestTemplate restTemplate = execShare.getRestTemplate();
   	 	
		ResponseEntity<RunningTaskImpl> response = restTemplate.exchange(href, HttpMethod.DELETE, entity, RunningTaskImpl.class);
	   	HttpStatus statusCode = response.getStatusCode();
	   	if(statusCode != HttpStatus.OK){	
	   		throw new ServerException("Unable to connect the server ! Check execAndShare.properties");
	   	}
	   	 	
	   	RunningTask newRunningTask = response.getBody();
	   	
	   	this.setState(newRunningTask.getState());
	   		
	   	Collection<Indicator> newIndicators = newRunningTask.getIndicators();
	   	if(newIndicators != null){
	   		Iterator<Indicator> newIndicatorsIterator = newIndicators.iterator();
	   		Indicator indicator;
	   		Indicator newIndicator;
	   		while(newIndicatorsIterator.hasNext()){
	   			newIndicator = newIndicatorsIterator.next();
	   			indicator = this.findIndicatorByName(newIndicator.getName());
	   			if(indicator != null){
	   				indicator.setValue(newIndicator.getValue());
	   			}
	   		}
	   	}
	   	 	  	 
	   	this.setFailureInfo(newRunningTask.getFailureInfo());
	   	
		return state == State.CANCELLED_BY_USER;
	}

	@Override
	public List<OutputData> getIntermediateResult()
			throws InterruptedException,
			org.oLabDynamics.client.exec.ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getInstance(OutputData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTaskId() {
		return taskId;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public String getFailureInfo() {
		return failureInfo;
	}

	@Override
	public String[] getIndicatorNames() {
		ArrayList<String> names = new ArrayList<String>();
		Iterator<Indicator> indicatorsIterator = indicators.iterator();
  	 	Indicator indicator;
  	 	while(indicatorsIterator.hasNext()){
  	 		indicator = indicatorsIterator.next();
  	 		names.add(indicator.getName());
  	 	}
  	 	if(names.size() == 0){
  	 		return null;
  	 	}
  	 	return names.toArray(new String[0]);
	}
	
	@Override
	public Collection<Indicator> getIndicators() {
		return indicators;
	}

	@Override
	public Indicator findIndicatorByName(String indicatorName) {
		Iterator<Indicator> indicatorsIterator = indicators.iterator();
  	 	Indicator indicator;
  	 	while(indicatorsIterator.hasNext()){
  	 		indicator = indicatorsIterator.next();
  	 		if(indicator.getName().equals(indicatorName)){
  	 			return indicator;
  	 		}
  	 	}
		return null;
	}

	public void getRunningState() {
		
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = getLink(attributeName);
		if(link == null){
		
		}
		
		href = link.getHref();
		
		ExecutorService executorService = execShare.getExecutorService();
		future = executorService.submit(this);
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setFailureInfo(String failureInfo) {
		this.failureInfo = failureInfo;
	}

	public void setIndicators(Collection<Indicator> indicators) {
		this.indicators = indicators;
	}

	@Override
	public void run() {
		
		dialogWithServerThread = Thread.currentThread();
			
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
		String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
   	
		byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
		headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
   	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
   	 	RestTemplate restTemplate = execShare.getRestTemplate();
   	 	
   	 	RunningTaskImpl newRunningTask;
   	 	
   	 	do{
   	 		
   	 		try {
				Thread.sleep(updateResultFrequency.frequency() * 1000);
			} catch (InterruptedException e) {
				state = State.CANCELLED_BY_USER;
				if(cancelLatch != null){
					cancelLatch.countDown();
				}
				return;
			}
   	 		
   	   	 	ResponseEntity<RunningTaskImpl> response = restTemplate.exchange(href, HttpMethod.GET, entity, RunningTaskImpl.class);
   	   	 	HttpStatus statusCode = response.getStatusCode();
   	   	 	if(statusCode != HttpStatus.OK){	
   	   	 		throw new ServerException("Unable to connect the server ! Check execAndShare.properties");
   	   	 	}
   	   	 
   	   	 	newRunningTask = response.getBody();
   	   	 
   	   	 	this.setState(newRunningTask.getState());
   	   	 	
   	   	 	Collection<Indicator> newIndicators = newRunningTask.getIndicators();
   	   	 	if(newIndicators != null){
   	   	 		Iterator<Indicator> newIndicatorsIterator = newIndicators.iterator();
   	   	 		Indicator indicator;
   	   	 		Indicator newIndicator;
   	   	 		while(newIndicatorsIterator.hasNext()){
   	   	 			newIndicator = newIndicatorsIterator.next();
   	   	 			indicator = this.findIndicatorByName(newIndicator.getName());
   	   	 			if(indicator != null){
   	   	 				indicator.setValue(newIndicator.getValue());
   	   	 			}
   	   	 		}
   	   	 	}
   	   	 	  	 
   	   	 	this.setFailureInfo(newRunningTask.getFailureInfo());

   	   	 	execShare.getExecutorService().submit(new TaskListenerThread(this));
   	   	 
   	   	 	
   	 	}while(state==State.PENDING || state==State.RUNNING);
	
   	 	if(state == State.SUCCEEDED){

   	 		this.setOutputs(newRunningTask.getOutputs());
   	 		
   	 	} 
   	 	
 		synchronized(this){
 			notify();
 		}
 		
	}

	
}
