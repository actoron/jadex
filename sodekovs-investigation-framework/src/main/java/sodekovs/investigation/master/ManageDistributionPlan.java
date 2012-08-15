package sodekovs.investigation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sodekovs.investigation.helper.Constants;
import sodekovs.investigation.model.InvestigationConfiguration;
import sodekovs.investigation.services.IRemoteSimulationExecutionService;
import sodekovs.util.misc.GlobalConstants;

public class ManageDistributionPlan extends Plan
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5708005325242003053L;


	@Override
	public void body() {		
		startApplicationRemotley((Map) getParameter("applicationArgs").getValue(), (HashMap<String,Object>) getParameter("clientArgs").getValue());
	}

	
	private void startApplicationRemotley(Map applicationArgs, HashMap<String,Object> clientArgs) {
		
		InvestigationConfiguration investigationConf = (InvestigationConfiguration) getBeliefbase().getBelief("investigationConf").getFact();
		HashMap beliefbaseFacts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();

		try {
			Collection<IRemoteSimulationExecutionService> services = null;

			int counter = 0;
			do {
				// find appropriate service
				System.out.println("#ManageDistributionPlan# Searching for remote simulation services.");
				services = (Collection<IRemoteSimulationExecutionService>) SServiceProvider.getServices(getScope().getServiceContainer(), IRemoteSimulationExecutionService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(this);
				System.out.println("#ManageDistributionPlan# Nr. of found remote services: " + services.size());
				if (services.size() <= 0) {
					System.out.println("#ManageDistributionPlan# Could not find remote simulation execution service! Retry in 5 sec.");
					waitFor(5000);
				}
				counter++;
			} while (services.size() <= 0 && counter < 5);

			if (services.size() > 0) {


				System.out.println("#ManageDistributionPlan# Distributed new Simulation Experiment remotely. Nr.:" + clientArgs.get(GlobalConstants.EXPERIMENT_ID) + "(" + beliefbaseFacts.get(Constants.TOTAL_EXPERIMENT_COUNTER) + ") with Optimization Values: "
						+ investigationConf.getOptimization().getParameterSweeping().getCurrentConfiguration());
				

				//TODO: current workload einbeziehen bei der auswahl von services
				IFuture fut = services.iterator().next().executeExperiment(applicationArgs, clientArgs);
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
			System.out.println("Could not start simulation experiment for remote execution. " + e.toString());
		}
	}
}
