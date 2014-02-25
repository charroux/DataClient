package org.oLabDynamics.client;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.write.CodeReadWrite;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class OutputData extends ResourceSupport {
	
	String description;
	
	@JsonIgnore
	Code code;
	
	RestTemplate restTemplate;
	HttpEntity<String> entity;
	
	public OutputData(){
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "OutputData [description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		OutputData other = (OutputData) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	
	
}
