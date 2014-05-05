package test.org.oLabDynamics.jra;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import jra.JRA;
import jra.Query;
import jra.model.Author;
import jra.model.Code;
import jra.model.CompanionSite;
import jra.model.Publication;

import org.oLabDynamics.jra.ExecShareImpl;

/**
 * 
 * @author Benoit Charroux
 *
 */
public class MainImportExport {

	public static void main(String[] args) {
		
		try{
			
			JRA execShare = ExecShareImpl.getInstance();
					
			Query query = new Query("author");
			List<Author> authors = execShare.prepare(query);
			Author author = authors.get(0);
			
			List<Publication> publis = author.getPublications();
			Publication publication = publis.get(0	);
			
			CompanionSite companionSite = publication.getCompanionSite();
			
			//OutputData out = companionSite.getCode().getOutputs().get(0);
			//System.out.println(out);
			
			execShare.exportation(companionSite, new FileOutputStream("companionSite.xml"), JRA.Format.XML, JRA.ImportExportOptions.COMPANION_SITE);
			
			execShare.exportation(companionSite, new FileOutputStream("code.xml"), JRA.Format.XML, JRA.ImportExportOptions.CODE_WITH_DATA);
			
			Code code = (Code)execShare.importation(companionSite, new FileInputStream("code.xml"), JRA.Format.XML, JRA.ImportExportOptions.CODE_WITH_DATA);
			
			System.out.println(code);
			
			CompanionSite companionSite1 = (CompanionSite)execShare.importation(companionSite, new FileInputStream("companionSite.xml"), JRA.Format.XML, JRA.ImportExportOptions.COMPANION_SITE);
			
			System.out.println(companionSite1);

			System.exit(0);
			
			//FileInputStream fis = new FileInputStream("data" + i + ".txt");
			
			//inputData.importData(fis, Format.XML);
			
			
						
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
