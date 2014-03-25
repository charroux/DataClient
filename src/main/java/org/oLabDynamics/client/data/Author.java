package org.oLabDynamics.client.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.ExecShare.PUBLICATION_MODE;
import org.oLabDynamics.client.impl.ExecShareImpl;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.rest.Resource;
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
public class Author extends Resource {
	
	@JsonIgnore
	private ArrayList<Publication> publications = null;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	
	String firstName;
	String lastName;

	
	public Author(){
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
		restTemplate = execShare.getRestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        entity = new HttpEntity<String>(headers);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	boolean addPublication(Publication publication) {
		if(publications == null){	
			publications = this.getPublications(); 	// get from the server
			if(publications == null){				// nothing at the server
				publications = new ArrayList<Publication>();
			}
		}
		if(publications.contains(publication) == false){
			publications.add(publication);
			return true;
		}
		return false;
	}

	public ArrayList<Publication> getPublications() {
		
		if(publications != null){	
			return publications;
		}
		
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		if(link == null){
			return new ArrayList<Publication>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<ArrayList<Publication>> typeRef = new ParameterizedTypeReference<ArrayList<Publication>>() {};
		ResponseEntity<ArrayList<Publication>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
	}
	
	public void publish(PUBLICATION_MODE publicationMode){
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        if(super.getLinks().size() == 0){	// this is a new author 
        	
            String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new author : new id, links, rel...
        	ResponseEntity<Author> response = restTemplate.exchange(href, HttpMethod.GET, entity, Author.class);
        	Resource resource = response.getBody();
  
        	super.add(resource.getLinks());      	
        }
        
        String href = super.getLink("self").getHref();
		HttpEntity<Author> entity = new HttpEntity<Author>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, Author.class);
		
		if(publications != null){
			
			for(int i=0; i<publications.size(); i++){
				Publication publication = publications.get(i);
				
				 if(publication.getLinks().size() == 0){	// this is a new publication 
			        	
			            String className = publication.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new publication : new id, links, rel...
			        	ResponseEntity<Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, Publication.class);
			        	Resource resource = response.getBody();
			  
			        	publication.add(resource.getLinks());      	
			        }
			}
			
			href = super.getLink("publications").getHref();
			
			HttpEntity<Publication[]> entities = new HttpEntity<Publication[]>(publications.toArray(new Publication[0]),headers);
			
			ParameterizedTypeReference<Publication[]> typeRef = new ParameterizedTypeReference<Publication[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
	}

	@Override
	public String toString() {
		return "Author [firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((publications == null) ? 0 : publications.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (publications == null) {
			if (other.publications != null)
				return false;
		} else if (!publications.equals(other.publications))
			return false;
		return true;
	}

}
