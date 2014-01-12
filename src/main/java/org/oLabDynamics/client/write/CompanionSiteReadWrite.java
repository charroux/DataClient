package org.oLabDynamics.client.write;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;


/**
 * Site
 */

public class CompanionSiteReadWrite extends CompanionSite{
	
	public CompanionSiteReadWrite(){
		super();
	}

	@Override
	public String toString() {
		return "CompanionSiteReadWrite [toString()=" + super.toString() + "]";
	}

	
	
}
