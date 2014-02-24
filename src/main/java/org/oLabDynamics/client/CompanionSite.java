package org.oLabDynamics.client;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.write.CodeReadWrite;
import org.oLabDynamics.client.write.PublicationReadWrite;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;


/**
 * Site
 */

public class CompanionSite extends ResourceSupport{
	
	@JsonIgnore
	List<ThematicSite> thematicSites = null;
	
	@JsonIgnore
	Publication publication = null;
	
	@JsonIgnore
	Code code = null;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	
	public CompanionSite(){
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
	
	boolean addThematicSite(ThematicSite thematicSite) {
		if(thematicSites == null){	
			thematicSites = this.getThematicSites(); 	// get from the server
			if(thematicSites == null){				// nothing at the server
				thematicSites = new ArrayList<ThematicSite>();
			}
		}
		if(thematicSites.contains(thematicSite) == false){
			thematicSites.add(thematicSite);
			return true;
		}
		return false;
		
	}
	
	public Publication getPublication() {
		if(publication != null){
			return publication;
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
		
		ResponseEntity<Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, Publication.class);
    	
		return response.getBody();
	}
	
	void setPublication(Publication publication) {
		this.publication = publication;
	}
	
	public List<ThematicSite> getThematicSites() {
		if(thematicSites != null){
			return thematicSites;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		if(link == null){
			return new ArrayList<ThematicSite>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<ThematicSite>> typeRef = new ParameterizedTypeReference<List<ThematicSite>>() {};
		ResponseEntity<List<ThematicSite>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
	}

	public Code getCode() {
		if(code != null){
			return code;
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
		
		ResponseEntity<Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, Code.class);
    	
		return response.getBody();
	}
	
	void setCode(Code code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "CompanionSite [thematicSites=" + thematicSites
				+ ", publication=" + publication + ", code=" + code + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((publication == null) ? 0 : publication.hashCode());
		result = prime * result
				+ ((thematicSites == null) ? 0 : thematicSites.hashCode());
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
		CompanionSite other = (CompanionSite) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (publication == null) {
			if (other.publication != null)
				return false;
		} else if (!publication.equals(other.publication))
			return false;
		if (thematicSites == null) {
			if (other.thematicSites != null)
				return false;
		} else if (!thematicSites.equals(other.thematicSites))
			return false;
		return true;
	}
	
	

	
}
