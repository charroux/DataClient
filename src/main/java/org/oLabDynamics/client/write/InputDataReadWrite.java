package org.oLabDynamics.client.write;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.springframework.hateoas.Link;
import org.oLabDynamics.client.InputData;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class InputDataReadWrite extends InputData {
	
	public InputDataReadWrite(){
		super();
	}

	@Override
	public String toString() {
		return "InputDataReadWrite [toString()=" + super.toString() + "]";
	}

	
	
}
