package org.oLabDynamics.client.exec;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.oLabDynamics.client.data.OutputData;

/**
 * 
 * @author Benoit Charroux
 *
 */
public interface RunningTask {
	
	/// tout logger 
	/// alerte admin si pb => par mail ?
	/// reporting pour les PDF des resultats, pour les stats (Counters) sur les resultats...
	
	public static enum State
	{
		PENDING,  
		RUNNING,
		SUCCEEDED,
		FAILED,
		CANCELLED_BY_USER,
		KILLED_BY_ADMINISTRATOR
	}
	
	public static enum UPDATE_STATE_FREQUENCY{
		HIGH(2),
		NORMAL(5),
		LOW(10);
		
		private final int seconds;
		
		UPDATE_STATE_FREQUENCY(int seconds){
			this.seconds = seconds;
		}
		
		int frequency(){
			return seconds;
		}
	}
	
	public void addRunningTaskListener(RunningTaskListener listener);
	
	/**
	 * 
	 * @param mayInterruptIfRunning
	 * @return
	 */
	public boolean cancel(boolean mayInterruptIfRunning);
	
	/**
	 * Wait until computation complete and succeed.
	 * Blocking method.
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<OutputData> getResult() throws InterruptedException, ExecutionException;
	
	/**
	 * 
	 * @param frequency Freqency to get a new state from the server. 
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<OutputData> getResult(UPDATE_STATE_FREQUENCY frequency) throws InterruptedException, ExecutionException;
	
	/**
	 * No blocking method
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<OutputData> getIntermediateResult() throws InterruptedException, ExecutionException;
	
	/**
	 * Wait for computation to complete or time out occurs.
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public List<OutputData> getResult(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
	
	public List<OutputData> getResult(long timeout, TimeUnit unit, UPDATE_STATE_FREQUENCY frequency) throws InterruptedException, ExecutionException, TimeoutException;
	
	/**
	 * Returns an instance of the corresponding type to an output data : Image...
	 * @param data
	 * @return
	 */
	public Object getInstance(OutputData data);
	
	public long getTaskId();
		
	public State getState();
	
	public String getFailureInfo();
	
	public String[] getCounterNames();
	
	public Collection<Counter> getCounters();
	
	public Counter findByName(String counterName);
	  
}
