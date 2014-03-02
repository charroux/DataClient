package org.oLabDynamics.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.core.DefaultLinkDiscoverer;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Benoit Charroux
 *
 * @param <T>
 */
public interface ExecShare<T> {
		
	enum Format{
		JSON
	}
	
	public List<T> prepare(Query query) throws Exception;
	
	public void persist(ResourceSupport resourceSupport);
	
	public RunningTask exec(Code code) throws ExecutorException;
	
	public void exec(Code code, ResultListener resultListener) throws ExecutorException;
	
	public RunningTask exec(Code code, List<InputData> inputs) throws ExecutorException;
	
	public void exec(Code code, List<InputData> inputs, ResultListener resultListener) throws ExecutorException;
	
	public void exportInputData(List<InputData> inputs, FileOutputStream file, Format format);
	
	public void exportInputData(List<InputData> inputs, OutputStream outputStream, Format format);
	
	public List<InputData> importInputData(FileInputStream file, Format format);
	
	public List<InputData> importInputData(InputStream outputStream, Format format);	

	public void exportOutputData(List<OutputData> outputs, FileOutputStream file, Format format);
	
	public void exportOutputData(List<OutputData> outputs, OutputStream outputStream, Format format);

	public List<OutputData> importOutputData(FileInputStream file, Format format);
	
	public List<OutputData> importOutputData(InputStream outputStream, Format format);	
}
