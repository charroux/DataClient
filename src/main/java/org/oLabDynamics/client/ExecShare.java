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
 * Main interface to {@link org.oLabDynamics.client.ExecShare#prepare(Query) retrieve} any data from Exec & Share data model,
 * export or import this model, {@link org.oLabDynamics.client.ExecShare#publish(Resource, PUBLICATION_MODE) publish} data to the Exec & Share portal
 * and {@link org.oLabDynamics.client.ExecShare#exec(CompanionSite) launch} code associated to a companion site or a thematic site.
 * 
 * @author Benoit Charroux
 *
 * @param <T>
 */
public interface ExecShare<T> {
		
	/**
	 * Export format for Exec & Share data 
	 * @author charroux
	 *
	 */
	public enum Format{
		JSON
	}
	
	/**
	 * Publication for evaluation (private access) or for public publication to the Exec & Share portal 
	 * @author charroux
	 *
	 */
	public enum PUBLICATION_MODE{
		IN_PRIVATE_FOR_EVALUATION,
		PUBLIC;
	}
	
	/**
	 * Query the Exec & Share data model: retrieve an author from his (her) name, publication by title...
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<T> prepare(Query query) throws Exception;
	
	/**
	 * 
	 * @param companionSite
	 * @param publicationMode
	 * @throws ExecShareException
	 */
	public void publish(CompanionSite companionSite, PUBLICATION_MODE publicationMode) throws ExecShareException;
	
	/**
	 * Launch the code related to a companion site: the companion site must be prior registered to the server by using {@link org.oLabDynamics.client.ExecShare#publish(Resource, PUBLICATION_MODE) publish}.
	 * 
	 * @param companionSite
	 * @return An access to the running task for getting results, cancellation...
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
