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
			
			PublicationReadWrite publication = new PublicationReadWrite(PublicationType.WORKING_PAPER, "bla bla");
			
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
	        
			publication.publishPublication();
			
/*			File code = new File("MonCode.mat");
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

			publication.publishPublication(Publication.Type.PublishedPaper);
			
		}catch(UnsupportedConfigurationException e){
			e.printStackTrace();
		*/}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
