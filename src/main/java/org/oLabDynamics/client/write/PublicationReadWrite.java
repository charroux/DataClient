package org.oLabDynamics.client.write;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.oLabDynamics.client.Author;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.client.Publication;
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
 * Caches data from the server: get data from the server only at the first call of a getter method.
 * @author charroux
 *
 */
public class PublicationReadWrite extends Publication {
	
	private List<Author> authors = null;
	
	private CompanionSite companionSite = null;
	
	public PublicationReadWrite(){
		super();
	}
	
	public PublicationReadWrite(PublicationType publicationType, String title) {
		super();
		super.setPublicationType(publicationType);
		super.setTitle(title);
	}

	/**
	 * 
	 * @param author
	 * @param authorOrdering begin at 1
	 */
	public boolean addAuthor(AuthorReadWrite author, int authorOrdering){
		if(authors == null){
			authors = this.getAuthors();	// try to get authors form the server
		}
		if(authors.contains(author) == false){
			authors.add(authorOrdering-1, author);
			author.addPublication(this);
			return true;
		}
		return false;
	}
	
	public boolean removeAuthor(AuthorReadWrite author){
		if(authors == null){
			authors = this.getAuthors();	// try to get authors form the server
		}
		return authors.remove(author);
	}
	
	/**
	 * 
	 * @return all authors: first call, those present at the server  
	 * plus all added authors with {@link #addAuthor addAuthor}.
	 * Notice that {@link #setAuthors setAuthors} resets the entire list so that the list is no more gotten from the server.
	 */
	@Override
	public List<Author> getAuthors(){
		if(authors == null){	// la liste des auteurs n'a pas été réinitialisée, on peut prendre celle du serveur
			List<Author> authorsFromTheServer = super.getAuthors();
			if(authorsFromTheServer == null){
				return authors = new ArrayList<Author>();
			} else {
				return authors = authorsFromTheServer;
			}

		}
		return authors;
	}


	/**
	 * Replace all the authors, the given list should be ordered before calling this method if authors ordering is important. 
	 * Notice that it resets the entire list so that the list is no more gotten from the server.
	 * @param authors
	 */
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
		int i=0;
		while(i < authors.size()){
			((AuthorReadWrite)authors.get(i)).addPublication(this);
		}
	}
	
	@Override
	public CompanionSite getCompanionSite(){
		if(companionSite == null){
			companionSite = super.getCompanionSite();
		}
		return companionSite;
	}
	
	public void setCompanionSite(CompanionSiteReadWrite companionSite) {
		this.companionSite = companionSite;
		companionSite.setPublication(this);
	}

	public void publishPublication() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		
		Hashtable<String, Link> relToLink = ExecShare.getRelToLink();
		Link link = relToLink.get(currentMethodName);
		String href = link.getHref();	
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = ExecShare.getInstance().getExecShareConnexionFactory();
    	RestTemplate restTemplate = ExecShare.getInstance().getRestTemplate();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        HttpEntity<PublicationReadWrite> entity = new HttpEntity<PublicationReadWrite>(this,headers);
		
		ResponseEntity<ResourceSupport> response = restTemplate.exchange(href, HttpMethod.POST, entity, ResourceSupport.class);
		System.out.println(response.getBody());
		
	}

	@Override
	public String toString() {
		return "PublicationReadWrite [authors=" + authors + ", companionSite="
				+ companionSite + "]";
	}
		
}
