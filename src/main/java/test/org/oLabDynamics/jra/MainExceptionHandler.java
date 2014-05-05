package test.org.oLabDynamics.jra;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.JRA;
import org.oLabDynamics.client.JRA;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.jra.ExecShareImpl;
import org.oLabDynamics.jra.model.Author;
import org.oLabDynamics.jra.model.Code;
import org.oLabDynamics.jra.model.CompanionSite;
import org.oLabDynamics.jra.model.InputData;
import org.oLabDynamics.jra.model.OutputData;
import org.oLabDynamics.jra.model.Publication1;
import org.oLabDynamics.jra.model.ThematicSite;
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
public class MainExceptionHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			JRA execShare = ExecShareImpl.getInstance();
					
			Query query = new Query("author");
			query.addFilter("firstName", Query.FilterOperator.EQUAL, "n'importe quoi rgrrrth");
			List<Author> authors = execShare.prepare(query);
			System.out.println(authors);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
