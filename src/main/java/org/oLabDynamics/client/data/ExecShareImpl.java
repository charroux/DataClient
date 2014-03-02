package org.oLabDynamics.client.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.core.DefaultLinkDiscoverer;
import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.ExecShare.Format;
import org.oLabDynamics.client.exec.ExecutorException;
import org.oLabDynamics.client.exec.ResultListener;
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
		Code code;
		InputData[] inputs;
		public Code getCode() {
			return code;
		}
		public void setCode(Code code) {
			this.code = code;
		}
		public InputData[] getInputs() {
			return inputs;
		}
		public void setInputs(InputData[] inputs) {
			this.inputs = inputs;
		}
		
	}
	
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
		
		String rel = query.getRel();
		
		Link link = discoverLink(rel);
    	if(link == null){	
    	} 
    	
    	String href = link.getHref();
    	
    	String queryString = query.format();
    	
    	href = href + "?query=" + queryString;
    	
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
	public void publish(Resource resourceSupport) {

		if(resourceSupport instanceof Author){
			
			((Author)resourceSupport).publish();
			
		} else if(resourceSupport instanceof Publication){
			
			((Publication)resourceSupport).publish();
			
		} else if(resourceSupport instanceof CompanionSite){
			
			((CompanionSite)resourceSupport).publish();
			
		} else if(resourceSupport instanceof Code){
			
			((Code)resourceSupport).publish();
			
		} else if(resourceSupport instanceof InputData){
			
			((InputData)resourceSupport).publish();
			
		} else if(resourceSupport instanceof OutputData){
			
			((OutputData)resourceSupport).publish();
			
		} else if(resourceSupport instanceof ThematicSite){
			
			((ThematicSite)resourceSupport).publish();
			
		} 
		
	}

	@Override
	public RunningTask exec(Code code) throws ExecutorException {
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        CodeWithInputData codeWithInputData = new CodeWithInputData();
        codeWithInputData.setCode(code);
        InputData[] inputs = code.getInputs().toArray(new InputData[0]);
        codeWithInputData.setInputs(inputs);
        
		HttpEntity<CodeWithInputData> entities = new HttpEntity<CodeWithInputData>(codeWithInputData, headers);
		
		String href = this.discoverLink("exec").getHref();
		
		ResponseEntity<RunningTask> response = restTemplate.exchange(href, HttpMethod.PUT, entities, RunningTask.class);
		
		RunningTask runningTask = response.getBody();
		
		return runningTask;
	}

	@Override
	public void exec(Code code, ResultListener resultListener)
			throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RunningTask exec(Code code, List<InputData> inputs)
			throws ExecutorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exec(Code code, List<InputData> inputs,
			ResultListener resultListener) throws ExecutorException {
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

	

	

}
