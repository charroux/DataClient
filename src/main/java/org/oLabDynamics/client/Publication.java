package org.oLabDynamics.client;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.write.AuthorReadWrite;
import org.oLabDynamics.client.write.CompanionSiteReadWrite;
import org.oLabDynamics.client.write.PublicationReadWrite;
import org.oLabDynamics.rest.ResourceSupport;
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
public class Publication extends ResourceSupport {
	
	public enum PublicationType{
		ARTICLE,
		WORKING_PAPER
	}
	
	PublicationType publicationType;
	String title;

	@JsonIgnore
	List<Author> authors = null;
	
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
    	
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        entity = new HttpEntity<String>(headers);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	/**
	 * 
	 * @param author
	 * @param authorOrdering begin at 1
	 */
	public boolean addAuthor(Author author, int authorOrdering){
		if(authors == null){
			authors = this.getAuthors();	// try to get authors form the server
			if(authors == null){
				authors = new ArrayList<Author>();
			}
		}
		if(authors.contains(author) == false){
			authors.add(authorOrdering-1, author);
			author.addPublication(this);
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
	public List<Author> getAuthors(){
		if(authors != null){
			return authors;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		if(link == null){		// L'héritage de ResourceSupport n'a pas été initialisé par Jackson ou manuellement
			return null;
		}
		
		String href = link.getHref();
		
		ParameterizedTypeReference<List<Author>> typeRef = new ParameterizedTypeReference<List<Author>>() {};
		ResponseEntity<List<Author>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
		HttpStatus status = response.getStatusCode();
		if(status == HttpStatus.OK){
			return response.getBody();
		} else {
			throw new ServerException();
		}
	}
	
	public CompanionSite getCompanionSite(){
		if(companionSite != null){
			return companionSite;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();

		ResponseEntity<CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, CompanionSite.class);
    	
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

	public PublicationType getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(PublicationType publicationType) {
		this.publicationType = publicationType;
	}

	public void publishPublication() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
		
		String href = execShare.discoverLink(currentMethodName).getHref();
		/*Link link = relToLink.get(currentMethodName);
		String href = link.getHref();*/	
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = ((ExecShareImpl)ExecShareImpl.getInstance()).getExecShareConnexionFactory();
    	RestTemplate restTemplate = execShare.getRestTemplate();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        HttpEntity<Publication> entity = new HttpEntity<Publication>(this,headers);
		
		ResponseEntity<ResourceSupport> response = restTemplate.exchange(href, HttpMethod.POST, entity, ResourceSupport.class);
		
	}
	
	void save(){
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        if(super.getLinks().size() == 0){	// this is a new publication 
        	
        	String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new Publication : new id, links, rel...
        	ResponseEntity<Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, Publication.class);
        	ResourceSupport resource = response.getBody();
  
        	super.add(resource.getLinks());      	
        }
        
        String href = super.getLink("self").getHref();
		HttpEntity<Publication> entity = new HttpEntity<Publication>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, Publication.class);
		
		if(authors != null){
			
			for(int i=0; i<authors.size(); i++){
				Author author = authors.get(i);
				
				 if(author.getLinks().size() == 0){	// this is a new author 
			        	
			            String className = author.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new author : new id, links, rel...
			        	ResponseEntity<Author> response = restTemplate.exchange(href, HttpMethod.GET, entity, Author.class);
			        	ResourceSupport resource = response.getBody();
			  
			        	author.add(resource.getLinks());      	
			        }
			}
			
			href = super.getLink("authors").getHref();
			
			HttpEntity<Author[]> entities = new HttpEntity<Author[]>(authors.toArray(new Author[0]),headers);
			
			ParameterizedTypeReference<Author[]> typeRef = new ParameterizedTypeReference<Author[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
		
		if(companionSite != null){
			
			if(companionSite.getLinks().size() == 0){	// this is a new companionSite
				
				String className = companionSite.getClass().getName();
		    	className = className.substring(className.lastIndexOf(".")+1);
		    	className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
					
			     href = execShare.discoverLink(className).getHref() + "/new";
			     entity = new HttpEntity(headers);
		        	
		         // get a new companionSite : new id, links, rel...
			     ResponseEntity<CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, CompanionSite.class);
			     ResourceSupport resource = response.getBody();
			  
			     companionSite.add(resource.getLinks()); 			     
			}
			
			href = super.getLink("companionSite").getHref();
			
			HttpEntity<CompanionSite> entity1 = new HttpEntity<CompanionSite>(companionSite,headers);
			
			restTemplate.exchange(href, HttpMethod.PUT, entity1, CompanionSite.class);
			
		}		
	}

	@Override
	public String toString() {
		return "Publication [publicationType=" + publicationType + ", title="
				+ title + ", authors=" + authors + ", companionSite="
				+ companionSite + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result
				+ ((companionSite == null) ? 0 : companionSite.hashCode());
		result = prime * result
				+ ((publicationType == null) ? 0 : publicationType.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		if (publicationType != other.publicationType)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	
	
}
