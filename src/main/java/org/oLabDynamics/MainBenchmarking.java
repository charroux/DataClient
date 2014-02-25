package org.oLabDynamics;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.Author;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.client.ExecShareImpl;
import org.oLabDynamics.client.InputData;
import org.oLabDynamics.client.OutputData;
import org.oLabDynamics.client.Program;
import org.oLabDynamics.client.Publication;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.client.ThematicSite;
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

public class MainBenchmarking {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			ExecShare execShare = ExecShareImpl.getInstance();
					
			Query query = new Query("author");
			query.addFilter("name", Query.FilterOperator.EQUAL, "Tintin");
			List<Author> authors = execShare.prepare(query);
			Author author = authors.get(0);
			System.out.println(author);
			
			List<Publication> publis = author.getPublications();
			Publication publication = publis.get(0);
			System.out.println(publication);
			
			String title = publication.getTitle();
			query = new Query("publication");
			query.addFilter("title", Query.FilterOperator.EQUAL, title);
			publis = execShare.prepare(query);
			Publication publication1 = publis.get(0);
			System.out.println(publication1);
			
			if(publication.equals(publication1)){
				System.out.println("les deux publis sont égales");
			} else{
				System.out.println("probleme car les 2 publis sont différentes");
			}
			
			CompanionSite companionSite = publication.getCompanionSite();
			System.out.println(companionSite);
			
			Code code = companionSite.getCode();
			System.out.println(code);
			
			CompanionSite companionSite1 = code.getCompanionSites().get(0);
			System.out.println(companionSite1);
			
			if(companionSite.equals(companionSite1)){
				System.out.println("les deux companionSite sont égales");
			} else{
				System.out.println("probleme car les 2 companionSites sont différentes");
			}
			
			List<InputData> inputs = code.getInputs();
			System.out.println(inputs);
			
			InputData inputData = inputs.get(0);
			Code code1 = inputData.getCode();
			if(code.equals(code1)){
				System.out.println("les deux codes sont égaux");
			} else{
				System.out.println("probleme car les 2 codes sont différentes");
			}

			List<OutputData> outputs = code.getOutputs();
			System.out.println(outputs);
			
			OutputData outputData = outputs.get(0);
			Code code2 = outputData.getCode();
			if(code.equals(code2)){
				System.out.println("les deux codes sont égaux");
			} else{
				System.out.println("probleme car les 2 codes sont différentes");
			}

			
/*			Program program = new Program();
			program.setCode(referenceImplementation);
			program.addInputDate(inputData);
			program.execute();
			
			ThematicSite thematicSite = new ThematicSite();
			thematicSite.addProgram(program);
			
			thematicSite.execute();*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
