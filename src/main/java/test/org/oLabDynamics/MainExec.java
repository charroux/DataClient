package test.org.oLabDynamics;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.client.ExecShare;
import org.oLabDynamics.client.ExecShareConnexionFactory;
import org.oLabDynamics.client.Query;
import org.oLabDynamics.client.Query.FilterOperator;
import org.oLabDynamics.client.data.Author;
import org.oLabDynamics.client.data.Code;
import org.oLabDynamics.client.data.CompanionSite;
import org.oLabDynamics.client.data.InputData;
import org.oLabDynamics.client.data.OutputData;
import org.oLabDynamics.client.data.Program;
import org.oLabDynamics.client.data.Publication;
import org.oLabDynamics.client.data.ThematicSite;
import org.oLabDynamics.client.exec.Indicator;
import org.oLabDynamics.client.exec.RunningTask;
import org.oLabDynamics.client.exec.RunningTask.State;
import org.oLabDynamics.client.exec.RunningTaskListener;
import org.oLabDynamics.client.impl.ExecShareImpl;
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
public class MainExec {

	static final class TaskListener implements RunningTaskListener{

		@Override
		public void onProgress(State state, RunningTask runningTask) {
			Iterator<Indicator> indicators = runningTask.getIndicators().iterator();
			Indicator indicator;
			System.out.println("TaskListener : state = " + state);
			while(indicators.hasNext()){
				indicator = indicators.next();
				System.out.println("TaskListener : " + indicator);
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		try{
			
			ExecShare execShare = ExecShareImpl.getInstance();
					
			Query query = new Query("author");
			//query.addFilter("firstName", Query.FilterOperator.EQUAL, "Tintin");
			List<Author> authors = execShare.prepare(query);
			Author author = authors.get(0);
			
			List<Publication> publis = author.getPublications();
			Publication publication = publis.get(0);
			
			CompanionSite companionSite = publication.getCompanionSite();
			
			Code code = companionSite.getCode();
			if(code != null){
			
				List<InputData> inputs = code.getInputs();
				if(inputs != null){
					RunningTask runningTask = execShare.exec(companionSite);
					
					runningTask.addRunningTaskListener(new TaskListener());
					
					String[] indicatorNames = runningTask.getIndicatorNames();
					Indicator indicator;
					for(int i=0; i<indicatorNames.length; i++){
						indicator = runningTask.findIndicatorByName(indicatorNames[i]);
						System.out.println(indicator);
					}
					//List<OutputData> outputs = runningTask.getResult();
					//System.out.println(outputs);
					
					Thread.sleep(15000);
					
					System.out.println("cancel command is sent");
					
					boolean cancelled = runningTask.cancel(true);
					
					System.out.println("cancel has been done. Final state is : " + runningTask.getState());
					
					
					for(int i=0; i<indicatorNames.length; i++){
						indicator = runningTask.findIndicatorByName(indicatorNames[i]);
						System.out.println(indicator);
					}
				}

			}
			
						
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
