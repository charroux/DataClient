package org.oLabDynamics.jra;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jra.ConnectionError;
import jra.ConnectionFactory;
import jra.JRA;
import jra.JRAException;
import jra.Query;
import jra.exec.RunningTask;
import jra.exec.RunningTaskListener;
import jra.model.Code;
import jra.model.CompanionSite;
import jra.model.InputData;
import jra.model.InputData.Kind;
import jra.model.Publication;
import jra.model.ThematicSite;

import org.oLabDynamics.jra.model.Author;
import org.oLabDynamics.jra.rest.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.core.DefaultLinkDiscoverer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

/**
 * According to the principles of REST, next states from a given state are discovered dynamically:
 * a next state is given by a link (containing a URI and a relation ship name: rel).
 * 
 * When multiple states are allowed from a state: the one wish if chosen is given by a "convention de nomage" :
 * to change the current state, the end user must choose one of the getter method, then the corresponding attribut name (discovered by 
 * code introspection) gives the rel of the next step.
 * 
 * Only the main entry point is needed : it is given by the property execAndShare.serverEntryPoint
 * contained into the property file named : execAndShare.properties
 * 
 * @author Benoit Charroux
 *
 * @param <T>
 */
@Component
public class ExecShareImpl<T> implements JRA<T>{
	
	class CodeWithInputData{
		CompanionSite companionSite;
		InputData[] inputs;
		public CompanionSite getCompanionSite() {
			return companionSite;
		}
		public void setCompanionSite(CompanionSite companionSite) {
			this.companionSite = companionSite;
		}
		public InputData[] getInputs() {
			return inputs;
		}
		public void setInputs(InputData[] inputs) {
			this.inputs = inputs;
		}
		
	}
	
	ExecutorService executorService = Executors.newCachedThreadPool();

	static ExecShareImpl execShare;
	static Resource entryPoints = new Resource();
	
	static{
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		execShare = (ExecShareImpl)context.getBean("execShare");
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);

    	ExecShareConnectionFactory connectionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connectionFactory.getUserName() + ":" + connectionFactory.getPassword();
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		String infrastructureForReproducibilityURL = connectionFactory.getInfrastructureForReproducibilityURL();
		
		RestTemplate restTemplate = execShare.getRestTemplate();
		
		ResponseEntity<Resource> response = restTemplate.exchange(infrastructureForReproducibilityURL, HttpMethod.GET, entity, Resource.class);
		HttpStatus statusCode = response.getStatusCode();
		if(statusCode != HttpStatus.OK){
			throw new ConnectionError("Unable to connect the Infrastructure for Reproducibility " + infrastructureForReproducibilityURL + ". Check execAndShare.properties");
		}
		
		Resource resourceSupport = response.getBody();
    	entryPoints.add(resourceSupport.getLinks());
    	
    	String libraryForReproducibilityURL = connectionFactory.getLibraryForReproducibilityURL();
    	response = restTemplate.exchange(libraryForReproducibilityURL, HttpMethod.GET, entity, Resource.class);
		statusCode = response.getStatusCode();
		if(statusCode != HttpStatus.OK){
			throw new ConnectionError("Unable to connect the Library for Reproducibility " + infrastructureForReproducibilityURL + ". Check execAndShare.properties");
		}
		
