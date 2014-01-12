package org.oLabDynamics.client.write;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.oLabDynamics.client.Author;
import org.oLabDynamics.client.Code;
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

public class PublicationReadWrite extends Publication {
	
	public enum Type{
		WorkingPaper,
		PublishedPaper
	}

	public enum PublicationMode{
		ForContactsOnly
	}
	
	Type type;
	
	String title;
	List<Author> authors = new ArrayList<Author>();
	List<AuthorReadWrite> removedAuthors = new ArrayList<AuthorReadWrite>();	// maintient a jour la liste des auteurs supprimées pour ne pas les obtenir à nouveau quand on demande au serveur la liste

	private boolean resetAuthors;	// true si la liste des auteurs a été réinitilisée (il ne faut plus tenir compte de celle du serveur)

	private boolean resetReferenceImplementation;

	private Code referenceImplementation;
	
	public PublicationReadWrite(){
		super();
	}
	
	public PublicationReadWrite(String title, Type type) {
		super();
		this.title = title;
		this.type = type;
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
	public boolean addAuthor(AuthorReadWrite author, int authorOrdering){
		if(authors.contains(author) == false){
			authors = super.getAuthors();	// récupère la liste pour pouvoir placer l'auteur à sa place (possible récupération de la liste venant du serveur)
			authors.add(authorOrdering-1, author);
			author.addPublication(this);
			
			// "/publication/{id}/authors"	"/author/{id}/publications"
			return true;
		}
		return false;
	}
	
	public boolean removeAuthor(AuthorReadWrite author){
		boolean removed = authors.remove(author);
		if(removed == true){
			removedAuthors.add(author);
			author.removePublication(this);
		}
		return removed;
	}
	
	/**
	 * 
	 * @return all authors: those present at the server (if they exist and if they do not have been removed by {@link #removeAuthor removeAuthor}) 
	 * plus all added authors with {@link #addAuthor addAuthor}.
	 * Notice that {@link #setAuthors setAuthors} resets the entire list so that the list is no more gotten from the server.
	 */
	public List<Author> getAuthors(){
		if(resetAuthors == false){	// la liste des auteurs n'a pas été réinitialisée, on peut prendre celle du serveur
			List<Author> authorsFromTheServer = super.getAuthors();
			if(authorsFromTheServer == null){
				return authors;
			}
			authorsFromTheServer.removeAll(removedAuthors);
			authors.addAll(authorsFromTheServer);
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
		resetAuthors = true;	// la liste des auteurs a été réinitilisée (il ne faut plus tenir compte de celle du serveur)
	}
	
	public CompanionSiteReadWrite getCompanionSite(){
		CompanionSiteReadWrite companionSite = (CompanionSiteReadWrite)super.getCompanionSite();
		return companionSite;
	}
	
	/**
	 * 
	 * @return the reference implementation (from the server or a new implementation if {@link #setReferenceImplementation setReferenceImplementation} has been used 
	 */
	public Code getReferenceImplementation(){
		Code code = super.getReferenceImplementation();
		return code;
	}

	public void setReferenceImplementation(CodeReadWrite referenceImplementation) {
		resetReferenceImplementation = true;
		this.referenceImplementation = referenceImplementation;
		referenceImplementation.setPublication(this);
	}

	public void publishPublication(PublicationMode publicationMode) {
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
        
        org.oLabDynamics.model.dto1.Pub p = new org.oLabDynamics.model.dto1.Pub();
        p.setTitle("blabla");
        org.oLabDynamics.model.dto1.Author a = new org.oLabDynamics.model.dto1.Author();
        p.getAuthors().add(a);
        
		HttpEntity<org.oLabDynamics.model.dto1.Publication> entity = new HttpEntity<org.oLabDynamics.model.dto1.Publication>(p,headers);
		
		//System.out.println(href);
		
		ResponseEntity<ResourceSupport> response = restTemplate.exchange(href, HttpMethod.POST, entity, ResourceSupport.class);
		System.out.println(response.getBody());
		
/*		link = super.getLink("self");
		if(link == null){
			restTemplate.exchange(href, HttpMethod.POST, entity, Code.class);
		} else {
			String href = link.getHref();
		}*/
		
	}
	
	

	@Override
	public String toString() {
		return "Publication [type=" + type + ", title=" + title + ", authors="
				+ authors + ", removedAuthors=" + removedAuthors
				+ ", resetAuthors=" + resetAuthors
				+ ", resetReferenceImplementation="
				+ resetReferenceImplementation + ", toString()="
				+ super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		PublicationReadWrite other = (PublicationReadWrite) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		return true;
	}








	
}
