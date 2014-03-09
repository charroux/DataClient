package org.oLabDynamics;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.client.data.Author;
import org.oLabDynamics.client.data.Code;
import org.oLabDynamics.client.data.CompanionSite;
import org.oLabDynamics.client.data.ExecShareImpl;
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.data.Program;
import org.oLabDynamics.client.data.Publication;
import org.oLabDynamics.client.data.ThematicSite;
import org.oLabDynamics.client.exec.RunningTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.codec.Base64;

/**
 * 
 * @author Benoit Charroux
 *
 */
public class MainExec {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			ExecShare execShare = ExecShareImpl.getInstance();
					
			Query query = new Query("author");
			query.addFilter("firstName", Query.FilterOperator.EQUAL, "Tintin");
			List<Author> authors = execShare.prepare(query);
			Author author = authors.get(0);
			
			List<Publication> publis = author.getPublications();
			Publication publication = publis.get(0);
			
			CompanionSite companionSite = publication.getCompanionSite();
			
			Code code = companionSite.getCode();
			if(code != null){
			
				List<InputData> inputs = code.getInputs();
				if(inputs != null){
					RunningTask runningTask = execShare.exec(companionSite);
					//List<OutputData> outputs = runningTask.getResult();
					//System.out.println(outputs);
					
					Thread.sleep(6000);
					boolean cancelled = runningTask.cancel(true);
					System.out.println("cancelled = " + cancelled);
				}

			}
			
						
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
