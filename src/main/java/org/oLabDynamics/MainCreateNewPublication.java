package org.oLabDynamics;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.client.data.Author;
import org.oLabDynamics.client.data.Code;
import org.oLabDynamics.client.data.CompanionSite;
import org.oLabDynamics.client.data.Configuration;
import org.oLabDynamics.client.data.ExecShareImpl;
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.Linux;
import org.oLabDynamics.client.data.OperatingSystem;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.data.Program;
import org.oLabDynamics.client.data.Publication;
import org.oLabDynamics.client.data.ThematicSite;
import org.oLabDynamics.client.data.Publication.PublicationType;
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
public class MainCreateNewPublication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			Author author = new Author();
			author.setFirstName("Castafiore");
			author.setLastName("Cantatrice");
			
			Publication publication = new Publication();
			publication.setTitle("Une nouvelle publication");
			publication.setPublicationType(PublicationType.WORKING_PAPER);
			publication.addAuthor(author, 1);
			
			CompanionSite companionSite = new CompanionSite();
			publication.setCompanionSite(companionSite);
			
			ThematicSite thematicSite = new ThematicSite();
			thematicSite.addCompanionSite(companionSite);
			
			Code code = new Code();
			code.setDescription("ceci est un code");	
			code.addCompanionSite(companionSite);
			
			InputData input = new InputData();
			input.setDescription("Ceci est une entrée");
			code.addInput(input);
			
			OutputData output = new OutputData();
			output.setDescription("Ceci est une sortie");
			code.addOutput(output);
			
			
			ExecShare execShare = ExecShareImpl.getInstance();
			
			execShare.publish(author);
			execShare.publish(publication);
			execShare.publish(companionSite);
			execShare.publish(code);
			execShare.publish(input);
			execShare.publish(output);
			execShare.publish(thematicSite);
			
			
			Query query = new Query("author");
			query.addFilter("firstName", Query.FilterOperator.EQUAL, "Castafiore");
			List<Author> authors = execShare.prepare(query);
			Author author1 = authors.get(0);
			System.out.println(author1);
		
			Publication publication1 = author1.getPublications().get(0);
			System.out.println(publication1);
			
			CompanionSite companionSite1 = publication1.getCompanionSite();
			System.out.println(companionSite1);
			
			ThematicSite thematicSite1 = companionSite1.getThematicSites().get(0);
			System.out.println(thematicSite1);
			
			CompanionSite companionSite3 = thematicSite1.getCompanionSites().get(0);
			
			if(companionSite1.equals(companionSite3)){
				System.out.println("les deux companionSite sont égales");
			} else{
				System.out.println("probleme car les 2 companionSite sont différentes");
			}
			
			Code code1 = companionSite1.getCode();
			CompanionSite companionSite2 = code1.getCompanionSites().get(0);
			System.out.println(companionSite2);
			
			if(companionSite1.equals(companionSite2)){
				System.out.println("les deux companionSite sont égales");
			} else{
				System.out.println("probleme car les 2 companionSite sont différentes");
			}
			
			InputData inputData = code1.getInputs().get(0);
			Code code3 = inputData.getCode();
			
			if(code1.equals(code3)){
				System.out.println("les deux codes sont égales");
			} else{
				System.out.println("probleme car les 2 code sont différentes");
			}
			
			OutputData outputData = code3.getOutputs().get(0);
			
			Publication publication2 = companionSite1.getPublication();
			
			if(publication1.equals(publication2)){
				System.out.println("les deux publis sont égales");
			} else{
				System.out.println("probleme car les 2 publis sont différentes");
			}
			
			query = new Query("publication");
			query.addFilter("title", Query.FilterOperator.EQUAL, "Une nouvelle publication");
			List<Publication> publications = execShare.prepare(query);
			publication2 = publications.get(0);
			System.out.println(publication2);
		
			Author author2 = publication2.getAuthors().get(0);
			System.out.println(author2);
		
			if(author1.equals(author2)){
				System.out.println("les deux authors sont égales");
			} else{
				System.out.println("probleme car les 2 authors sont différentes");
			}
			
			if(publication1.equals(publication2)){
				System.out.println("les deux publis sont égales");
			} else{
				System.out.println("probleme car les 2 publis sont différentes");
			}
			
			OutputData outputData1 = publication1.getCompanionSite().getCode().getOutputs().get(0);
			if(outputData.equals(outputData1)){
				System.out.println("les deux outputData sont égales");
			} else{
				System.out.println("probleme car les 2 outputData sont différentes");
			}
			
			author = new Author();
			author.setFirstName("Dupont");
			author.setLastName("Albert");
			
			publication = new Publication();
			publication.setTitle("Une publication a la con");
			publication.setPublicationType(PublicationType.WORKING_PAPER);
			publication.addAuthor(author, 1);
			
			execShare = ExecShareImpl.getInstance();
			
			execShare.publish(publication);
			execShare.publish(author);
			
			
			query = new Query("author");
			query.addFilter("firstName", Query.FilterOperator.EQUAL, "Castafiore");
			authors = execShare.prepare(query);
			author1 = authors.get(0);
			System.out.println(author1);
		
			publication1 = author1.getPublications().get(0);
			System.out.println(publication1);
			
		
			query = new Query("publication");
			query.addFilter("title", Query.FilterOperator.EQUAL, "Une nouvelle publication");
			publications = execShare.prepare(query);
			publication2 = publications.get(0);
			System.out.println(publication2);
		
			author2 = publication2.getAuthors().get(0);
			System.out.println(author2);
		
			if(author1.equals(author2)){
				System.out.println("les deux authors sont égales");
			} else{
				System.out.println("probleme car les 2 authors sont différentes");
			}
			
			if(publication1.equals(publication2)){
				System.out.println("les deux publis sont égales");
			} else{
				System.out.println("probleme car les 2 publis sont différentes");
			}
			
			//publication.save();
			
			
/*			PublicationReadWrite publication = new PublicationReadWrite(PublicationType.WORKING_PAPER, "bla bla");
			
	        AuthorReadWrite arw = new AuthorReadWrite("n1", "n2");
	        publication.addAuthor(arw, 1);
	        
	        CompanionSiteReadWrite crw = new CompanionSiteReadWrite();
	        publication.setCompanionSite(crw);
	        
	        CodeReadWrite cwr = new CodeReadWrite("bla bla");
	        crw.setReferenceImplementation(cwr);
	        
	        InputDataReadWrite irw = new InputDataReadWrite();
	        cwr.addInput(irw);
	        
	        OutputDataReadWrite orw = new OutputDataReadWrite();
	        cwr.addOutput(orw);
	        
			publication.publishPublication();*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
