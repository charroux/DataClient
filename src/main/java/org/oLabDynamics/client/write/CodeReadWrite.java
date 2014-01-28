package org.oLabDynamics.client.write;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.client.Configuration;
import org.oLabDynamics.client.InputData;
import org.oLabDynamics.client.OutputData;
import org.oLabDynamics.client.Publication;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

/**
 * Caches data from the server: get data from the server only at the first call of a getter method.
 * @author charroux
 *
 */
public class CodeReadWrite extends Code {

	@JsonIgnore
	CompanionSite companionSite;
	
	List<InputData> inputs;
	List<OutputData> outputs;
	
	@JsonIgnore
	Set<Configuration> configurations;
	
	public CodeReadWrite(){
		super();
	}

	public CodeReadWrite(String description) {
		super();
		super.setDescription(description);
	}
	
	@Override
	public CompanionSite getCompanionSite(){
		if(companionSite == null){
			companionSite = super.getCompanionSite();
		}
		return companionSite;
	}

	void setCompanionSite(CompanionSiteReadWrite companionSite) {
		this.companionSite = companionSite;
	}

	public boolean addInput(InputDataReadWrite input) {
		if(inputs == null){
			inputs = this.getInputs();	// try to get authors form the server
		}
		if(inputs.contains(input) == false){
			inputs.add(input);
			input.setCode(this);
			return true;
		}
		return false;
	}
	
	@Override
	public List<InputData> getInputs() {
		if(inputs == null){	// la liste des publications n'a pas été réinitialisée, on peut prendre celle du serveur
			List<InputData> inputsFromTheServer = super.getInputs();
			if(inputsFromTheServer == null){
				return inputs = new ArrayList<InputData>();
			} else {
				return inputs = inputsFromTheServer;
			}
		}
		return inputs;
	}
	
	public boolean addOutput(OutputDataReadWrite output) {
		if(outputs == null){
			outputs = this.getOutputs();	// try to get authors form the server
		}
		if(outputs.contains(output) == false){
			outputs.add(output);
			output.setCode(this);
			return true;
		}
		return false;
	}
	
	@Override
	public List<OutputData> getOutputs() {
		if(outputs == null){	// la liste des publications n'a pas été réinitialisée, on peut prendre celle du serveur
			List<OutputData> outputsFromTheServer = super.getOutputs();
			if(outputsFromTheServer == null){
				return outputs = new ArrayList<OutputData>();
			} else {
				return outputs = outputsFromTheServer;
			}
		}
		return outputs;
	}

	@Override
	public String toString() {
		return "CodeReadWrite [inputs=" + inputs + ", outputs=" + outputs
				+ ", configurations=" + configurations + "]";
	}
	
}
