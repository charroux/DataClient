package org.oLabDynamics.client.write;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.oLabDynamics.client.Author;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;

public class AuthorReadWrite extends Author {

	
	public AuthorReadWrite(){
		super();
	}

	public AuthorReadWrite(String firstName, String lastName) {
		this();
	}

	void addPublication(PublicationReadWrite publication) {
		
	}

	void removePublication(PublicationReadWrite publication) {
		
	}

	@Override
	public String toString() {
		return "AuthorReadWrite [toString()=" + super.toString() + "]";
	}

	
	
	

}
