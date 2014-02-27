package org.oLabDynamics.client;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.write.CompanionSiteReadWrite;
import org.oLabDynamics.client.write.InputDataReadWrite;
import org.oLabDynamics.client.write.OutputDataReadWrite;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class Code extends ResourceSupport {
	
	@JsonIgnore
	List<CompanionSite> companionSites;
	
	@JsonIgnore
	List<InputData> inputs;
	
	@JsonIgnore
	List<OutputData> outputs;
	
	String description;
	
	@JsonIgnore
	RestTemplate restTemplate;
	
	@JsonIgnore
	HttpEntity<String> entity;
	
	public Code(){
		ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
		restTemplate = execShare.getRestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
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
	
	public List<CompanionSite> getCompanionSites(){
		if(companionSites != null){
			return companionSites;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		
		Link link = super.getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();

		ParameterizedTypeReference<List<CompanionSite>> typeRef = new ParameterizedTypeReference<List<CompanionSite>>() {};
		ResponseEntity<List<CompanionSite>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
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
	
	public List<InputData> getInputs(){
		if(inputs != null){
			return inputs;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		if(link == null){
			return new ArrayList<InputData>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<InputData>> typeRef = new ParameterizedTypeReference<List<InputData>>() {};
		ResponseEntity<List<InputData>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
	}
	
	public List<OutputData> getOutputs(){
		if(outputs != null){
			return outputs;
		}
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = super.getLink(attributeName);
		if(link == null){
			return new ArrayList<OutputData>();
		}
		String href = link.getHref();
		
		ParameterizedTypeReference<List<OutputData>> typeRef = new ParameterizedTypeReference<List<OutputData>>() {};
		ResponseEntity<List<OutputData>> response = restTemplate.exchange(href, HttpMethod.GET, entity, typeRef);
    	
		return response.getBody();
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
	
	void save(){
		
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	ExecShareImpl execShare = (ExecShareImpl) ExecShareImpl.getInstance();
    	ExecShareConnexionFactory connexionFactory = execShare.getExecShareConnexionFactory();
    	String auth = connexionFactory.getUserName() + ":" + connexionFactory.getPassword();

    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
        
        if(super.getLinks().size() == 0){	// this is a new code 
        	
            String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new code : new id, links, rel...
        	ResponseEntity<Code> response = restTemplate.exchange(href, HttpMethod.GET, entity, Code.class);
        	ResourceSupport resource = response.getBody();
  
        	super.add(resource.getLinks());      	
        }
        
        String href = super.getLink("self").getHref();
		HttpEntity<Code> entity = new HttpEntity<Code>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, Code.class);
		
		if(companionSites != null){
			
			for(int i=0; i<companionSites.size(); i++){
				CompanionSite companionSite = companionSites.get(i);
				
				 if(companionSite.getLinks().size() == 0){	// this is a new companionSite 
			        	
			            String className = companionSite.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new publication : new id, links, rel...
			        	ResponseEntity<CompanionSite> response = restTemplate.exchange(href, HttpMethod.GET, entity, CompanionSite.class);
			        	ResourceSupport resource = response.getBody();
			  
			        	companionSite.add(resource.getLinks());      	
			        }
			}
			
			href = super.getLink("companionSites").getHref();
			
			HttpEntity<CompanionSite[]> entities = new HttpEntity<CompanionSite[]>(companionSites.toArray(new CompanionSite[0]),headers);
			
			ParameterizedTypeReference<CompanionSite[]> typeRef = new ParameterizedTypeReference<CompanionSite[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
		
		if(inputs != null){
			
			for(int i=0; i<inputs.size(); i++){
				InputData input = inputs.get(i);
				
				 if(input.getLinks().size() == 0){	// this is a new input 
			        	
			            String className = input.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		//className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new input : new id, links, rel...
			        	ResponseEntity<InputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, InputData.class);
			        	ResourceSupport resource = response.getBody();
			  
			        	input.add(resource.getLinks());      	
			        }
			}
			
			href = super.getLink("inputs").getHref();
			
			HttpEntity<InputData[]> entities = new HttpEntity<InputData[]>(inputs.toArray(new InputData[0]),headers);
			
			ParameterizedTypeReference<InputData[]> typeRef = new ParameterizedTypeReference<InputData[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
		
		if(outputs != null){
			
			for(int i=0; i<outputs.size(); i++){
				OutputData output = outputs.get(i);
				
				 if(output.getLinks().size() == 0){	// this is a new output 
			        	
			            String className = output.getClass().getName();
			    		className = className.substring(className.lastIndexOf(".")+1);
			    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
			    		
			            href = execShare.discoverLink(className).getHref() + "/new";

			        	entity = new HttpEntity(headers);
			        	
			        	// get a new output : new id, links, rel...
			        	ResponseEntity<OutputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, OutputData.class);
			        	ResourceSupport resource = response.getBody();
			  
			        	output.add(resource.getLinks());      	
			        }
			}
			
			href = super.getLink("outputs").getHref();
			
			HttpEntity<OutputData[]> entities = new HttpEntity<OutputData[]>(outputs.toArray(new OutputData[0]),headers);
			
			ParameterizedTypeReference<OutputData[]> typeRef = new ParameterizedTypeReference<OutputData[]>() {};
			restTemplate.exchange(href, HttpMethod.PUT, entities, typeRef);
		}
	}

	@Override
	public String toString() {
		return "Code [companionSites=" + companionSites + ", inputs=" + inputs
				+ ", outputs=" + outputs + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((companionSites == null) ? 0 : companionSites.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
