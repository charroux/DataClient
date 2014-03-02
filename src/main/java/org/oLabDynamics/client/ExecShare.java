package org.oLabDynamics.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;

import org.oLabDynamics.client.data.Code;
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.exec.ExecutorException;
import org.oLabDynamics.client.exec.ResultListener;
import org.oLabDynamics.client.exec.RunningTask;
import org.oLabDynamics.rest.Resource;

/**
 * 
 * @author Benoit Charroux
 *
 * @param <T>
 */
public interface ExecShare<T> {
		
	enum Format{
		JSON
	}
	
	public List<T> prepare(Query query) throws Exception;
	
	public void publish(Resource resourceSupport);
	
	public RunningTask exec(Code code) throws ExecutorException;
	
	public void exec(Code code, ResultListener resultListener) throws ExecutorException;
	
	public RunningTask exec(Code code, List<InputData> inputs) throws ExecutorException;
	
	public void exec(Code code, List<InputData> inputs, ResultListener resultListener) throws ExecutorException;
	
	public void exportInputData(List<InputData> inputs, FileOutputStream file, Format format);
	
	public void exportInputData(List<InputData> inputs, OutputStream outputStream, Format format);
	
	public List<InputData> importInputData(FileInputStream file, Format format);
	
	public List<InputData> importInputData(InputStream outputStream, Format format);	

	public void exportOutputData(List<OutputData> outputs, FileOutputStream file, Format format);
	
	public void exportOutputData(List<OutputData> outputs, OutputStream outputStream, Format format);

	public List<OutputData> importOutputData(FileInputStream file, Format format);
	
	public List<OutputData> importOutputData(InputStream outputStream, Format format);	
}
