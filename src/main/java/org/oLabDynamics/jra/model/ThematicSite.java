package org.oLabDynamics.jra.model;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.model.CompanionSite;
import jra.JRA.PublicationMode;
import jra.JRAException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.oLabDynamics.jra.ExecShareConnectionFactory;
import org.oLabDynamics.jra.ExecShareImpl;
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

public class ThematicSite extends jra.model.ThematicSite implements Resource{
	
	ResourceSupport resourceSupport = new ResourceSupport();
	
	@JsonIgnore
	List<CompanionSite> companionSites = null;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	
	public ThematicSite(){
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

	public void addCompanionSite(CompanionSite companionSite) {
		if(companionSites == null){	// la liste des companionSites n'a pas été réinitialisée, on peut prendre celle du serveur
			companionSites = this.getCompanionSites();
			if(companionSites == null){
				companionSites = new ArrayList<CompanionSite>();
			}
		}
		if(companionSites.contains(companionSite) == false){
			companionSites.add(companionSite);
			companionSite.addThematicSite(this);
		}
	}

	@XmlElement(name = "companionSites", required = true)
	public List<CompanionSite> getCompanionSites() {
		if(companionSites != null){
			return companionSites;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return new ArrayList<CompanionSite>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<org.oLabDynamics.jra.model.CompanionSite>> typeRef = new ParameterizedTypeReference<List<org.oLabDynamics.jra.model.CompanionSite>>() {};
		ResponseEntity<List<org.oLabDynamics.jra.model.CompanionSite>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		List<CompanionSite> sites = new ArrayList<CompanionSite>(response.getBody());
		
		return sites;
	}
	
	public void publishThematicSite() {
		/*class Local {};
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
        
        HttpEntity<ThematicSite> entity = new HttpEntity<ThematicSite>(this,headers);
		
		ResponseEntity<ResourceSupport> response = restTemplate.exchange(href, HttpMethod.POST, entity, ResourceSupport.class);*/
		
	}
	
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
        
        this.publish(publicationMode, headers);
	}
	
	void publish(PublicationMode publicationMode, HttpHeaders headers) {
		// TODO Auto-generated method stub
			
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
        
        if(getLinks().size() == 0){	// this is a new thematicSite 
        	
            String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new thematicSite : new id, links, rel...
        	ResponseEntity<org.oLabDynamics.jra.model.ThematicSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.ThematicSite.class);
        	Resource resource = response.getBody();
  
        	add(resource.getLinks());      	
        }
        
        String href = getLink("self").getHref();
		HttpEntity<org.oLabDynamics.jra.model.ThematicSite> entity = new HttpEntity<org.oLabDynamics.jra.model.ThematicSite>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, org.oLabDynamics.jra.model.ThematicSite.class);
		
		if(companionSites != null){
			
			for(int i=0; i<companionSites.size(); i++){
				org.oLabDynamics.jra.model.CompanionSite companionSite = (org.oLabDynamics.jra.model.CompanionSite) companionSites.get(i);
				
				 if(companionSite.getLinks().size() == 0){	// this is a new companionSite 
			        	
			            String className = companionSite.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new companionSite : new id, links, rel...
			        	ResponseEntity<org.oLabDynamics.jra.model.CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.CompanionSite.class);
			        	Resource resource = response.getBody();
			  
			        	companionSite.add(resource.getLinks());      	
			        }
			}
			
			href = getLink("companionSites").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.CompanionSite[]> entities = new HttpEntity<org.oLabDynamics.jra.model.CompanionSite[]>(companionSites.toArray(new org.oLabDynamics.jra.model.CompanionSite[0]),headers);
			
			ParameterizedTypeReference<org.oLabDynamics.jra.model.CompanionSite[]> typeRef = new ParameterizedTypeReference<org.oLabDynamics.jra.model.CompanionSite[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
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
		return "ThematicSite [companionSites=" + companionSites
				+ ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((companionSites == null) ? 0 : companionSites.hashCode());
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
		ThematicSite other = (ThematicSite) obj;
		if (companionSites == null) {
			if (other.companionSites != null)
				return false;
		} else if (!companionSites.equals(other.companionSites))
			return false;
		return true;
	}

	


	

}
