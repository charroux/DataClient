package org.oLabDynamics.jra.model;

import java.lang.reflect.Method;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.model.Code;
import jra.model.InputData;
import jra.model.OutputData;
import jra.model.Publication;
import jra.model.ThematicSite;
import jra.JRA.PublicationMode;
import jra.JRAException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.oLabDynamics.jra.ExecShareConnectionFactory;
import org.oLabDynamics.jra.ExecShareImpl;
import org.oLabDynamics.jra.model.Author;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;


/**
 * 
 * @author Benoit Charroux
 *
 */

@XmlRootElement(name = "CompanionSite")
@XmlType(namespace="oLabDynamics", propOrder = {"uri","publication","code"})
public class CompanionSite extends jra.model.CompanionSite  implements Resource{
	
	ResourceSupport resourceSupport = new ResourceSupport();
	
	@JsonIgnore
	List<ThematicSite> thematicSites = null;
	
	@JsonIgnore
	Publication publication = null;
	
	@JsonIgnore
	Code code = null;
	
	URI uri;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	
	public CompanionSite(){
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
		restTemplate = execShare.getRestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        entity = new HttpEntity<String>(headers);
	}
	
	public boolean addThematicSite(ThematicSite thematicSite) {
		if(thematicSites == null){	
			thematicSites = this.getThematicSites(); 	// get from the server
			if(thematicSites == null){				// nothing at the server
				thematicSites = new ArrayList<ThematicSite>();
			}
		}
		if(thematicSites.contains(thematicSite) == false){
			thematicSites.add(thematicSite);
			return true;
		}
		return false;
		
	}
	
	@XmlElement(name = "publication", required = true)
	public Publication getPublication() {
		if(publication != null){
			return publication;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();
		
		ResponseEntity<org.oLabDynamics.jra.model.Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Publication.class);
    	
		return response.getBody();
	}
	
	public void setPublication(Publication publication) {
		this.publication = publication;
	}
	
	public List<ThematicSite> getThematicSites() {
		if(thematicSites != null){
			return thematicSites;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return new ArrayList<ThematicSite>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<ThematicSite>> typeRef = new ParameterizedTypeReference<List<ThematicSite>>() {};
		ResponseEntity<List<ThematicSite>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
	}

	@XmlElement(name = "code", required = true)
	public Code getCode() {
		if(code != null){
			return code;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();
		
		ResponseEntity<org.oLabDynamics.jra.model.Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Code.class);
    	
		return response.getBody();
	}
	
	public void setCode(Code code) {
		this.code = code;
	}
	
	public void publish(PublicationMode publicationMode) throws JRAException{
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        if(getLinks().size() == 0){	// this is a new companionsite 
        	
        	String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new CompanionSite : new id, links, rel...
        	ResponseEntity<org.oLabDynamics.jra.model.CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.CompanionSite.class);
        	Resource resource = response.getBody();
  
        	add(resource.getLinks());      	
        }
        
        String href = getLink("self").getHref();
		HttpEntity<org.oLabDynamics.jra.model.CompanionSite> entity = new HttpEntity<org.oLabDynamics.jra.model.CompanionSite>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, org.oLabDynamics.jra.model.CompanionSite.class);		
		
		if(publication != null){
			
			if(((org.oLabDynamics.jra.model.Publication)publication).getLinks().size() == 0){	// this is a new publication
				
				String className = publication.getClass().getName();
		    	className = className.substring(className.lastIndexOf(".")+1);
		    	className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
					
			     href = execShare.discoverLink(className).getHref() + "/new";
			     entity = new HttpEntity(headers);
		        	
		         // get a new companionSite : new id, links, rel...
			     ResponseEntity<org.oLabDynamics.jra.model.Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Publication.class);
			     Resource resource = response.getBody();
			  
			     ((org.oLabDynamics.jra.model.Publication)publication).add(resource.getLinks()); 			     
			}
			
			href = getLink("publication").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.Publication> entity1 = new HttpEntity<org.oLabDynamics.jra.model.Publication>(((org.oLabDynamics.jra.model.Publication)publication),headers);
			
