package org.oLabDynamics.client.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.ExecShare.PUBLICATION_MODE;
import org.oLabDynamics.client.impl.ExecShareImpl;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.rest.Resource;
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
public class ThematicSite extends Resource {
	
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
    	
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
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

	public List<CompanionSite> getCompanionSites() {
		if(companionSites != null){
			return companionSites;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		if(link == null){
			return new ArrayList<CompanionSite>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<CompanionSite>> typeRef = new ParameterizedTypeReference<List<CompanionSite>>() {};
		ResponseEntity<List<CompanionSite>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
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
	
	public void publish(PUBLICATION_MODE publicationMode){
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        if(super.getLinks().size() == 0){	// this is a new thematicSite 
        	
            String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new thematicSite : new id, links, rel...
        	ResponseEntity<ThematicSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, ThematicSite.class);
        	Resource resource = response.getBody();
  
        	super.add(resource.getLinks());      	
        }
        
        String href = super.getLink("self").getHref();
		HttpEntity<ThematicSite> entity = new HttpEntity<ThematicSite>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, ThematicSite.class);
		
		if(companionSites != null){
			
			for(int i=0; i<companionSites.size(); i++){
				CompanionSite companionSite = companionSites.get(i);
				
				 if(companionSite.getLinks().size() == 0){	// this is a new companionSite 
			        	
			            String className = companionSite.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new companionSite : new id, links, rel...
			        	ResponseEntity<CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, CompanionSite.class);
			        	Resource resource = response.getBody();
			  
			        	companionSite.add(resource.getLinks());      	
			        }
			}
			
			href = super.getLink("companionSites").getHref();
			
			HttpEntity<CompanionSite[]> entities = new HttpEntity<CompanionSite[]>(companionSites.toArray(new CompanionSite[0]),headers);
			
			ParameterizedTypeReference<CompanionSite[]> typeRef = new ParameterizedTypeReference<CompanionSite[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
	}

	@Override
	public String toString() {
		return "ThematicSite [companionSites=" + companionSites + "]";
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
