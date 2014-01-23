package org.oLabDynamics.client.write;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.client.Configuration;
import org.oLabDynamics.client.InputData;
import org.oLabDynamics.client.Publication;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class CodeReadWrite extends Code {
	
	@JsonIgnore
	Publication publication;
	
	@JsonIgnore
	CompanionSite companionSite;
	
	@JsonIgnore
	InputData referenceInputData;
	
	@JsonIgnore
	List<InputData> inputs;
	
	@JsonIgnore
	Set<Configuration> configurations;
	
	public CodeReadWrite(){
		super();
	}

	public CodeReadWrite(String description) {
		super();
		super.setDescription(description);
	}

	void setPublication(PublicationReadWrite publication) {
		this.publication = publication;
	}
	
	@Override
	public Publication getPublication() {
		if(publication == null){
			publication = super.getPublication();
		}
		return publication;
	}
	
	@Override
	public CompanionSite getCompanionSite(){
		if(companionSite == null){
			companionSite = super.getCompanionSite();
		}
		return companionSite;
	}

	void setCompanionSite(CompanionSiteReadWrite companionSite) {
		this.companionSite = companionSite;
	}
	
	

/*	@Override
	public String toString() {
		return "CodeReadWrite [publication=" + publication + ", toString()="
				+ super.toString() + "]";
	}*/

	
	
}
