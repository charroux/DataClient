package org.oLabDynamics.client;

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
 * @author charroux
 *
 * @param <T>
 */
@Component
public class ExecShareImpl<T> implements ExecShare<T>{
	
	//static Hashtable<String, Link> relToLink = new Hashtable<String, Link>();
	
	static ExecShareImpl execShare;
	static ResourceSupport entryPoint;
	
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
		
		String serverEntryPoint = connexionFactory.getServerEntryPoint();
		RestTemplate restTemplate = execShare.getRestTemplate();
		
		ResponseEntity<ResourceSupport> response = restTemplate.exchange(serverEntryPoint, HttpMethod.GET, entity, ResourceSupport.class);
		HttpStatus statusCode = response.getStatusCode();
		if(statusCode != HttpStatus.OK){
			throw new ServerException("Unable to connect the server ! Check execAndShare.properties");
		}
		
    	entryPoint = response.getBody();
    	
    	/*List<Link> links = entryPoint.getLinks();
    	Link link;
    	for(int i=0; i<links.size(); i++){
    		link = links.get(i);
    		relToLink.put(link.getRel(), link);
    	}*/
    	
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

	/*public static Hashtable<String, Link> getRelToLink() {
		return relToLink;
	}*/
	
/*	public static Link discoverLink(String rel){
		return entryPoint.getLink(rel);
	}*/

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

	@Override
	public Link discoverLink(String rel) {
		return entryPoint.getLink(rel);
	}

	@Override
	public void persist(ResourceSupport resourceSupport) {

		if(resourceSupport instanceof Author){
			((Author)resourceSupport).save();
		} else if(resourceSupport instanceof Publication){
			((Publication)resourceSupport).save();
		} 
		
	}

}
