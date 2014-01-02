package org.oLabDynamics.client;

import java.util.HashSet;
import java.util.Set;

import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

public class Configuration extends ResourceSupport {
	
	RestTemplate restTemplate;
	HttpEntity<String> entity;
	
	String programmingLanguage;
	
	public OperatingSystem getOperatingSystem() {
		return ...;
	}

	public String getProgrammingLanguage() {
		return programmingLanguage;
	}

	public Set<Library> getLibraries() {
		return libraries;
	}

	@Override
	public String toString() {
		return "Configuration [operatingSystem=" + operatingSystem
				+ ", programmingLanguage=" + programmingLanguage
				+ ", libraries=" + libraries + "]";
	}
	
	
}
