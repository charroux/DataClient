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
	
	public Configuration() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OperatingSystem getOperatingSystem() {
		return null;
	}

	public String getProgrammingLanguage() {
		return programmingLanguage;
	}
	
	public void setProgrammingLanguage(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	public Set<Library> getLibraries() {
		return null;
	}

	public void installOnThisMachine() throws UnsupportedConfigurationException{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		return "Configuration [programmingLanguage=" + programmingLanguage
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((programmingLanguage == null) ? 0 : programmingLanguage
						.hashCode());
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
		Configuration other = (Configuration) obj;
		if (programmingLanguage == null) {
			if (other.programmingLanguage != null)
				return false;
		} else if (!programmingLanguage.equals(other.programmingLanguage))
			return false;
		return true;
	}



	
	
	
}
