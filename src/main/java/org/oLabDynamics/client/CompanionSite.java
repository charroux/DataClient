package org.oLabDynamics.client;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;


/**
 * Site
 */

public class CompanionSite extends ResourceSupport{
	
	RestTemplate restTemplate;
	HttpEntity<String> entity;
	
	public CompanionSite(){
		restTemplate = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	String auth = "temporary" + ":" + "temporary";
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        entity = new HttpEntity<String>(headers);
	}
	
	public Publication getPublication() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		String href = link.getHref();
		
		ResponseEntity<Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, Publication.class);
    	
		return response.getBody();
	}
	
	public Code getCode() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		String href = link.getHref();
		
		ResponseEntity<Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, Code.class);
    	
		return response.getBody();
	}

	@Override
	public String toString() {
		return "CompanionSite [toString()=" + super.toString() + "]";
	}
	
	
	
}
