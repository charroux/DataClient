package org.oLabDynamics.client;

import java.util.ArrayList;
import java.util.List;

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
