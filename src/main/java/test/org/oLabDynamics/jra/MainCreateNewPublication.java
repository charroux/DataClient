package test.org.oLabDynamics.jra;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jra.JRA;
import jra.JRA.PublicationMode;
import jra.Query;
import jra.model.Code;
import jra.model.CompanionSite;
import jra.model.InputData;
import jra.model.OutputData;
import jra.model.Publication;
import jra.model.Publication.PublicationType;
import jra.model.ThematicSite;

import org.oLabDynamics.jra.ExecShareImpl;
import org.oLabDynamics.jra.model.Author;
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
			input.setDescription("Ceci est une entr�e");
			code.addInput(input);
			
			OutputData output = new OutputData();
			output.setDescription("Ceci est une sortie");
			code.addOutput(output);
			
			
			JRA execShare = ExecShareImpl.getInstance();
			
			execShare.publish(companionSite, PublicationMode.IN_PRIVATE_FOR_EVALUATION);			
			
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
				System.out.println("les deux companionSite sont �gales");
			} else{
				System.out.println("probleme car les 2 companionSite sont diff�rentes");
			}
			
			Code code1 = companionSite1.getCode();
			CompanionSite companionSite2 = code1.getCompanionSites().get(0);
			System.out.println(companionSite2);
			
			if(companionSite1.equals(companionSite2)){
				System.out.println("les deux companionSite sont �gales");
			} else{
				System.out.println("probleme car les 2 companionSite sont diff�rentes");
			}
			
			InputData inputData = code1.getInputs().get(0);
			Code code3 = inputData.getCode();
			
			if(code1.equals(code3)){
				System.out.println("les deux codes sont �gales");
			} else{
				System.out.println("probleme car les 2 code sont diff�rentes");
			}
			
			OutputData outputData = code3.getOutputs().get(0);
			
			Publication publication2 = companionSite1.getPublication();
			
			if(publication1.equals(publication2)){
				System.out.println("les deux publis sont �gales");
			} else{
				System.out.println("probleme car les 2 publis sont diff�rentes");
			}
			
			query = new Query("publication");
			query.addFilter("title", Query.FilterOperator.EQUAL, "Une nouvelle publication");
			List<Publication> publications = execShare.prepare(query);
			publication2 = publications.get(0);
			System.out.println(publication2);
		
			Author author2 = (Author) publication2.getAuthors().get(0);
			System.out.println(author2);
		
			if(author1.equals(author2)){
				System.out.println("les deux authors sont �gales");
			} else{
				System.out.println("probleme car les 2 authors sont diff�rentes");
			}
			
			if(publication1.equals(publication2)){
				System.out.println("les deux publis sont �gales");
			} else{
				System.out.println("probleme car les 2 publis sont diff�rentes");
			}
			
			OutputData outputData1 = publication1.getCompanionSite().getCode().getOutputs().get(0);
			if(outputData.equals(outputData1)){
				System.out.println("les deux outputData sont �gales");
			} else{
				System.out.println("probleme car les 2 outputData sont diff�rentes");
			}
			
			author = new Author();
			author.setFirstName("Dupont");
			author.setLastName("Albert");
			
			publication = new Publication();
			publication.setTitle("Une publication a la con");
			publication.setPublicationType(PublicationType.WORKING_PAPER);
			publication.addAuthor(author, 1);
			
			execShare = ExecShareImpl.getInstance();
			
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
		
			author2 = (Author) publication2.getAuthors().get(0);
			System.out.println(author2);
		
			if(author1.equals(author2)){
				System.out.println("les deux authors sont �gales");
			} else{
				System.out.println("probleme car les 2 authors sont diff�rentes");
			}
			
			if(publication1.equals(publication2)){
				System.out.println("les deux publis sont �gales");
			} else{
				System.out.println("probleme car les 2 publis sont diff�rentes");
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