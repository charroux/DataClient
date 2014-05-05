package org.oLabDynamics.jra.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.JRA.PublicationMode;
import jra.JRAException;
import jra.model.Publication;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.oLabDynamics.jra.ExecShareConnectionFactory;
import org.oLabDynamics.jra.ExecShareImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;

/**
 * 
 * @author Benoit Charroux
 *
 */
@XmlRootElement(name = "Author")
@XmlType(namespace="oLabDynamics")
public class Author extends jra.model.Author implements Resource{
	
	ResourceSupport resourceSupport = new ResourceSupport();
	
	@JsonIgnore
	private ArrayList< jra.model.Publication> publications = null;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	
	public Author(){
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

	public boolean addPublication( jra.model.Publication publication) {
		if(publications == null){	
			publications = this.getPublications(); 	// get from the server
			if(publications == null){				// nothing at the server
				publications = new ArrayList< jra.model.Publication>();
			}
		}
		if(publications.contains(publication) == false){
			publications.add(publication);
			return true;
		}
		return false;
	}

	@XmlTransient
	public ArrayList<jra.model.Publication> getPublications() {
		
		if(publications != null){	
			return publications;
		}
		
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return new ArrayList< jra.model.Publication>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<ArrayList<org.oLabDynamics.jra.model.Publication>> typeRef = new ParameterizedTypeReference<ArrayList<org.oLabDynamics.jra.model.Publication>>() {};
		ResponseEntity<ArrayList<org.oLabDynamics.jra.model.Publication>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		ArrayList<jra.model.Publication> authors = new ArrayList<jra.model.Publication>();
		authors.addAll(response.getBody());
		return authors;
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
		
        if(getLinks().size() == 0){	// this is a new author 
        	
            String className = this.getClass().getSuperclass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new author : new id, links, rel...
        	ResponseEntity<org.oLabDynamics.jra.model.Author> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Author.class);
        	Resource resource = response.getBody();
  
        	add(resource.getLinks());      	
        }
        
        String href = getLink("self").getHref();
		HttpEntity<org.oLabDynamics.jra.model.Author> entity = new HttpEntity<org.oLabDynamics.jra.model.Author>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, org.oLabDynamics.jra.model.Author.class);
		
		if(publications != null){
			
			for(int i=0; i<publications.size(); i++){
				 org.oLabDynamics.jra.model.Publication publication = (org.oLabDynamics.jra.model.Publication) publications.get(i);
				
				 if(publication.getLinks().size() == 0){	// this is a new publication 
			        	
			            String className = publication.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new publication : new id, links, rel...
			        	ResponseEntity<org.oLabDynamics.jra.model.Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Publication.class);
			        	//Resource resource = response.getBody();
			        	Resource resource = response.getBody();
			  
			        	publication.add(resource.getLinks());      	
			        }
			}
			
			href = getLink("publications").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.Publication[]> entities = new HttpEntity<org.oLabDynamics.jra.model.Publication[]>(publications.toArray(new org.oLabDynamics.jra.model.Publication[0]),headers);
			
			ParameterizedTypeReference<org.oLabDynamics.jra.model.Publication[]> typeRef = new ParameterizedTypeReference<org.oLabDynamics.jra.model.Publication[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
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
