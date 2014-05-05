package org.oLabDynamics.jra.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.model.Code;
import jra.JRA.PublicationMode;
import jra.JRAException;
import jra.model.Choice;

import org.springframework.core.io.FileSystemResource;
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
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
/**
 * 
 * @author Benoit Charroux
 *
 */

@XmlRootElement(name = "InputData")
@XmlType(namespace="oLabDynamics")	//, propOrder = {"nameInCode","nickName","description","kind","type","mandatory","multipleValues","data"})
public class InputData extends jra.model.InputData implements Cloneable, Resource {
	
	ResourceSupport resourceSupport = new ResourceSupport();
	
	//@XmlTransient
	@JsonIgnore
	Code code;
	
	Object data;
	
	@JsonIgnore
	RestTemplate restTemplate;
	@JsonIgnore
	HttpEntity<String> entity;
	
	public InputData(){
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

	//@XmlTransient
	@Override
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

	//@XmlTransient
	@XmlElement(name = "data", required = true)
	public Object getData() throws JRAException {
		if(data != null){
			return data;
		}
		
/*		Integer t[][] = new Integer[1][1]; 
		t[0][0] = 4;
		data = t;
		
		return data;*/
		
		class Local {};
		Method currentMethod = Local.class.getEnclosingMethod();
		String currentMethodName = currentMethod.getName();
		String attributeName = currentMethodName.substring(3, 4).toLowerCase() + currentMethodName.substring(4);
		Link link = getLink(attributeName);
		if(link == null){
			return null;
		}
		
		String href = link.getHref();
		
		ResponseEntity<String> response = restTemplate.exchange(href, HttpMethod.GET, entity, String.class);
    	
		String dataAsString = response.getBody();
		
		switch(kind){
		case MATRIX:
			String[] rows = dataAsString.split("},");
			String[][] matrixOfStrings = new String[rows.length][];
			String row;
		    for (int i=0; i<rows.length; i++) {
		    	row = rows[i];
		    	matrixOfStrings[i] = row.split(",");
		    }
		    
		    Object[][]matrix = null;
		    
		    for(int j=0; j<matrixOfStrings.length; j++){
				for(int k=0; k<matrixOfStrings[j].length; k++){
					matrixOfStrings[j][k] = matrixOfStrings[j][k].trim();

					Pattern p = Pattern.compile("-?\\d+");
					Matcher m = p.matcher(matrixOfStrings[j][k]);
					while (m.find()) {
						matrixOfStrings[j][k] = m.group();
					}
					
					switch(type){
					case INTEGER:
						if(matrix == null){
							matrix = new Integer[matrixOfStrings.length][matrixOfStrings[0].length];
						}
						matrix[j][k] = Integer.parseInt(matrixOfStrings[j][k]);
						break;
					case REAL:
						if(matrix == null){
							matrix = new Double[matrixOfStrings.length][matrixOfStrings[0].length];
						}
						matrix[j][k] = Double.parseDouble(matrixOfStrings[j][k]);
						break;
					default:
						break;
					}
				}
			}
		    
		    data = matrix;
		    return data;

		case CHOICE:
			Choice choice = new Choice();
			//String[] choicesAsString = dataAsString.split(",[");
			switch(type){
			case STRING:
				//choice.setValue(choicesAsString);
				
				
				
				break;
			default:
				break;
			}
			break;
		case RAW :
			//throw new ExecShareException("Raw data can not be manipulated in Java, use export method to get raw data into a file");
		default:
			break;
			
		}
		
		
		//Integer[][] matrix1 = {{1,2,3}, {2,3,4}};
		
		return null;
	}

	public void setData(Object data) {
		this.data = data;
	}

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
        
        if(getLinks().size() == 0){	// this is a new input 
        	
        	String className = this.getClass().getName();
    		className = className.substring(className.lastIndexOf(".")+1);
    		className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
    		
            String href = execShare.discoverLink(className).getHref() + "/new";

        	HttpEntity entity = new HttpEntity(headers);
        	
        	// get a new input : new id, links, rel...
        	ResponseEntity<org.oLabDynamics.jra.model.InputData> response = restTemplate.exchange(href, HttpMethod.GET, entity, org.oLabDynamics.jra.model.InputData.class);
        	Resource resource = response.getBody();
  
        	add(resource.getLinks());      	
        }
        
        String href = getLink("self").getHref();
		HttpEntity<org.oLabDynamics.jra.model.InputData> entity = new HttpEntity<org.oLabDynamics.jra.model.InputData>(this,headers);
	
		restTemplate.exchange(href, HttpMethod.PUT, entity, org.oLabDynamics.jra.model.InputData.class);		
		
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
	}

	@Override
    public InputData clone() throws CloneNotSupportedException {
		InputData clone = (InputData) super.clone();
        return clone;
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
		return "InputData [toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		InputData other = (InputData) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	
	
}
