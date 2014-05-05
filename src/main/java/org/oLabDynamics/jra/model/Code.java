package org.oLabDynamics.jra.model;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.model.CompanionSite;
import jra.model.InputData;
import jra.model.OutputData;
import jra.JRA.PublicationMode;
import jra.JRAException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.oLabDynamics.jra.ExecShareConnectionFactory;
import org.oLabDynamics.jra.ExecShareImpl;
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
@XmlRootElement(name = "Code")
@XmlType(namespace="oLabDynamics")//, propOrder = {"description","inputs","outputs"})
public class Code extends jra.model.Code implements Resource{
	
	ResourceSupport resourceSupport = new ResourceSupport();
	
	@JsonIgnore
	List<CompanionSite> companionSites;
	
	@JsonIgnore
	List<InputData> inputs;
	
	@JsonIgnore
	List<OutputData> outputs;
	
	//String description;
	
	@JsonIgnore
	RestTemplate restTemplate;
	
	@JsonIgnore
	HttpEntity<String> entity;
	
	public Code(){
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
	
	public boolean addInput(InputData input) {
		if(inputs == null){
			inputs = this.getInputs();	// try to get authors form the server
			if(inputs == null){
				inputs = new ArrayList<InputData>();
			}
		}
		if(inputs.contains(input) == false){
			inputs.add(input);
			input.setCode(this);
			return true;
		}
		return false;
	}
	
	public boolean addOutput(OutputData output) {
		if(outputs == null){
			outputs = this.getOutputs();	// try to get authors form the server
			if(outputs == null){
				outputs = new ArrayList<OutputData>();
			}
		}
		if(outputs.contains(output) == false){
			outputs.add(output);
			output.setCode(this);
			return true;
		}
		return false;
	}
	
	@XmlTransient
	public List<CompanionSite> getCompanionSites(){
		if(companionSites != null){
			return companionSites;
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

		ParameterizedTypeReference<List<org.oLabDynamics.jra.model.CompanionSite>> typeRef = new ParameterizedTypeReference<List<org.oLabDynamics.jra.model.CompanionSite>>() {};
		ResponseEntity<List<org.oLabDynamics.jra.model.CompanionSite>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		List<CompanionSite> sites = new ArrayList<CompanionSite>(response.getBody());
		return sites;
	}
	
	public boolean addCompanionSite(CompanionSite companionSite) {
		if(companionSites == null){
			companionSites = this.getCompanionSites();
			if(companionSites == null){
				companionSites = new ArrayList<CompanionSite>();
			}
		}
		if(companionSites.contains(companionSite) == false){
			companionSites.add(companionSite);
			companionSite.setCode(this);
			return true;
		}
		return false;
	}
	
	//@XmlElement(required = true)
	@XmlAnyElement
	public List<InputData> getInputs(){
		if(inputs != null){
			return inputs;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return new ArrayList<InputData>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<org.oLabDynamics.jra.model.InputData>> typeRef = new ParameterizedTypeReference<List<org.oLabDynamics.jra.model.InputData>>() {};
		ResponseEntity<List<org.oLabDynamics.jra.model.InputData>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		List<InputData> ins = new ArrayList<InputData>(response.getBody());
		return ins;
	}
	
	//@XmlElement(required = true)
	@XmlAnyElement
	public List<OutputData> getOutputs(){
		if(outputs != null){
			return outputs;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return new ArrayList<OutputData>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<org.oLabDynamics.jra.model.OutputData>> typeRef = new ParameterizedTypeReference<List<org.oLabDynamics.jra.model.OutputData>>() {};
		ResponseEntity<List<org.oLabDynamics.jra.model.OutputData>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		List<OutputData> outs = new ArrayList<OutputData>(response.getBody());
		
		return outs;
	}
	
/*	public InputData getReferenceInputData() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();

		ResponseEntity<InputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, InputData.class);
    	
		return response.getBody();
	}*/

/*	public Publication getPublication() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		String href = link.getHref();

		ResponseEntity<Publication> response = restTemplate.exchange(href, HttpMethod.GET, entity, Publication.class);
    	
		return response.getBody();
	}*/

	@XmlElement(name = "description", required = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void export(File file) {
		// TODO Auto-generated method stub	
	}

	/*public Set<Configuration> getConfigurations() {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<Set<Configuration>> typeRef = new ParameterizedTypeReference<Set<Configuration>>() {};
		ResponseEntity<Set<Configuration>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);

		return response.getBody();
	}
	
	public Set<Configuration> getConfigurations(Class operatingSystem) {
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		String href = link.getHref();
		
		href = href + "?operatingSystemName=" + operatingSystem.getName();
		
		ParameterizedTypeReference<Set<Configuration>> typeRef = new ParameterizedTypeReference<Set<Configuration>>() {};
		ResponseEntity<Set<Configuration>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);

		return response.getBody();
	}*/
	
	/**
	 * Restricted access: can not be used without special authorization.
	 * Use {@link org.oLabDynamics.client.JRA#publish(CompanionSite, PUBLICATION_MODE) publish} instead.
	 * @param publicationMode
	 * @throws JRAException
	 */
	public void publish(PublicationMode publicationMode) throws JRAException{
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnectionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        this.publish(publicationMode, headers);
        
	}
	
	void publish(PublicationMode publicationMode, HttpHeaders headers){
		
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
        
        if(getLinks().size() == 0){	// this is a new code 
        	
            String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new code : new id, links, rel...
        	ResponseEntity<org.oLabDynamics.jra.model.Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.Code.class);
        	Resource resource = response.getBody();
  
        	add(resource.getLinks());      	
        }
        
        String href = getLink("self").getHref();
		HttpEntity<org.oLabDynamics.jra.model.Code> entity = new HttpEntity<org.oLabDynamics.jra.model.Code>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, org.oLabDynamics.jra.model.Code.class);
		
		if(companionSites != null){
			
			for(int i=0; i<companionSites.size(); i++){
				org.oLabDynamics.jra.model.CompanionSite companionSite = (org.oLabDynamics.jra.model.CompanionSite) companionSites.get(i);
				
				 if(companionSite.getLinks().size() == 0){	// this is a new companionSite 
			        	
			            String className = companionSite.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new publication : new id, links, rel...
			        	ResponseEntity<org.oLabDynamics.jra.model.CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.CompanionSite.class);
			        	Resource resource = response.getBody();
			  
			        	companionSite.add(resource.getLinks());      	
			        }
			}
			
			href = getLink("companionSites").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.CompanionSite[]> entities = new HttpEntity<org.oLabDynamics.jra.model.CompanionSite[]>(companionSites.toArray(new org.oLabDynamics.jra.model.CompanionSite[0]),headers);
			
			ParameterizedTypeReference<org.oLabDynamics.jra.model.CompanionSite[]> typeRef = new ParameterizedTypeReference<org.oLabDynamics.jra.model.CompanionSite[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
		
		if(inputs != null){
			
			for(int i=0; i<inputs.size(); i++){
				org.oLabDynamics.jra.model.InputData input = (org.oLabDynamics.jra.model.InputData) inputs.get(i);
				
				 if(input.getLinks().size() == 0){	// this is a new input 
			        	
			            String className = input.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new input : new id, links, rel...
			        	ResponseEntity<org.oLabDynamics.jra.model.InputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.InputData.class);
			        	Resource resource = response.getBody();
			  
			        	input.add(resource.getLinks());      	
			        }
			}
			
			href = getLink("inputs").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.InputData[]> entities = new HttpEntity<org.oLabDynamics.jra.model.InputData[]>(inputs.toArray(new org.oLabDynamics.jra.model.InputData[0]),headers);
			
			ParameterizedTypeReference<org.oLabDynamics.jra.model.InputData[]> typeRef = new ParameterizedTypeReference<org.oLabDynamics.jra.model.InputData[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
		
		if(outputs != null){
			
			for(int i=0; i<outputs.size(); i++){
				org.oLabDynamics.jra.model.OutputData output = (org.oLabDynamics.jra.model.OutputData) outputs.get(i);
				
				 if(output.getLinks().size() == 0){	// this is a new output 
			        	
			            String className = output.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new output : new id, links, rel...
			        	ResponseEntity<org.oLabDynamics.jra.model.OutputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.OutputData.class);
			        	Resource resource = response.getBody();
			  
			        	output.add(resource.getLinks());      	
			        }
			}
			
			href = getLink("outputs").getHref();
			
			HttpEntity<org.oLabDynamics.jra.model.OutputData[]> entities = new HttpEntity<org.oLabDynamics.jra.model.OutputData[]>(outputs.toArray(new org.oLabDynamics.jra.model.OutputData[0]),headers);
			
			ParameterizedTypeReference<org.oLabDynamics.jra.model.OutputData[]> typeRef = new ParameterizedTypeReference<org.oLabDynamics.jra.model.OutputData[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
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
		return "Code [companionSites=" + companionSites + ", inputs=" + inputs
				+ ", outputs=" + outputs + ", toString()=" + super.toString()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((companionSites == null) ? 0 : companionSites.hashCode());
		result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
		result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
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
		Code other = (Code) obj;
		if (companionSites == null) {
			if (other.companionSites != null)
				return false;
		} else if (!companionSites.equals(other.companionSites))
			return false;
		if (inputs == null) {
			if (other.inputs != null)
				return false;
		} else if (!inputs.equals(other.inputs))
			return false;
		if (outputs == null) {
			if (other.outputs != null)
				return false;
		} else if (!outputs.equals(other.outputs))
			return false;
		return true;
	}

	

}
