package org.oLabDynamics.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.write.PublicationReadWrite;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;

public class Author extends ResourceSupport {
	
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
		
		System.out.println("getPublications");
		
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
	
	public void save(){
		
/*		List<Publication> copyPublications = new ArrayList<Publication>();
		
		if(publications != null){	
			for(int i=0; i<publications.size(); i++){
				copyPublications.add(publications.get(i));
			}
			publications = null;
		}*/

		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = ((ExecShareImpl)ExecShareImpl.getInstance()).getExecShareConnexionFactory();
    	//RestTemplate restTemplate = ExecShare.getInstance().getRestTemplate();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        System.out.println(this);
        
		HttpEntity<Author> entity1 = new HttpEntity<Author>(this,headers);
		
		Link link = super.getLink("self");
		if(link == null){
			/*String className = this.getClass().getName();
			className = className.substring(className.lastIndexOf(".")+1);
			System.out.println(className);
			restTemplate.exchange(href, HttpMethod.POST, entity1, ResourceSupport.class);*/
		}
		
		String href = link.getHref();
		
		restTemplate.exchange(href, HttpMethod.PUT, entity1, ResourceSupport.class);
		
		if(publications != null){
			//publications = copyPublications;
			
			link = super.getLink("publications");
			if(link == null){
			}
			
			href = link.getHref();
			
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