			restTemplate.exchange(href, HttpMethod.PUT, entity1, org.oLabDynamics.jra.model.Publication.class);
			
		}		
		
		if(code != null){
			
			if(((org.oLabDynamics.jra.model.Code)code).getLinks().size() == 0){	// this is a new code
				
				String className = code.getClass().getName();
		    	className = className.substring(className.lastIndexOf(".")+1);
		    	className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
					
			     href = execShare.discoverLink(className).getHref() + "/new";
			     entity = new HttpEntity(headers);
		        	
		         // get a new code : new id, links, rel...
			     ResponseEntity<org.oLabDynamics.jra.model.Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Code.class);
			     Resource resource = response.getBody();
			  
			     ((org.oLabDynamics.jra.model.Code)code).add(resource.getLinks()); 			     
			}
			
			href = getLink("code").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.Code> entity1 = new HttpEntity<org.oLabDynamics.jra.model.Code>(((org.oLabDynamics.jra.model.Code)code),headers);
			
			restTemplate.exchange(href, HttpMethod.PUT, entity1, org.oLabDynamics.jra.model.Code.class);
			
		}
		
		if(thematicSites != null){
			
			for(int i=0; i<thematicSites.size(); i++){
				org.oLabDynamics.jra.model.ThematicSite thematicSite = (org.oLabDynamics.jra.model.ThematicSite) thematicSites.get(i);
				
				 if(thematicSite.getLinks().size() == 0){	// this is a new thematicSite 
			        	
			            String className = thematicSite.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new thematicSite : new id, links, rel...
			        	ResponseEntity<org.oLabDynamics.jra.model.ThematicSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.ThematicSite.class);
			        	Resource resource = response.getBody();
			  
			        	thematicSite.add(resource.getLinks());      	
			        }
			}
			
			href = getLink("thematicSites").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.ThematicSite[]> entities = new HttpEntity<org.oLabDynamics.jra.model.ThematicSite[]>(thematicSites.toArray(new org.oLabDynamics.jra.model.ThematicSite[0]),headers);
			
			ParameterizedTypeReference<org.oLabDynamics.jra.model.ThematicSite[]> typeRef = new ParameterizedTypeReference<org.oLabDynamics.jra.model.ThematicSite[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
		
		Publication publication = this.getPublication();
		if(publication != null){
			Code code = this.getCode();
			if(code != null){
				
				List<InputData> inputs = code.getInputs();
				if(inputs==null || inputs.size()==0){
					throw new JRAException("Unable to publih a CompanionSite that has a Code without any Input.");
				}
				
				List<OutputData> outputs = code.getOutputs();
				if(outputs==null || outputs.size()==0){
					throw new JRAException("Unable to publih a CompanionSite that has a Code without any Output.");
				}
				
				List<jra.model.Author> authors = publication.getAuthors();
				if(authors==null || authors.size()==0){
					throw new JRAException("Unable to publih a CompanionSite that has a publication without any Author.");
				}
				org.oLabDynamics.jra.model.Author author;
				for(int i=0; i<authors.size(); i++){
					author = (org.oLabDynamics.jra.model.Author) authors.get(i);
					author.publish(publicationMode, headers);
				}
				
				publication.publish(publicationMode);
				
				((org.oLabDynamics.jra.model.Code)code).publish(publicationMode, headers);
				
				List<ThematicSite> thematicSites = this.getThematicSites();
				ThematicSite thematicSite;
				for(int i=0; i<thematicSites.size(); i++){
					thematicSite = thematicSites.get(i);
					((org.oLabDynamics.jra.model.ThematicSite)thematicSite).publish(publicationMode, headers);
				}
				
				InputData input;
				for(int i=0; i<inputs.size(); i++){
					input = inputs.get(i);
					((org.oLabDynamics.jra.model.InputData)input).publish(publicationMode, headers);
				}
				
				OutputData output;
				for(int i=0; i<outputs.size(); i++){
					output = outputs.get(i);
					((org.oLabDynamics.jra.model.OutputData)output).publish(publicationMode, headers);
				}
				
			} else {
				throw new JRAException("Unable to publih a CompanionSite that has no code.");
			}
		} else {
			throw new JRAException("Unable to publih a CompanionSite that has no publication.");
		}
	}

	/**
	 * Get the uri of this companionSite: a companion site can come from any Libraries for Reproducibility,
	 * the uri indentify the origin of the companionSite (which library it comes from), 
	 * and give the unique identifier it has.
	 * @return 
	 */
	@XmlElement(name = "uri", required = true)
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	@XmlTransient
	public String getTypeOfResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTypeOfResource(String typeOfResource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@JsonIgnore
	public Link getId() {
		return resourceSupport.getId();
	}

	@Override
	public void add(Link link) {
		resourceSupport.add(link);
	}

	@Override
	public void add(Iterable<Link> links) {
		resourceSupport.add(links);
	}

	@Override
	public boolean hasLinks() {
		return resourceSupport.hasLinks();
	}

	@Override
	public boolean hasLink(String rel) {
		return resourceSupport.hasLink(rel);
	}

	@Override
	@XmlTransient
	@JsonProperty("links")
	public List<Link> getLinks() {
		return resourceSupport.getLinks();
	}
	
	public void setLinks(List<Link> links){
		resourceSupport.add(links);
	}

	@Override
	public void removeLinks() {
		resourceSupport.removeLinks();
	}

	@Override
	public Link getLink(String rel) {
		return resourceSupport.getLink(rel);
	}
	
	@Override
	public String toString() {
		return "CompanionSite [thematicSites=" + thematicSites
				+ ", publication=" + publication + ", code=" + code
				+ ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((publication == null) ? 0 : publication.hashCode());
		result = prime * result
				+ ((thematicSites == null) ? 0 : thematicSites.hashCode());
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
		CompanionSite other = (CompanionSite) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (publication == null) {
			if (other.publication != null)
				return false;
		} else if (!publication.equals(other.publication))
			return false;
		if (thematicSites == null) {
			if (other.thematicSites != null)
				return false;
		} else if (!thematicSites.equals(other.thematicSites))
			return false;
		return true;
	}

		
}
