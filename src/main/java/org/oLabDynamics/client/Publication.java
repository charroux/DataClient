package org.oLabDynamics.client;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.oLabDynamics.client.Publication.PublicationMode;
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
	List<Author> removedAuthors = new ArrayList<Author>();	// maintient a jour la liste des auteurs supprimées pour ne pas les obtenir à nouveau quand on demande au serveur la liste
	
	RestTemplate restTemplate;
	HttpEntity<String> entity;

	private boolean resetAuthors;	// true si la liste des auteurs a été réinitilisée (il ne faut plus tenir compte de celle du serveur)

	private boolean resetReferenceImplementation;

	private Code referenceImplementation;
	
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
	
	public Publication(String title, Type type) {
		this();
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
	public boolean addAuthor(Author author, int authorOrdering){
		if(authors.contains(author) == false){
			authors = this.getAuthors();	// récupère la liste pour pouvoir placer l'auteur à sa place (possible récupération de la liste venant du serveur)
			authors.add(authorOrdering-1, author);
			author.addPublication(this);
			return true;
		}
		return false;
	}
	
	public boolean removeAuthor(Author author){
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
			class Local {};
			Method currentMethod = Local.class.getEnclosingMethod();
			String currentMethodName = currentMethod.getName();
			String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
			Link link = super.getLink(attributeName);
			if(link == null){		// L'héritage de ResourceSupport n'a pas été initialisé par Jackson ou manuellement
				return authors;
			}
			
			String href = link.getHref();
			
			ParameterizedTypeReference<List<Author>> typeRef = new ParameterizedTypeReference<List<Author>>() {};
			ResponseEntity<List<Author>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
			HttpStatus status = response.getStatusCode();
			if(status == HttpStatus.OK){
				List<Author> authorsFromTheServer = response.getBody();
				authorsFromTheServer.removeAll(removedAuthors);
				authors.addAll(authorsFromTheServer);
			} else {
				throw new ServerException();
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
			authors.get(i).addPublication(this);
		}
		resetAuthors = true;	// la liste des auteurs a été réinitilisée (il ne faut plus tenir compte de celle du serveur)
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
	public Code getReferenceImplementation(){
		if(resetReferenceImplementation == false){
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
	    	
			referenceImplementation =  response.getBody();
		}
		return referenceImplementation;
	}

	public void setReferenceImplementation(Code referenceImplementation) {
		resetReferenceImplementation = true;
		this.referenceImplementation = referenceImplementation;
		referenceImplementation.setPublication(this);
	}

	public void publish(PublicationMode publicationMode) {
		Link link = super.getLink("self");
		if(link == null){
			restTemplate.exchange(href, HttpMethod.POST, entity, Code.class);
		} else {
			String href = link.getHref();
		}
		
	}
	
	@Override
	public String toString() {
		return "Publication [type=" + type + ", title=" + title + "]";
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
		Publication other = (Publication) obj;
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
