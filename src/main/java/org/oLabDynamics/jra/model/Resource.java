package org.oLabDynamics.jra.model;

import java.util.List;

import org.springframework.hateoas.Link;

public interface Resource {
	
	public String getTypeOfResource();

	public void setTypeOfResource(String typeOfResource);

	public Link getId();
	
	public void add(Link link);
	
	public void add(Iterable<Link> links);
	
	public boolean hasLinks();
	
	public boolean hasLink(String rel);
	
	public List<Link> getLinks();
	
	public void removeLinks();
	
	public Link getLink(String rel);
}
