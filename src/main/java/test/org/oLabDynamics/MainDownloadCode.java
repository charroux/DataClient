package test.org.oLabDynamics;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.client.data.Author;
import org.oLabDynamics.client.data.Code;
import org.oLabDynamics.client.data.CompanionSite;
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.Publication;
import org.oLabDynamics.client.data.ThematicSite;
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
public class MainDownloadCode {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{

			ExecShare execShare = new ExecShare();
			
			Query query = new Query("publication");
			query.addFilter("title", Query.FilterOperator.EQUAL, "bla bla");
			List<Publication> publis = execShare.prepare(query);
			Publication publication = publis.get(0);
			System.out.println(publication);
			
			Code referenceImplementation = publication.getReferenceImplementation();
			System.out.println(referenceImplementation);
			
			File file = new File("code.c");
			referenceImplementation.export(file);
			
			Set<Configuration> configurations = referenceImplementation.getConfigurations();
			Iterator<Configuration> iterator = configurations.iterator();
			while(iterator.hasNext()){
				System.out.println(iterator.next());
			}
			
			configurations = referenceImplementation.getConfigurations(Linux.class);
			Configuration configuration = configurations.iterator().next();
			configuration.installOnThisMachine();
						
		}catch(UnsupportedConfigurationException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
