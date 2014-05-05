package org.oLabDynamics.jra.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.model.Code;
import jra.JRA.PublicationMode;
import jra.JRAException;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.oLabDynamics.jra.ExecShareConnectionFactory;
import org.oLabDynamics.jra.ExecShareImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Benoit Charroux
 *
 */

@XmlRootElement(name = "OutputData")
@XmlType(namespace="oLabDynamics")	//, propOrder = {"description"})
public class OutputData extends jra.model.OutputData implements Resource{
	
	ResourceSupport resourceSupport = new ResourceSupport();
	
	@JsonIgnore
	Code code;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	
	public OutputData(){
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
		restTemplate = execShare.getRestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        entity = new HttpEntity<String>(headers);
	}
	
	//@XmlTransient
	public Code getCode() {
		if(code != null){
			return code;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();
		
		ResponseEntity<org.oLabDynamics.jra.model.Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Code.class);
    	
		return response.getBody();
	}
	
	public void setCode(Code code) {
		this.code = code;
	}
	
	/**
	 * Restricted access: can not be used without special authorization.
	 * Use {@link org.oLabDynamics.client.JRA#publish(CompanionSite, PUBLICATION_MODE) publish} instead.
	 * @param publicationMode
	 * @throws JRAException
	 */
	public void publish(PublicationMode publicationMode) throws JRAException{
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        this.publish(publicationMode, headers);
        
	}
	
	void publish(PublicationMode publicationMode, HttpHeaders headers){
		
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
        
        if(getLinks().size() == 0){	// this is a new output 
        	
        	String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new output : new id, links, rel...
        	ResponseEntity<org.oLabDynamics.jra.model.OutputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.OutputData.class);
        	Resource resource = response.getBody();
  
        	add(resource.getLinks());      	
        }
        
        String href = getLink("self").getHref();
		HttpEntity<org.oLabDynamics.jra.model.OutputData> entity = new HttpEntity<org.oLabDynamics.jra.model.OutputData>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, org.oLabDynamics.jra.model.OutputData.class);		
		
		if(code != null){
			
			if(((org.oLabDynamics.jra.model.Code)code).getLinks().size() == 0){	// this is a new code
				
				String className = code.getClass().getName();
		    	className = className.substring(className.lastIndexOf(".")+1);
		    	className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
					
			     href = execShare.discoverLink(className).getHref() + "/new";
			     entity = new HttpEntity(headers);
		        	
		         // get a new code : new id, links, rel...
			     ResponseEntity<org.oLabDynamics.jra.model.Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Code.class);
			     Resource resource = response.getBody();
			  
			     ((org.oLabDynamics.jra.model.Code)code).add(resource.getLinks()); 			     
			}
			
			href = getLink("code").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.Code> entity1 = new HttpEntity<org.oLabDynamics.jra.model.Code>(((org.oLabDynamics.jra.model.Code)code),headers);
			
			restTemplate.exchange(href, HttpMethod.PUT, entity1, org.oLabDynamics.jra.model.Code.class);
			
		}
	}

	@Override
	public String toString() {
		return "OutputData [toString()=" + super.toString() + "]";
	}

	@Override
	@XmlTransient
	public String getTypeOfResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTypeOfResource(String typeOfResource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@JsonIgnore
	public Link getId() {
		return resourceSupport.getId();
	}

	@Override
	public void add(Link link) {
		resourceSupport.add(link);
	}

	@Override
	public void add(Iterable<Link> links) {
		resourceSupport.add(links);
	}

	@Override
	public boolean hasLinks() {
		return resourceSupport.hasLinks();
	}

	@Override
	public boolean hasLink(String rel) {
		return resourceSupport.hasLink(rel);
	}

	@Override
	@XmlTransient
	@JsonProperty("links")
	public List<Link> getLinks() {
		return resourceSupport.getLinks();
	}
	
	public void setLinks(List<Link> links){
		resourceSupport.add(links);
	}

	@Override
	public void removeLinks() {
		resourceSupport.removeLinks();
	}

	@Override
	public Link getLink(String rel) {
		return resourceSupport.getLink(rel);
	}
	
}
