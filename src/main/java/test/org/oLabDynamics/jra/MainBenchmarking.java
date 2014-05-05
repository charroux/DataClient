package test.org.oLabDynamics.jra;

import java.util.List;

import jra.JRA;
import jra.Query;
import jra.model.Author;
import jra.model.Code;
import jra.model.CompanionSite;
import jra.model.InputData;
import jra.model.OutputData;
import jra.model.Publication;

import org.oLabDynamics.jra.ExecShareImpl;

/**
 * 
 * @author Benoit Charroux
 *
 */
public class MainBenchmarking {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			
			JRA execShare = ExecShareImpl.getInstance();
					
			Query query = new Query("author");
			query.addFilter("firstName", Query.FilterOperator.EQUAL, "Tintin");
			//query.addFilter("lastName", Query.FilterOperator.EQUAL, "Journaliste");
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
