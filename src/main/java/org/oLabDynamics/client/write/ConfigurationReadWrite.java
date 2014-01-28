package org.oLabDynamics.client.write;

import java.util.HashSet;
import java.util.Set;

import org.oLabDynamics.client.Configuration;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Caches data from the server: get data from the server only at the first call of a getter method.
 * @author charroux
 *
 */
public class ConfigurationReadWrite extends Configuration {
	
	public ConfigurationReadWrite() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ConfigurationReadWrite(String programmingLanguage) {
		super();
		super.setProgrammingLanguage(programmingLanguage);
	}

	@Override
	public String toString() {
		return "ConfigurationReadWrite [toString()=" + super.toString() + "]";
	}

	
}
