package org.oLabDynamics.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;

public class ExecShare<T> {

	public List<T> prepare(Query query) throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	String auth = "temporary" + ":" + "temporary";
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
		HttpEntity<String> entity = new HttpEntity<String>(headers);
    	ResponseEntity<ResourceSupport> response = restTemplate.exchange("http://localhost:8081/Data/sites/entryPoint", HttpMethod.GET, entity, ResourceSupport.class);
    	ResourceSupport entryPoint = response.getBody();
    	
    	String rel = query.getRel();
    	Link link = entryPoint.getLink(rel);
    	
    	if(link == null){
    		
    	} 
    	
    	String href = link.getHref();
    	System.out.println("href=" + href);
    	String queryString = query.format();
    	
    	href = href + "?query=" + queryString;
    	
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
