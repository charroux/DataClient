package org.oLabDynamics.client.write;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.Author;
import org.oLabDynamics.client.Publication;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;

/**
 * Caches data from the server: get data from the server only at the first call of a getter method.
 * @author charroux
 *
 */
public class AuthorReadWrite extends Author {

	@JsonIgnore
	private List<Publication> publications = null;
	
	public AuthorReadWrite(){
		super();
	}

	public AuthorReadWrite(String firstName, String lastName) {
		this();
		super.setFirstName(firstName);
		super.setLastName(lastName);
	}

	@Override
	public List<Publication> getPublications(){
		if(publications == null){	// la liste des publications n'a pas été réinitialisée, on peut prendre celle du serveur
			List<Publication> publicationsFromTheServer = super.getPublications();
			if(publicationsFromTheServer == null){
				return publications = new ArrayList<Publication>();
			} else {
				return publications = publicationsFromTheServer;
			}
		}
		return publications;
	}

	void addPublication(PublicationReadWrite publication) {
		if(publications == null){	// la liste des publications n'a pas été réinitialisée, on peut prendre celle du serveur
			publications = this.getPublications();
		}
		this.publications.add(publication);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		AuthorReadWrite other = (AuthorReadWrite) obj;
		if (publications == null) {
			if (other.publications != null)
				return false;
		} else if (!publications.equals(other.publications))
			return false;
		return true;
	}

	

}