		resourceSupport = response.getBody();
    	entryPoints.add(resourceSupport.getLinks());
    	
	}
	
	public static JRA getInstance(){
		return execShare;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	ExecShareConnectionFactory execShareConnectionFactory;
	RestTemplate restTemplate;
	Marshaller marshaller;
    Unmarshaller unmarshaller;
	
	@Autowired
	public void setExecShareConnexionFactory(ExecShareConnectionFactory execShareConnectionFactory) {
		this.execShareConnectionFactory = execShareConnectionFactory;
	}
	
	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	
	
	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public ExecShareConnectionFactory getExecShareConnexionFactory() {
		return execShareConnectionFactory;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public List<T> prepare(Query query) throws JRAException {
		
		String rel = query.getDataType();
		
		Link link = discoverLink(rel);
    	if(link == null){	
    	} 
    	
    	String href = link.getHref();
    	
    	String queryString = query.format();
    	
    	if(queryString.equals("") == false){
    		href = href + "?query=" + queryString;
    	}
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
    	
    	if(rel.equals("author")){
    		ParameterizedTypeReference<List<org.oLabDynamics.jra.model.Author>> typeRef = new ParameterizedTypeReference<List<org.oLabDynamics.jra.model.Author>>() {};
        	ResponseEntity<List<org.oLabDynamics.jra.model.Author>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
        	HttpStatus httpStatus = resp.getStatusCode();
        	if(httpStatus.equals(HttpStatus.OK) == false){
        		System.out.println("errrrrreeeeeerrrrrr");
        	}
    		List respBody = resp.getBody();
    		return respBody;
    	} else if(rel.equals("publication")){
    		ParameterizedTypeReference<List<org.oLabDynamics.jra.model.Publication>> typeRef = new ParameterizedTypeReference<List<org.oLabDynamics.jra.model.Publication>>() {};
        	ResponseEntity<List<org.oLabDynamics.jra.model.Publication>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    		List respBody = resp.getBody();
    		return respBody;
    	} else if(rel.equals("thematicSite")){
    		ParameterizedTypeReference<List<ThematicSite>> typeRef = new ParameterizedTypeReference<List<ThematicSite>>() {};
        	ResponseEntity<List<ThematicSite>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    		List respBody = resp.getBody();
    		return respBody;
    	}
    	
    	throw new JRAException("Query contains a bad rel");
    	
	}

	public Link discoverLink(String rel) {
		return entryPoints.getLink(rel);
	}

	@Override
	public void publish(CompanionSite companionSite, PublicationMode publicationMode) throws JRAException {
		
		companionSite.publish(publicationMode);
		
	}
	
	private List<InputData> convertDataToString(List<InputData> inputs) throws JRAException{
		
		List<InputData> inputsAsString = new ArrayList<InputData>();
		
		InputData inputData;
		String dataString;
		for(int i=0; i<inputs.size(); i++){
			inputData = inputs.get(i);
		
			Kind kind = inputData.getKind();
			if(kind == Kind.MATRIX){
				dataString = "{";
				Object[][] matrix = (Object[][])inputData.getData();
				for(int j=0; j<matrix.length; j++){
					dataString = dataString + "{";
					for(int k=0; k<matrix[j].length; k++){
						dataString = dataString + matrix[j][k].toString();
						if(k < matrix[j].length-1){
							dataString = dataString + ",";
						}
					}
					dataString = dataString + "}";
				}
				dataString = dataString + "}";
				try {
					InputData inputClone = inputData.clone();
					inputClone.setData(dataString);
					inputsAsString.add(inputClone);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		return inputsAsString;
	}
	
	@Override
	public RunningTask exec(CompanionSite companionSite) throws JRAException {
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        CodeWithInputData codeWithInputData = new CodeWithInputData();
        codeWithInputData.setCompanionSite(companionSite);
        Code code = companionSite.getCode();
        InputData[] inputs = code.getInputs().toArray(new InputData[0]);
        codeWithInputData.setInputs(inputs);
        
		HttpEntity<CodeWithInputData> entities = new HttpEntity<CodeWithInputData>(codeWithInputData, headers);
		
		String href = this.discoverLink("executor").getHref();
		
		ResponseEntity<RunningTaskImpl> response = restTemplate.exchange(href, HttpMethod.PUT, entities, RunningTaskImpl.class);
		
		RunningTaskImpl runningTask = response.getBody();
		
		runningTask.setExecShare(execShare);
		
		//runningTask.setExecutor(executor);
		
		//runningTask.setRestTemplate(restTemplate);
		
		runningTask.getRunningState();
		
		return runningTask;
	}

	@Override
	public void exec(CompanionSite companionSite, RunningTaskListener resultListener)
			throws JRAException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RunningTask exec(CompanionSite companionSite, List<InputData> inputs) throws JRAException {
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        CodeWithInputData codeWithInputData = new CodeWithInputData();
        codeWithInputData.setCompanionSite(companionSite);
        
        List<InputData> inputDataAsString = this.convertDataToString(inputs);
        
        codeWithInputData.setInputs(inputDataAsString.toArray(new InputData[0]));
        
		HttpEntity<CodeWithInputData> entities = new HttpEntity<CodeWithInputData>(codeWithInputData, headers);
		
		String href = this.discoverLink("executor").getHref();
		
		ResponseEntity<RunningTaskImpl> response = restTemplate.exchange(href, HttpMethod.PUT, entities, RunningTaskImpl.class);
		
		RunningTaskImpl runningTask = response.getBody();
		
		runningTask.setExecShare(execShare);
		
		//runningTask.setExecutor(executor);
		
		//runningTask.setRestTemplate(restTemplate);
		
		runningTask.getRunningState();
		
		return runningTask;
		
	}

	@Override
	public void exec(CompanionSite companionSite, List<InputData> inputs,
			RunningTaskListener resultListener) throws JRAException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void exportation(CompanionSite companionSite, OutputStream outputStream, JRA.Format format)  throws JRAException{
		
		this.exportation(companionSite, outputStream, format, JRA.ImportExportOptions.COMPANION_SITE);
		
	}
	
	@Override
	public void exportation(CompanionSite companionSite, OutputStream outputStream, JRA.Format format, JRA.ImportExportOptions options) throws JRAException {

		if(outputStream == null){
			throw new JRAException("outputStream should not be null");
		}
		

		
		//List<InputData> inputs = code.getInputs();
		
		switch(format){
		case XML:
			try {
				Marshaller marshaller = execShare.getMarshaller();
				
				if(options == JRA.ImportExportOptions.COMPANION_SITE){
					
					marshaller.marshal(companionSite, new StreamResult(outputStream));
					
				} else if(options == JRA.ImportExportOptions.CODE_WITH_DATA){
					
					Code code = companionSite.getCode();
					marshaller.marshal(code, new StreamResult(outputStream));
					
				} 
				
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	throw new JRAException(e.getLocalizedMessage());
			} finally {
	            if(outputStream != null) {
	            	try {
	            		outputStream.close();
					} catch (IOException e) {
					}
	            }
	        }
			break;
		default:
			break;
		}
		
	}



	@Override
	public Object importation(CompanionSite companionSite, InputStream inputStream, JRA.Format format)  throws JRAException{
		
		return this.importation(companionSite, inputStream, format, ImportExportOptions.COMPANION_SITE);
	
	}
	
	@Override
	public Object importation(CompanionSite companionSite, InputStream inputStream, JRA.Format format, JRA.ImportExportOptions options) throws JRAException {

		if(inputStream == null){
			throw new JRAException("inputStream should not be null");
		}
		
		
		switch(format){
		case XML:
			try {
				
				Unmarshaller unmarshaller = execShare.getUnmarshaller();
				return unmarshaller.unmarshal(new StreamSource(inputStream));
	            
	        } catch (Exception e) {
				throw new JRAException(e.getLocalizedMessage());
			} finally {
	            if (inputStream != null) {
	            	try {
	            		inputStream.close();
					} catch (IOException e) {
					}
	            }
	        }
		default:
			break;
		}
		
		return null;
		
	}

	@Override
	public RunningTask exec(ThematicSite thematicSite) throws JRAException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionFactory getConnectionFactory() {
		return execShareConnectionFactory;
	}




	

}
