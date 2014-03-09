package org.oLabDynamics.client.exec;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
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
	Collection<Counter> counters;
	List<OutputData> outputs;
	UPDATE_STATE_FREQUENCY updateResultFrequency; 
	
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
		// TODO Auto-generated method stub
		
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
		if(mayInterruptIfRunning==false && state==State.RUNNING){
			return false;
		}
		cancelLatch = new CountDownLatch(1);
		if(dialogWithServerThread!=null && dialogWithServerThread.isAlive()){
			dialogWithServerThread.interrupt();
		}
		try {
			System.out.println("latch wait");
			cancelLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		System.out.println("latch fin wait");
		cancelLatch = null;
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
	public String[] getCounterNames() {
		return null;
	}
	
	@Override
	public Collection<Counter> getCounters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Counter findByName(String counterName) {
		// TODO Auto-generated method stub
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

	public void setCounters(Collection<Counter> counters) {
		this.counters = counters;
	}

	@Override
	public void run() {
		
		dialogWithServerThread = Thread.currentThread();
		
		System.out.println("debut run");
			
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
   	 		
   	 		System.out.println("get running state");

   	   	 	ResponseEntity<RunningTaskImpl> response = restTemplate.exchange(href, HttpMethod.GET, entity, RunningTaskImpl.class);
   	   	 	HttpStatus statusCode = response.getStatusCode();
   	   	 	if(statusCode != HttpStatus.OK){	
   	   	 		throw new ServerException("Unable to connect the server ! Check execAndShare.properties");
   	   	 	}
   	   	 
   	   	 	newRunningTask = response.getBody();
   	   	 
   	   	 	this.setState(newRunningTask.getState());
   	 
   	   	 	this.setCounters(newRunningTask.getCounters());
   	   	 
   	   	 	this.setFailureInfo(newRunningTask.getFailureInfo());

   	 	}while(state==State.PENDING || state==State.RUNNING);
	
   	 	if(state == State.SUCCEEDED){

   	 		this.setOutputs(newRunningTask.getOutputs());
   	 		
   	 	} 
   	 	
 		synchronized(this){
 			notify();
 		}
 		
	}

	
}
