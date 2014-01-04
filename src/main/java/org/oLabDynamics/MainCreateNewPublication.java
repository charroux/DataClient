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
import org.oLabDynamics.client.Query.FilterOperator;
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
			
			Publication publication = new Publication("Un nouvel algorithme", Publication.Type.WorkingPaper);
			
			List<Author> authors = new ArrayList<Author>();
			
			Author author = new Author("Christophe", "Marchand");
			authors.add(author);
			
			author = new Author("Paul", "Durand");
			authors.add(author);
			
			publication.setAuthors(authors);
			
			String login = "myLogin";
			String password = "myPassword";
			
			publication.publish(Publication.PublicationMode.ForContactsOnly);
			
			File code = new File("MonCode.mat");
			Code referenceImplementation = new Code(code);
			
			Configuration configuration;
			
			ExecShare execShare = new ExecShare();
			Query query = new Query("configuration");
			query.addFilter("operatingSystem.distribution", Query.FilterOperator.EQUAL, "CentOs");
			query.addFilter("version", Query.FilterOperator.EQUAL, "2.3");
			query.addFilter("programmingLanguage", Query.FilterOperator.EQUAL, "Matlab");
			query.addFilter("libraries", Query.FilterOperator.CONTAIN, "Toolbox1", "Toobox2");
			List<Configuration> configurations = execShare.prepare(query);
			if(configurations.size() == 0){	// configuration demandée inexistante => demande crétion nouvelle configuration
			
				String programmingLanguage = "Matlab";
				configuration = new Configuration(programmingLanguage);
				Linux linux = new Linux("Centos", "2.3");
				configuration.setOperatingSystem(linux);
				Library library = new Library("Toolbox1");
				configuration.addLibrary(library);
				library = new Library("Toolbox2");
				configuration.addLibrary(library);
				
			}
			
			referenceImplementation.setConfiguration(configuration);
				
			publication.setReferenceImplementation(referenceImplementation);
			
			File data = new File("mydata");
			InputData inputData = new InputData(data);
			
			referenceImplementation.setReferenceInputData(inputData);
			
			OutputData outputData = publication.launch(login, password, Publication.LaunchMode.Test);

			publication.publish(Publication.Type.PublishedPaper);
			
		}catch(UnsupportedConfigurationException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
