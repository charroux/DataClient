package org.oLabDynamics.client.exec;

import java.util.List;

import org.oLabDynamics.client.data.OutputData;

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
