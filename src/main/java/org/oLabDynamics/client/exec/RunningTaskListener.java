package org.oLabDynamics.client.exec;

import java.util.List;

import org.oLabDynamics.client.data.OutputData;

/**
 * 
 * @author Benoit Charroux
 *
 */
public interface RunningTaskListener {
	
	public void onNewState(RunningTask.State state, RunningTask runningTask);

}
