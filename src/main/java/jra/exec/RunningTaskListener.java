package jra.exec;

import java.util.List;

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
