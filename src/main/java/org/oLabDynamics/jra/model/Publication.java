package org.oLabDynamics.jra.model;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.ConnectionError;
import jra.JRA.PublicationMode;
import jra.JRAException;
import jra.model.CompanionSite;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.oLabDynamics.jra.ExecShareConnectionFactory;
import org.oLabDynamics.jra.ExecShareImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Benoit Charroux
 *
 */

@XmlRootElement(name = "Publication")
@XmlType(namespace = Publication.ATOM_NAMESPACE, propOrder = {"publicationType","authors"})
public class Publication extends jra.model.Publication implements Resource{
	
	ResourceSupport resourceSupport = new ResourceSupport();
	
	@JsonIgnore
	List<jra.model.Author> authors = null;
	
	@JsonIgnore
	CompanionSite companionSite;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	

	public Publication(){
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
	
	@Override
	public void setAuthors(List<jra.model.Author> authors) {
		this.authors = authors;
	}



	/**
	 * 
	 * @param author
	 * @param authorOrdering begin at 1
	 */
	@Override
	public boolean addAuthor(jra.model.Author author, int authorOrdering){
		if(authors == null){
			authors = this.getAuthors();	// try to get authors form the server
			if(authors == null){
				authors = new ArrayList<jra.model.Author>();
			}
		}
		if(authors.contains(author) == false){
			authors.add(authorOrdering-1, author);
			((org.oLabDynamics.jra.model.Author)author).addPublication(this);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return all authors: those present at the server (if they exist and if they do not have been removed by {@link #removeAuthor removeAuthor}) 
	 * plus all added authors with {@link #addAuthor addAuthor}.
	 * Notice that {@link #setAuthors setAuthors} resets the entire list so that the list is no more gotten from the server.
	 */
	//@XmlElement(name = "authors", required = true)
	@XmlAnyElement
	@Override
	public List<jra.model.Author> getAuthors(){
		if(authors != null){
			return authors;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){		// L'héritage de ResourceSupport n'a pas été initialisé par Jackson ou manuellement
			return null;
		}
		
		String href = link.getHref();
		
		ParameterizedTypeReference<List<org.oLabDynamics.jra.model.Author>> typeRef = new ParameterizedTypeReference<List<org.oLabDynamics.jra.model.Author>>() {};
		ResponseEntity<List<org.oLabDynamics.jra.model.Author>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
		HttpStatus status = response.getStatusCode();
		if(status == HttpStatus.OK){
			List<jra.model.Author> authors = new ArrayList<jra.model.Author>();
			authors.addAll(response.getBody());
			return authors;
		} else {
			throw new ConnectionError();
		}
	}
	
	@XmlTransient
	public CompanionSite getCompanionSite(){
		if(companionSite != null){
			return companionSite;
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

		ResponseEntity<org.oLabDynamics.jra.model.CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.CompanionSite.class);
    	
		return response.getBody();
	}
	
	public void setCompanionSite(CompanionSite companionSite) {
		if(companionSite != null){
			this.companionSite = companionSite;
			companionSite.setPublication(this);
		}
	}
	
	/**
	 * 
	 * @return the reference implementation (from the server or a new implementation if {@link #setReferenceImplementation setReferenceImplementation} has been used 
	 */
/*	public Code getReferenceImplementation(){
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();
			ResponseEntity<Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, Code.class);
    	
		return response.getBody();
	}*/

	@XmlElement(name = "publicationType", required = true)
	public PublicationType getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(PublicationType publicationType) {
		this.publicationType = publicationType;
	}

/*	public void publish() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
		
		String href = execShare.discoverLink(currentMethodName).getHref();
		Link link = relToLink.get(currentMethodName);
		String href = link.getHref();	
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = ((ExecShareImpl)ExecShareImpl.getInstance()).getExecShareConnexionFactory();
    	RestTemplate restTemplate = execShare.getRestTemplate();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        HttpEntity<Publication> entity = new HttpEntity<Publication>(this,headers);
		
		ResponseEntity<Resource> response = restTemplate.exchange(href, HttpMethod.POST, entity, Resource.class);
		
	}*/
	
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
        
        this.publishWithCompanionSite(publicationMode, headers);
	}
	
	void publishWithCompanionSite(PublicationMode publicationMode, HttpHeaders headers){
		
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
        
        if(getLinks().size() == 0){	// this is a new publication 
        	
        	String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new Publication : new id, links, rel...
        	ResponseEntity<org.oLabDynamics.jra.model.Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Publication.class);
        	Resource resource = response.getBody();
  
        	add(resource.getLinks());      	
        }
        
        String href = getLink("self").getHref();
		HttpEntity<org.oLabDynamics.jra.model.Publication> entity = new HttpEntity<org.oLabDynamics.jra.model.Publication>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, org.oLabDynamics.jra.model.Publication.class);
		
		if(authors != null){
			
			for(int i=0; i<authors.size(); i++){
				org.oLabDynamics.jra.model.Author author = (org.oLabDynamics.jra.model.Author)authors.get(i);
				
				 if(author.getLinks().size() == 0){	// this is a new author 
			        	
			            String className = author.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new author : new id, links, rel...
			        	ResponseEntity<org.oLabDynamics.jra.model.Author> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Author.class);
			        	//Resource resource = response.getBody();
			        	org.oLabDynamics.jra.model.Author resource = response.getBody();
			  
			        	author.add(resource.getLinks());      	
			        }
			}
			
			href = getLink("authors").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.Author[]> entities = new HttpEntity<org.oLabDynamics.jra.model.Author[]>(authors.toArray(new org.oLabDynamics.jra.model.Author[0]),headers);
			
			ParameterizedTypeReference<org.oLabDynamics.jra.model.Author[]> typeRef = new ParameterizedTypeReference<org.oLabDynamics.jra.model.Author[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
		
		if(companionSite != null){
			
			if(((org.oLabDynamics.jra.model.CompanionSite)companionSite).getLinks().size() == 0){	// this is a new companionSite
				
				String className = companionSite.getClass().getName();
		    	className = className.substring(className.lastIndexOf(".")+1);
		    	className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
					
			     href = execShare.discoverLink(className).getHref() + "/new";
			     entity = new HttpEntity(headers);
		        	
		         // get a new companionSite : new id, links, rel...
			     ResponseEntity<org.oLabDynamics.jra.model.CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.CompanionSite.class);
			     Resource resource = response.getBody();
			  
			     ((org.oLabDynamics.jra.model.CompanionSite)companionSite).add(resource.getLinks()); 			     
			}
			
			href = getLink("companionSite").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.CompanionSite> entity1 = new HttpEntity<org.oLabDynamics.jra.model.CompanionSite>(((org.oLabDynamics.jra.model.CompanionSite)companionSite),headers);
			
			restTemplate.exchange(href, HttpMethod.PUT, entity1, org.oLabDynamics.jra.model.CompanionSite.class);
			
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

	@Override
	public String toString() {
		return "Publication [authors=" + authors + ", companionSite="
				+ companionSite + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result
				+ ((companionSite == null) ? 0 : companionSite.hashCode());
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
		Publication other = (Publication) obj;
		if (authors == null) {
			if (other.authors != null)
				return false;
		} else if (!authors.equals(other.authors))
			return false;
		if (companionSite == null) {
			if (other.companionSite != null)
				return false;
		} else if (!companionSite.equals(other.companionSite))
			return false;
		return true;
	}

	
	
	
	
}
