package test.org.oLabDynamics;

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
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.data.Program;
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
public class MainThematicSite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			ExecShare execShare = ExecShare.getInstance();
					
			Query query = new Query("thematicSite");
			//query.addFilter("name", Query.FilterOperator.EQUAL, "Tintin");
			List<ThematicSite> thematicSites = execShare.prepare(query);
			ThematicSite thematicSite = thematicSites.get(0);
			System.out.println(thematicSite);
			
			List<CompanionSite> companionSites = thematicSite.getCompanionSites();
			CompanionSite companionSite = companionSites.get(0);
			System.out.println(companionSite);
			
			Publication publication = companionSite.getPublication();
			System.out.println(publication);
			
			Code referenceImplementation = companionSite.getReferenceImplementation();
			System.out.println(referenceImplementation);
			
			List<InputData> inputs = referenceImplementation.getInputs();
			System.out.println(inputs);
			
			InputData inputData = inputs.get(0);
			Code code = inputData.getCode();
			if(referenceImplementation.equals(code)){
				System.out.println("les deux codes sont égaux");
			} else{
				System.out.println("probleme car les 2 codes sont différentes");
			}

			List<OutputData> outputs = referenceImplementation.getOutputs();
			System.out.println(outputs);
			
			OutputData outputData = outputs.get(0);
			Code code1 = outputData.getCode();
			if(referenceImplementation.equals(code1)){
				System.out.println("les deux codes sont égaux");
			} else{
				System.out.println("probleme car les 2 codes sont différentes");
			}
			
			thematicSites = companionSite.getThematicSites();
			ThematicSite thematicSite1 = thematicSites.get(0);
			System.out.println(thematicSite1);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
