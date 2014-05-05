package test.org.oLabDynamics.jra;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.JRA;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.jra.model.Author;
import org.oLabDynamics.jra.model.Code;
import org.oLabDynamics.jra.model.CompanionSite;
import org.oLabDynamics.jra.model.InputData;
import org.oLabDynamics.jra.model.Publication1;
import org.oLabDynamics.jra.model.ThematicSite;
import org.oLabDynamics.jra.model.Publication1.PublicationType;
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
public class MainCreateThematicSite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			ThematicSiteReadWrite thematicSite = new ThematicSiteReadWrite();
			
			JRA execShare = JRA.getInstance();
			
			Query query = new Query("author");
			query.addFilter("name", Query.FilterOperator.EQUAL, "Tintin");
			List<Author> authors = execShare.prepare(query);
			Author author = authors.get(0);
			System.out.println(author);
			
			List<Publication1> publis = author.getPublications();
			Publication1 publication = publis.get(0);
			System.out.println(publication);
			
			CompanionSite companionSite = publication.getCompanionSite();
			
			thematicSite.addCompanionSite(companionSite);
			
			CompanionSiteReadWrite companionSiteReadWrite = new CompanionSiteReadWrite();
			
			PublicationReadWrite publicationReadWrite = new PublicationReadWrite(PublicationType.WORKING_PAPER, "Titre d'un super publi");
			publicationReadWrite.setCompanionSite(companionSiteReadWrite);
			
			thematicSite.addCompanionSite(companionSiteReadWrite);
			
			thematicSite.publishThematicSite();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
