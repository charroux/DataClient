package org.oLabDynamics.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

@Component
public class ExecShare<T> {
	
	static Hashtable<String, Link> relToLink = new Hashtable<String, Link>();
	
	static ExecShare execShare;
	
	static{
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		execShare = (ExecShare)context.getBean("execShare");
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);

    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		String serverEntryPoint = connexionFactory.getServerEntryPoint();
		RestTemplate restTemplate = execShare.getRestTemplate();
		
		ResponseEntity<ResourceSupport> response = restTemplate.exchange(serverEntryPoint, HttpMethod.GET, entity, ResourceSupport.class);
		HttpStatus statusCode = response.getStatusCode();
		if(statusCode != HttpStatus.OK){
			throw new ServerException("Unable to connect the server ! Check execAndShare.properties");
		}
		
    	ResourceSupport entryPoint = response.getBody();
    	
    	List<Link> links = entryPoint.getLinks();
    	Link link;
    	for(int i=0; i<links.size(); i++){
    		link = links.get(i);
    		relToLink.put(link.getRel(), link);
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

	public static Hashtable<String, Link> getRelToLink() {
		return relToLink;
	}

	public List<T> prepare(Query query) throws Exception {
		
/*    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		String serverEntryPoint = connexionFactory.getServerEntryPoint();

		ResponseEntity<ResourceSupport> response = restTemplate.exchange(serverEntryPoint, HttpMethod.GET, entity, ResourceSupport.class);
    	ResourceSupport entryPoint = response.getBody();
    	
    	String rel = query.getRel();
    	Link link = entryPoint.getLink(rel);*/
		
		String rel = query.getRel();
		Link link = relToLink.get(rel);
    	
    	if(link == null){
    		
    	} 
    	
    	String href = link.getHref();
    	System.out.println("href=" + href);
    	String queryString = query.format();
    	
    	href = href + "?query=" + queryString;
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
    	
    	if(rel.equals("list of authors")){
    		ParameterizedTypeReference<List<Author>> typeRef = new ParameterizedTypeReference<List<Author>>() {};
        	ResponseEntity<List<Author>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    		List respBody = resp.getBody();
    		return respBody;
    	} else if(rel.equals("list of publications")){
    		ParameterizedTypeReference<List<Publication>> typeRef = new ParameterizedTypeReference<List<Publication>>() {};
        	ResponseEntity<List<Publication>> resp = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    		List respBody = resp.getBody();
    		return respBody;
    	}
    	
    	throw new Exception("Query contains a bad rel");
    	
	}

}
