package org.oLabDynamics.client;

import java.util.List;

/**
 * 
 * @author Benoit Charroux
 *
 */
public interface RunningTask {
	
	public boolean cancel(boolean mayInterruptIfRunning);
	
	public List<OutputData> waitForResult();
	
	public List<OutputData> waitForResult(long timeout, TimeUnit unit);
	
	public boolean isCancelled();
	
	public boolean isDone(); 

}
