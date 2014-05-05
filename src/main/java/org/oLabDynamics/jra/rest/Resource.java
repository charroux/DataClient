package org.oLabDynamics.jra.rest;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author Benoit Charroux
 *
 */

public class Resource extends org.springframework.hateoas.ResourceSupport{
	
	String typeOfResource;

	@XmlTransient
	public String getTypeOfResource() {
		return typeOfResource;
	}

	public void setTypeOfResource(String typeOfResource) {
		this.typeOfResource = typeOfResource;
	}

	@Override
	public String toString() {
		return "ResourceSupport [typeOfResource=" + typeOfResource
				+ ", toString()=" + super.toString() + "]";
	}
	
	
	
}
