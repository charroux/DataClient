package org.oLabDynamics.client;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class Publication extends ResourceSupport {
	
	public enum PublicationType{
		ARTICLE,
		WORKING_PAPER
	}
	
	PublicationType publicationType;
	String title;

	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;

	public Publication(){
		ExecShare execShare = ExecShare.getInstance();
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
	 * @return all authors: those present at the server (if they exist and if they do not have been removed by {@link #removeAuthor removeAuthor}) 
	 * plus all added authors with {@link #addAuthor addAuthor}.
	 * Notice that {@link #setAuthors setAuthors} resets the entire list so that the list is no more gotten from the server.
	 */
	public List<Author> getAuthors(){
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

	@Override
	public String toString() {
		return "Publication [publicationType=" + publicationType + ", title="
				+ title + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
