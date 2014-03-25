package org.oLabDynamics.client.exec;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.oLabDynamics.client.data.OutputData;

/**
 * Main interface to control code computation (cancellation...) and to get information (results, indicators like ramaining time to complete...).
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
	
	/**
	 * Frequency used to refresh information from the computational server.
	 * 
	 * @author charroux
	 *
	 */
	public static enum UPDATE_STATE_FREQUENCY{
		HIGH(2),
		NORMAL(5),
		LOW(10);
		
		private final int seconds;
		
		UPDATE_STATE_FREQUENCY(int seconds){
			this.seconds = seconds;
		}
		
		public int frequency(){
			return seconds;
		}
	}
	
	/**
	 * An interface to get informations about the computation in an asynchronous mode
	 * @param listener
	 */
	public void addRunningTaskListener(RunningTaskListener listener);
	
	/**
	 * @param listener
	 */
	public void removeRunningTaskListener(RunningTaskListener listener);
	
	/**
	 * Wait until computation has been cancelled.
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
	 * No blocking method to get results
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
	
	/**
	 * 
	 * @return the name of the indicators, or null if there is indicator.
	 */
	public String[] getIndicatorNames();
	
	/**
	 * Get indicators like computation start time, remaining time to complete...
	 * @return
	 */
	
	public Collection<Indicator> getIndicators();
	
	/**
	 * Find an indicator by its name.
	 * @param indicatorName
	 * @return
	 */
	public Indicator findIndicatorByName(String indicatorName);
	  
}
