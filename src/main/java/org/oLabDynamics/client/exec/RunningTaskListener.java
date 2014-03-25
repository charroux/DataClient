package org.oLabDynamics.client.exec;

import java.util.List;

import org.oLabDynamics.client.data.OutputData;

/**
 * 
 * Get computational information in an asynchronous mode.
 * 
 * @author Benoit Charroux
 *
 */
public interface RunningTaskListener {
	
	public void onProgress(RunningTask.State state, RunningTask runningTask);

}
