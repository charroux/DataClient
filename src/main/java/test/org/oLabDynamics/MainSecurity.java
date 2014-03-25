package test.org.oLabDynamics;

import java.util.List;

import org.oLabDynamics.client.EntryPoint;
import org.oLabDynamics.client.data.Author;
import org.oLabDynamics.client.data.CompanionSite;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
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
public class MainSecurity {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    	
    	RestTemplate restTemplate = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	String auth = "temporary" + ":" + "temporary";
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
    	ResponseEntity<EntryPoint> response = restTemplate.exchange("http://localhost:8081/Data/sites", HttpMethod.GET, entity, EntryPoint.class);
    	EntryPoint sites = response.getBody();
  
    	System.out.println("sites=" + sites);
    	
    	//HttpEntity<String> entity1 = new HttpEntity<String>(headers);
    	ResponseEntity<CompanionSite> response1 = restTemplate.exchange("http://localhost:8081/Data/sites/1", HttpMethod.GET, entity, CompanionSite.class);
    	CompanionSite site = response1.getBody();
    	
    	System.out.println("site=" + site);
    	
    	 ParameterizedTypeReference<List<Author>> typeRef = new ParameterizedTypeReference<List<Author>>() {};
    	 ResponseEntity<List<Author>> response2 = restTemplate.exchange("http://localhost:8081/Data/sites/author", HttpMethod.GET, entity, typeRef);
    	 
    	 List<Author> list = response2.getBody();
    	
    	System.out.println("authors=" + list);
	}

}
