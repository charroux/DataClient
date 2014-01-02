package org.oLabDynamics;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.Author;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.InputData;
import org.oLabDynamics.client.Program;
import org.oLabDynamics.client.Publication;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.client.SecurityException;
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

			ExecShare execShare = new ExecShare();
			
			Query query = new Query("author");
			query.addFilter("name", Query.FilterOperator.EQUAL, "Tintin");
			List<Author> authors = execShare.prepare(query);
			Author author = authors.get(0);
			System.out.println(author);
			
			List<Publication> publis = author.getPublications();
			Publication publication = publis.get(0);
			System.out.println(publication);
			
			try{
			publication.setTitle("Tentative changement titre");
			}catch(SecurityException e ){
				System.out.println(e);
			}
			
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
			
			Code referenceImplementation = publication.getReferenceImplementation();
			System.out.println(referenceImplementation);
			
			Code code = companionSite.getReferenceImplementation();
			System.out.println(code);
			
			if(referenceImplementation.equals(code)){
				System.out.println("les deux codes sont égaux");
			} else{
				System.out.println("probleme car les 2 codes sont différentes");
			}
			
			Publication publication2 = code.getPublication();
			
			if(publication2.equals(publication)){
				System.out.println("les deux publis sont égales");
			} else{
				System.out.println("probleme car les 2 publis sont différentes");
			}
			
			InputData inputData = code.getReferenceInputData();
			System.out.println(inputData);
			
			Program program = new Program();
			program.setCode(code);
			program.addInputDate(inputData);
			program.execute();
			
			ThematicSite thematicSite = new ThematicSite();
			thematicSite.addProgram(program);
			
			thematicSite.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		/*Code code = publi.getCode();

		query = new Query("CompanionSite");
		query.addFilter("lastName", Query.FilterOperator.EQUAL, "Tintin");
		List<Author> list = ExecShare.prepare(q);*/
		
		
		/*ThematicSite thematicSite = new ThematicSite();
		
		Sites sites = new Sites();
		
		List<CompanionSite> list = sites.find("select site from Site site where site.author = 'Mr X'");
		
		Code code = site.getCode();
		InputData inputData = site.getDemoData();
		
		Program program = new Program();
		program.setCode(code);
		program.addInputDate(inputData);
		
		thematicSite.addProgram(program);
		
		site = sites.find("select site from Site site where site.creationDate < = 'Mr X'");
		
		Code code = site.getCode();
		InputData inputData = site.getDemoData();
		
		Program program = new Program();
		program.setCode(code);
		program.addInputDate(inputData);
		
		thematicSite.addProgram(program);
		
		
		File file = new File("input.data");
		
		input.exportTo(file);
		
		
		Program program = new Program();
		thematicSite.addProgram(program );
		
		ExecShare execShare = new ExecShare();
		
		Authentication authentication = new Authentication("temporary", "temporary", "appli_ID_1");
		
		Response<Sites> response1 = execShare.retreive("http://localhost:8081/Data/sites", authentication, Sites.class);
		ResponseStatus status = response1.getStatus();
		if(status == ResponseStatus.ERROR){
			System.exit(0);
		}
		
		Sites sites = response1.getBody();
		List<ResourceLink> links = sites.getResourceLinks();
		ResourceLink linkToSite = links.get(0);
		String siteHref = linkToSite.getHref();
		String siteRelation = linkToSite.getRelation();
		
		Response<Site> response2 = execShare.retreive(siteHref, authentication, Site.class);
		status = response2.getStatus();
		if(status == ResponseStatus.ERROR){
			System.exit(0);
		}
		
		Site site = response2.getBody();
		
		List<ResourceLink> siteLinks = site.getResourceLinks();
		ResourceLink resourceLink = null;
		int i=0;
		while(i<siteLinks.size() && (resourceLink=siteLinks.get(i)).getRelation().equals("code")==false){
			i++;
		}
		
		if(resourceLink == null){
			System.exit(0);
		}
		
		String codeHref = resourceLink.getHref();
		
		Response<Code> response3 = execShare.retreive(codeHref, authentication, Code.class);
		status = response3.getStatus();
		if(status == ResponseStatus.ERROR){
			System.exit(0);
		}
		
		Code code = response3.getBody();
		
		Program program = new Program();
		program.setCode(code);
		
		resourceLink = null;
		i=0;
		while(i<siteLinks.size() && (resourceLink=siteLinks.get(i)).getRelation().equals("inputData")==false){
			i++;
		}
		
		if(resourceLink == null){
			System.exit(0);
		}
		
		String inputDataHref = resourceLink.getHref();
		
		Response<InputData> response4 = execShare.retreive(inputDataHref, authentication, InputData.class);
		status = response3.getStatus();
		if(status == ResponseStatus.ERROR){
			System.exit(0);
		}
		InputData inputData = response4.getBody();
		
		program.addInputDate(inputData);
		
		ThematicSite execution = new ThematicSite();
		execution.addProgram(program);
		
		execShare.run(authentication, execution);*/
		
/*    	RestTemplate restTemplate = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	String auth = "temporary" + ":" + "temporary";
    	
    	byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
    	
        HttpEntity<String> entity = new HttpEntity<String>(headers);
    	ResponseEntity<Sites> response = restTemplate.exchange("http://localhost:8081/Data/sites", HttpMethod.GET, entity, Sites.class);
    	Sites sites = response.getBody();
    	
    	System.out.println("sites=" + sites);
    	
    	HttpEntity<String> entity1 = new HttpEntity<String>(headers);
    	ResponseEntity<Site> response1 = restTemplate.exchange("http://localhost:8081/Data/sites/1", HttpMethod.GET, entity, Site.class);
    	Site site = response1.getBody();
    	
    	System.out.println("site=" + site);*/
	}

}
