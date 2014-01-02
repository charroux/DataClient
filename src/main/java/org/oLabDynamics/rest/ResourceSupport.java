package org.oLabDynamics.rest;

public class ResourceSupport extends org.springframework.hateoas.ResourceSupport{
	
	String typeOfResource;

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
