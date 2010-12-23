package jadex.simulation.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.remote.IRemoteSimulationExecutionService;

public class ManageDistributionPlan extends Plan
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5708005325242003053L;


	@Override
	public void body() {
//		// TODO Auto-generated method stub
//		System.out.println("Startet Simple Plan");
//		
//		for(int i=0; i< 4; i++){
//			System.out.println("Simple Plan running...");
//			waitFor(2500);
//		}
//		
//		System.out.println("Simple Plan --> 10sec");
//		waitFor(10000);
//		System.out.println("Simple Plan:  Dispatch Event");
//		dispatchInternalEvent(createInternalEvent("triggerNextExperiment"));
//		System.out.println("Simple Plan terminated");
		
		startApplicationRemotley((Map) getParameter("applicationArgs").getValue(), (HashMap<String,Object>) getParameter("clientArgs").getValue());
	}

	
	private void startApplicationRemotley(Map applicationArgs, HashMap<String,Object> clientArgs) {
		
		SimulationConfiguration simConf = (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact();
		HashMap beliefbaseFacts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();

		try {
			ArrayList<IRemoteSimulationExecutionService> services = null;

			int counter = 0;
			do {
				// find appropriate service
				System.out.println("#ManageDistributionPlan# Searching for remote simulation services.");
				services = (ArrayList<IRemoteSimulationExecutionService>) SServiceProvider.getServices(getScope().getServiceProvider(), IRemoteSimulationExecutionService.class, true, true).get(this);
				System.out.println("#ManageDistributionPlan# Nr. of found remote services: " + services.size());
				if (services.size() <= 0) {
					System.out.println("#ManageDistributionPlan# Could not find remote simulation execution service! Retry in 5 sec.");
					waitFor(5000);
				}
				counter++;
			} while (services.size() <= 0 && counter < 5);

			if (services.size() > 0) {


				System.out.println("#ManageDistributionPlan# Distributed new Simulation Experiment remotely. Nr.:" + clientArgs.get(Constants.EXPERIMENT_ID) + "(" + beliefbaseFacts.get(Constants.TOTAL_EXPERIMENT_COUNTER) + ") with Optimization Values: "
						+ simConf.getOptimization().getParameterSweeping().getCurrentConfiguration());
				
				IFuture fut = services.get(0).executeExperiment(applicationArgs, clientArgs);
//				fut.addResultListener(new IResultListener() {
//					public void resultAvailable(Object source, Object result) {
//						System.out.println("#ManageDistributionPlan#Received res from remote simulation execution");
//
//						// Start Evaluation of single experiment result
//						IGoal eval = (IGoal) getGoalbase().createGoal("EvaluateSingleResult");
//						eval.getParameter("args").setValue(result);
//						getGoalbase().dispatchTopLevelGoal(eval);
//					}
//
//					public void exceptionOccurred(Object source, Exception exception) {
//						System.out.println("#ManageDistributionPlan#Error: Remote simulation execution failed! " + exception);
//					}
//				});

				 Map resMap = (Map) fut.get(this);
				 System.out.println("#StartSimulationExpPlan# RECEIVED res at Master...");
				 IGoal eval = (IGoal)
				 getGoalbase().createGoal("EvaluateSingleResult");
				 eval.getParameter("args").setValue(resMap);
				 getGoalbase().dispatchTopLevelGoal(eval);

			} else {
				System.out.println("Error: Could not find remote simulation execution service!");
			}

		} catch (Exception e) {
			System.out.println("Could not start simulation experiment for remote execution. " + e);
		}
	}
}
