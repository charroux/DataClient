package org.oLabDynamics.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;

import org.oLabDynamics.client.data.Code;
import org.oLabDynamics.client.data.CompanionSite;
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.data.ThematicSite;
import org.oLabDynamics.client.exec.ExecutorException;
import org.oLabDynamics.client.exec.RunningTaskListener;
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
	
	/**
	 * Request
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<T> prepare(Query query) throws Exception;
	
	public void publish(Resource resourceSupport);
	
	/**
	 * Launch the code related to a companion site.
	 * The companion site must be registered to the server.
	 * @param companionSite
	 * @return
	 * @throws ExecutorException
	 */
	public RunningTask exec(CompanionSite companionSite) throws ExecutorException;
	
	public void exec(CompanionSite companionSite, RunningTaskListener resultListener) throws ExecutorException;
	
	public RunningTask exec(CompanionSite companionSite, List<InputData> inputs) throws ExecutorException;
	
	public void exec(CompanionSite companionSite, List<InputData> inputs, RunningTaskListener resultListener) throws ExecutorException;
	
	public RunningTask exec(ThematicSite thematicSite) throws ExecutorException;
	
	public void exportInputData(List<InputData> inputs, FileOutputStream file, Format format);
	
	public void exportInputData(List<InputData> inputs, OutputStream outputStream, Format format);
	
	public List<InputData> importInputData(FileInputStream file, Format format);
	
	public List<InputData> importInputData(InputStream outputStream, Format format);	

	public void exportOutputData(List<OutputData> outputs, FileOutputStream file, Format format);
	
	public void exportOutputData(List<OutputData> outputs, OutputStream outputStream, Format format);

	public List<OutputData> importOutputData(FileInputStream file, Format format);
	
	public List<OutputData> importOutputData(InputStream outputStream, Format format);	
}
