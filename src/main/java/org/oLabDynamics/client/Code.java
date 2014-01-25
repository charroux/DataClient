package org.oLabDynamics.client;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class Code extends ResourceSupport {
	
	String description;
	
	@JsonIgnore
	RestTemplate restTemplate;
	
	@JsonIgnore
	HttpEntity<String> entity;
	
	public Code(){
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
	
	public List<InputData> getInputs(){
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		if(link == null){
			return new ArrayList<InputData>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<InputData>> typeRef = new ParameterizedTypeReference<List<InputData>>() {};
		ResponseEntity<List<InputData>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
	}
	
/*	public InputData getReferenceInputData() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();

		ResponseEntity<InputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, InputData.class);
    	
		return response.getBody();
	}*/

/*	public Publication getPublication() {
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
	}*/

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void export(File file) {
		// TODO Auto-generated method stub	
	}

	public Set<Configuration> getConfigurations() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<Set<Configuration>> typeRef = new ParameterizedTypeReference<Set<Configuration>>() {};
		ResponseEntity<Set<Configuration>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);

		return response.getBody();
	}
	
	public Set<Configuration> getConfigurations(Class operatingSystem) {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		String href = link.getHref();
		
		href = href + "?operatingSystemName=" + operatingSystem.getName();
		
		ParameterizedTypeReference<Set<Configuration>> typeRef = new ParameterizedTypeReference<Set<Configuration>>() {};
		ResponseEntity<Set<Configuration>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);

		return response.getBody();
	}
	
	@Override
	public String toString() {
		return "Code [description=" + description + ", toString()="
				+ super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
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
		Code other = (Code) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}


}
