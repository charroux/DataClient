package org.oLabDynamics.client.exec;

import java.util.List;

import org.oLabDynamics.client.data.OutputData;

/**
 * 
 * @author Benoit Charroux
 *
 */
public interface ResultListener {
	
	public void onResult(List<OutputData> ouputs);

}
