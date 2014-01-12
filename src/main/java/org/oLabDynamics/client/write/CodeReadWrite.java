package org.oLabDynamics.client.write;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class CodeReadWrite extends Code {
	
	public CodeReadWrite(){
		super();
	}

	void setPublication(PublicationReadWrite publication) {
		
	}

	@Override
	public String toString() {
		return "CodeReadWrite [toString()=" + super.toString() + "]";
	}
	

	
	
}
