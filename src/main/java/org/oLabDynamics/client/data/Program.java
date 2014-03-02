package org.oLabDynamics.client.data;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Benoit Charroux
 *
 */
public class Program {
	
	Code code;
	List<InputData> inputs = new ArrayList<InputData>();
	
	public void setCode(Code code){
		this.code = code;
	}
	
	public void addInputDate(InputData inputData){
		inputs.add(inputData);
	}

	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
