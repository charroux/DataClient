package jra;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import jra.exec.RunningTask;
import jra.exec.RunningTaskListener;
import jra.model.CompanionSite;
import jra.model.InputData;
import jra.model.ThematicSite;


/**
 * 
 * Main interface to {@link org.oLabDynamics.client.JRA#prepare(Query) retrieve} any data from Exec & Share data model,
 * export or import this model, {@link org.oLabDynamics.client.JRA#publish(Resource, PublicationMode) publish} data to the Exec & Share portal
 * and {@link org.oLabDynamics.client.JRA#exec(CompanionSite) launch} code associated to a companion site or a thematic site.
 * 
 * Edit the file execAndShare.properties to set connection information suitable to access Exec & Share Library for Reproducibility.
 * 
 * Exec & Share as a Library for Reproducibility can retrieve all information form any other libraries.
 * Péciser le niveau de service.
 * 
 * Même chose pour Infrastructure for Reproducibility ?
 * 
 * @author Benoit Charroux
 *
 * @param <T>
 */
public interface JRA<T> {
		
	/**
	 * Export format for data.
	 * CODE_FORMAT: compatible with, and only with, the data format of the companionSite's code as it has 
	 * been provided by the author of the code; use this format only to download code and data for launching code 
	 * out of the Exec & Share infrastructure.  
	 * @author charroux
	 *
	 */
	public enum Format{
		JSON,
		XML,
		CODE_FORMAT
	}
	
	public enum ImportExportOptions{
		CODE_WITH_DATA,
		COMPANION_SITE
	}
	
	/**
	 * Publication for evaluation (private access) or for public publication to the Exec & Share portal 
	 * @author charroux
	 *
	 */
	public enum PublicationMode{
		IN_PRIVATE_FOR_EVALUATION,
		PUBLIC;
	}
	
	/**
	 * Query the Exec & Share data model: retrieve an author from his (her) name, publication by title...
	 * @param query
	 * @return
	 * @throws JRAException
	 */
	public List<T> prepare(Query query) throws JRAException;
	
	/**
	 * 
	 * @param companionSite
	 * @param publicationMode
	 * @throws JRAException
	 */
	public void publish(CompanionSite companionSite, PublicationMode publicationMode) throws JRAException;
	
	/**
	 * Launch the code related to a companion site: the companion site must be prior registered to the server by using {@link org.oLabDynamics.client.JRA#publish(Resource, PublicationMode) publish}.
	 * 
	 * @param companionSite
	 * @return An access to the running task for getting results, cancellation...
	 * @throws JRAException
	 */
	public RunningTask exec(CompanionSite companionSite) throws JRAException;
	
	public void exec(CompanionSite companionSite, RunningTaskListener resultListener) throws JRAException;
	
	public RunningTask exec(CompanionSite companionSite, List<InputData> inputs) throws JRAException;
	
	public void exec(CompanionSite companionSite, List<InputData> inputs, RunningTaskListener resultListener) throws JRAException;
	
	public RunningTask exec(ThematicSite thematicSite) throws JRAException;
	
	public void exportation(CompanionSite companionSite, OutputStream outputStream, JRA.Format format) throws JRAException;

	public void exportation(CompanionSite companionSite, OutputStream outputStream, JRA.Format format, JRA.ImportExportOptions options) throws JRAException;

	public Object importation(CompanionSite companionSite, InputStream inputStream, JRA.Format format) throws JRAException;	
	
	public Object importation(CompanionSite companionSite, InputStream inputStream, JRA.Format format, JRA.ImportExportOptions options) throws JRAException;	

	/**
	 * @return information about Exec & Share version and connection URL.
	 */
	public ConnectionFactory getConnectionFactory();
}
