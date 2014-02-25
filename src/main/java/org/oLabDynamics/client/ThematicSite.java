package org.oLabDynamics.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.write.ThematicSiteReadWrite;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;

public class ThematicSite extends ResourceSupport {
	
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
