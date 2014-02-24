package org.oLabDynamics;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.Author;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.client.Configuration;
import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.InputData;
import org.oLabDynamics.client.Linux;
import org.oLabDynamics.client.OperatingSystem;
import org.oLabDynamics.client.Program;
import org.oLabDynamics.client.Publication;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Publication.PublicationType;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.client.write.AuthorReadWrite;
import org.oLabDynamics.client.write.CodeReadWrite;
import org.oLabDynamics.client.write.CompanionSiteReadWrite;
import org.oLabDynamics.client.write.InputDataReadWrite;
import org.oLabDynamics.client.write.OutputDataReadWrite;
import org.oLabDynamics.client.write.PublicationReadWrite;
import org.oLabDynamics.client.ThematicSite;
import org.oLabDynamics.client.UnsupportedConfigurationException;
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

public class MainCreateNewPublication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			ExecShare execShare = ExecShare.getInstance();
			
			Query query = new Query("author");
			query.addFilter("name", Query.FilterOperator.EQUAL, "Tintin");
			List<Author> authors = execShare.prepare(query);
			Author author = authors.get(0);
			System.out.println(author);
			
			author.setFirstName("Hadock");
			
			Publication publication = new Publication();
			publication.setTitle("Une nouvelle publication");
			publication.setPublicationType(PublicationType.WORKING_PAPER);
			publication.addAuthor(author, 1);
			
			author.save();
			
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
