package test.org.oLabDynamics.jra;

import java.util.Iterator;
import java.util.List;

import org.oLabDynamics.jra.ExecShareImpl;

import jra.JRA;
import jra.JRAException;
import jra.Query;
import jra.exec.Indicator;
import jra.exec.RunningTask;
import jra.exec.RunningTaskListener;
import jra.exec.RunningTask.State;
import jra.model.Author;
import jra.model.Code;
import jra.model.CompanionSite;
import jra.model.InputData;
import jra.model.InputData.Kind;
import jra.model.InputData.Type;
import jra.model.Publication;

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
			
			JRA execShare = ExecShareImpl.getInstance();
					
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
					
					InputData inputData;
					
					for(int i=0; i<inputs.size(); i++){
						
						inputData = inputs.get(i);
						
						Kind kind = inputData.getKind();
						if(kind == Kind.MATRIX){
							Type type = inputData.getType();
							if(type == Type.INTEGER){
								Integer[][] matrix = (Integer[][]) inputData.getData();
								matrix[0][0] = 111;
							}							
						}
						if(kind == Kind.CHOICE){
							//Choice choice = (Choice)inputData.getData();
							//Object[] choices = choice.getChoices();
							//Object value = choice.getValue();
						}if(kind == Kind.RAW){
							try{
								inputData.getData();		// throw en exception since Raw data can not be manipulated in Java, use export method to get raw data into a file
							}catch(JRAException e){
								System.out.println(e);
							}
							
						}
					}
					
					RunningTask runningTask = execShare.exec(companionSite, inputs);
					
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
