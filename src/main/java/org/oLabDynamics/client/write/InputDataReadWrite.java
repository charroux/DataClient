package org.oLabDynamics.client.write;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.Code;
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

public class InputDataReadWrite extends InputData {
	
	@JsonIgnore
	Code code;
	
	public InputDataReadWrite(){
		super();
	}

	@Override
	public String toString() {
		return "InputDataReadWrite [toString()=" + super.toString() + "]";
	}
	
	@Override
	public Code getCode(){
		if(code == null){	// la liste des publications n'a pas été réinitialisée, on peut prendre celle du serveur
			code = super.getCode();
		}
		return code;
	}

	void setCode(CodeReadWrite code) {
		this.code = code;
	}
	
}
