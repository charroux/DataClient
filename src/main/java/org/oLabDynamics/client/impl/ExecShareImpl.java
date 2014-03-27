package org.oLabDynamics.client.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.core.DefaultLinkDiscoverer;
import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.client.ExecShareException;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.ExecShare.Format;
import org.oLabDynamics.client.ExecShare.PUBLICATION_MODE;
import org.oLabDynamics.client.data.Author;
import org.oLabDynamics.client.data.Code;
import org.oLabDynamics.client.data.CompanionSite;
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.data.Publication;
import org.oLabDynamics.client.data.ServerException;
import org.oLabDynamics.client.data.ThematicSite;
import org.oLabDynamics.client.exec.ExecutorException;
import org.oLabDynamics.client.exec.RunningTaskListener;
import org.oLabDynamics.client.exec.RunningTask;
import org.oLabDynamics.rest.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
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
public class ExecShareImpl<T> implements ExecShare<T>{
	
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

    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		String[] serverEntryPoints = connexionFactory.getServerEntryPoints();
		RestTemplate restTemplate = execShare.getRestTemplate();
		
		for(int i=0; i<serverEntryPoints.length; i++){
			ResponseEntity<Resource> response = restTemplate.exchange(serverEntryPoints[i], HttpMethod.GET, entity, Resource.class);
			HttpStatus statusCode = response.getStatusCode();
			if(statusCode != HttpStatus.OK){
				throw new ServerException("Unable to connect the server ! Check execAndShare.properties");
			}
			
			Resource resourceSupport = response.getBody();
	    	entryPoints.add(resourceSupport.getLinks());

		}
    	
	}
	
	public static ExecShare getInstance(){
		return execShare;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}

	ExecShareConnexionFactory execShareConnexionFactory;
	RestTemplate restTemplate;
	
	@Autowired
	public void setExecShareConnexionFactory(ExecShareConnexionFactory execShareConnexionFactory) {
		this.execShareConnexionFactory = execShareConnexionFactory;
	}
	
	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public ExecShareConnexionFactory getExecShareConnexionFactory() {
		return execShareConnexionFactory;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public List<T> prepare(Query query) throws Exception {
		
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
    	
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
    	
    	if(rel.equals("author")){
    		ParameterizedTypeReference<List<Author>> typeRef = new ParameterizedTypeReference<List<Author>>() {};
        	ResponseEntity<List<Author>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
        	HttpStatus httpStatus = resp.getStatusCode();
        	if(httpStatus.equals(HttpStatus.OK) == false){
        		System.out.println("errrrrreeeeeerrrrrr");
        	}
    		List respBody = resp.getBody();
    		return respBody;
    	} else if(rel.equals("publication")){
    		ParameterizedTypeReference<List<Publication>> typeRef = new ParameterizedTypeReference<List<Publication>>() {};
        	ResponseEntity<List<Publication>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    		List respBody = resp.getBody();
    		return respBody;
    	} else if(rel.equals("thematicSite")){
    		ParameterizedTypeReference<List<ThematicSite>> typeRef = new ParameterizedTypeReference<List<ThematicSite>>() {};
        	ResponseEntity<List<ThematicSite>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    		List respBody = resp.getBody();
    		return respBody;
    	}
    	
    	throw new Exception("Query contains a bad rel");
    	
	}

	public Link discoverLink(String rel) {
		return entryPoints.getLink(rel);
	}

	@Override
	public void publish(CompanionSite companionSite, PUBLICATION_MODE publicationMode) throws ExecShareException {
		
		companionSite.publish(publicationMode);
		
	}
	
	@Override
	public RunningTask exec(CompanionSite companionSite) throws ExecutorException {
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
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
			throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RunningTask exec(CompanionSite companionSite, List<InputData> inputs)
			throws ExecutorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exec(CompanionSite companionSite, List<InputData> inputs,
			RunningTaskListener resultListener) throws ExecutorException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void exportInputData(List<InputData> inputs, FileOutputStream file,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportInputData(List<InputData> inputs,
			OutputStream outputStream,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<InputData> importInputData(FileInputStream file,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InputData> importInputData(InputStream outputStream,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportOutputData(List<OutputData> outputs,
			FileOutputStream file,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportOutputData(List<OutputData> outputs,
			OutputStream outputStream,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<OutputData> importOutputData(FileInputStream file,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OutputData> importOutputData(InputStream outputStream,
			org.oLabDynamics.client.ExecShare.Format format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RunningTask exec(ThematicSite thematicSite) throws ExecutorException {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
