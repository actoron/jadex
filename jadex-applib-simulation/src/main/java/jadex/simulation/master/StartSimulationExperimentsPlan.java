package jadex.simulation.master;

import jadex.adapter.base.SComponentFactory;
import jadex.adapter.base.fipa.IDF;
import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.agr.AGRSpace;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentExecutionService;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.Optimization;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.ExperimentResult;
import jadex.simulation.model.result.RowResult;
import jadex.simulation.model.result.SimulationResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartSimulationExperimentsPlan extends Plan {

	public void body() {
		System.out.println("#StartSimulationExpPlan# Start Simulation Experiments at Master.");

		HashMap beliefbaseFacts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();		
		SimulationConfiguration simConf = (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact();
		
		System.out.println("#StartSimulationExpPlan# Path: " + simConf.getApplicationReference());
		

		// how many experiments to do within this row
		long experimentsPerRowToMake = ((Long) beliefbaseFacts.get(Constants.EXPERIMENTS_PER_ROW_TO_DO)).longValue();
		int totalRuns = ((Integer) beliefbaseFacts.get(Constants.TOTAL_EXPERIMENT_COUNTER)).intValue();
		int expInRow = ((Integer) beliefbaseFacts.get(Constants.ROW_EXPERIMENT_COUNTER)).intValue();
		int rowCounter = ((Integer) beliefbaseFacts.get(Constants.EXPERIMENT_ROW_COUNTER)).intValue();

		// Prepare values for the experiments of this row
		String fileName = simConf.getApplicationReference();
		String configName = simConf.getApplicationConfiguration();

		
		//prepare object that handles the result
		RowResult rowResult = new RowResult();
		rowResult.setStarttime(getClock().getTime());
		rowResult.setId(String.valueOf(rowCounter));
				
		
		// Put SimulationConfiguration into the application.xml as parameter for
		// the SimulationClient.
		Map simFacts = new HashMap();
		simFacts.put(Constants.SIMULATION_FACTS_FOR_CLIENT, simConf);
		// simFacts.put(Constants.EXPERIMENT_ID, experimentID);

		
		//Create Map that contains ObservedEvents, produced by the Executor
		HashMap<Long, ArrayList<ObservedEvent>> observedEvents = new HashMap<Long, ArrayList<ObservedEvent>>();
		
		
		// Put args into application. This args are passed to the
		// application.xml to parameterize application.
		Map args = new HashMap();
		args.put(Constants.SIMULATION_FACTS_FOR_CLIENT, simFacts);
		args.put(Constants.OBSERVED_EVENTS_MAP, observedEvents);

		// check, whether parameters have to be swept
		if (simConf.getOptimization().getParameterSweeping() != null) {
			sweepParameters(simConf, args);
		}

		for (long i = 0; i < experimentsPerRowToMake; i++) {

			String experimentID = rowCounter + "." + expInRow;
			String appName = simConf.getName() + experimentID;

			args.put(Constants.EXPERIMENT_ID, experimentID);
			((HashMap) args.get(Constants.SIMULATION_FACTS_FOR_CLIENT)).put(Constants.EXPERIMENT_ID, experimentID);

			startApplication(appName, fileName, configName, args);
			
			
			System.out.println("#StartSimulationExpPlan# Started new Simulation Experiment. Nr.:" + experimentID + "(" + totalRuns + ") with Optimization Values: " + simConf.getOptimization().getData().getName()  + " = " + simConf.getOptimization().getParameterSweeping().getCurrentValue());
			totalRuns++;
			expInRow++;

			
			waitForInternalEvent("triggerNewExperiment");
			System.out.println("#StartSimulationExpPlan# Received Results of Client!!!!");
			// HACK: Ein warten scheint notwendig zu sein..., damit Ausführung
			// korrekt läuft.
			waitFor(2000);
			// System.out.println("2Received Results!!!!");
			beliefbaseFacts.put(Constants.TOTAL_EXPERIMENT_COUNTER, new Integer(totalRuns));
			beliefbaseFacts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(expInRow));
			getBeliefbase().getBelief("generalSimulationFacts").setFact(beliefbaseFacts);
		}

		// Increment row counter
		rowCounter++;
		beliefbaseFacts.put(Constants.EXPERIMENT_ROW_COUNTER, new Integer(rowCounter));
		getBeliefbase().getBelief("generalSimulationFacts").setFact(beliefbaseFacts);
		
		
		//store results of row
		HashMap<Integer, ExperimentResult> experimentResults = (HashMap<Integer, ExperimentResult>) getBeliefbase().getBelief("experimentResults").getFact();//contains the results of the experiments done in this row
		HashMap rowResults = (HashMap) getBeliefbase().getBelief("rowResults").getFact();
		
		
//		System.out.println("#StartSimEx# tttttttttttttttttttttttttt.");
//		for(ExperimentResult ttt : experimentResults.values()){
//			System.out.println("#StartSimEx# ttt " + ttt.toString());
//			XMLHandler.writeXML(ttt, "rowRes.xml", ExperimentResult.class);	
//		}
//		
		ArrayList<ExperimentResult> experimentList = new ArrayList<ExperimentResult>(experimentResults.values());
		rowResult.setExperimentsResults(experimentList);
		rowResult.setEndtime(getClock().getTime());
		rowResult.setName("Tmp-Test");
		rowResult.setOptimizationName(experimentList.get(0).getOptimizationParameterName());
		rowResult.setOptimizationValue(experimentList.get(0).getOptimizationValue());
		
		rowResults.put(rowResult.getId(), rowResult);
		
		getBeliefbase().getBelief("experimentResults").setFact(new HashMap());
		getBeliefbase().getBelief("rowResults").setFact(rowResults);
		
//		System.out.println("#StartSimEx# Try to write RowResult to XML-File.");
		
		System.out.println("#StartSimExp# Write Row Res to XML");
		XMLHandler.writeXML(rowResult, "rowRes" +".xml", RowResult.class);
		
		//evaluate row
		dispatchInternalEvent(createInternalEvent("triggerExperimentRowEvaluation"));
	}

	private void startClientSimulators() {
		IGoal ca = createGoal("ams_create_agent");
		String type = "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\simulation\\client\\ClientSimulator.agent.xml";
		ca.getParameter("type").setValue(type);

		Map args = new HashMap();
		// java jadex.adapter.standalone.Platform
		// "hello:jadex.examples.helloworld.HelloWorld(default, msg=\"Hi!\")"
		args.put("msg", "Novo");
		// args.put("dealer", dealeraid);
		ca.getParameter("arguments").setValue(args);
		// agent.dispatchTopLevelGoalAndWait(start);

		// ca.getParameter("type").setValue("/jadex-applications-bdi/src/main/java/jadex/bdi/simulation/SimulationManager.agent.xml");
		//	
		// // Map<String, Object> arguments = new HashMap<String, Object>(); //
		// Hack:
		// // this works only for agents arguments.put("conf", ap); // that are
		// // started on the same platform
		// // arguments.put("current_server", appsrv); // ...
		//
		// // arguments.put("Position", brokerObj.getPosition());
		// // arguments.put("RoadMap", createManipulatedMap());
		// // arguments.put("RoutingStrategy", brokerObj.getRoutingStrategy());
		// // ca.getParameter("arguments").setValue(arguments); // ...
		// // System.out.println("1 ->" + ca.getLifecycleState());
		dispatchSubgoalAndWait(ca);
	}

	private void startApplication(String appName, String fileName, String configName, Map args) {
		// private void startApplication(String experimentID) {

		// SimulationConfiguration simConf = (SimulationConfiguration)
		// getBeliefbase().getBelief("simulationConf").getFact();
		//		
		// String appName = simConf.getName() + experimentID;
		// String fileName = simConf.getApplicationReference();
		// String configName = simConf.getApplicationConfiguration();
		//
		// //Put args into application
		// Map simFacts = new HashMap();
		// simFacts.put(Constants.SIMULATION_FACTS_FOR_CLIENT, simConf);
		// simFacts.put(Constants.EXPERIMENT_ID, experimentID);
		//		
		// Map args = new HashMap();
		// args.put(Constants.EXPERIMENT_ID, experimentID);
		// args.put(Constants.SIMULATION_FACTS_FOR_CLIENT, simFacts);
		//		
		// //check, whether parameters have to be swept
		// if(simConf.getOptimization().getParameterSweeping() != null ){
		// sweepParameters(simConf, args);
		// }

		// args.put(new String("evaporation_rat2e"), new Double(0.45));
		// args.put(new String("tmp_mission_time"), new String("Antisaaaaa"));
		// args.put(new String("simulationFacts"), (HashMap)
		// getBeliefbase().getBelief("generalSimulationFacts").getFact());
		// args.put(new String("nrr"), new Integer(20));
		// args.put(new String("al"), new Integer(19));
		// <argument name="evaporation_rate" typename="Double">0.03</argument>

		try {
			IComponentExecutionService executionService = (IComponentExecutionService) getScope().getServiceContainer().getService(IComponentExecutionService.class);

			executionService.createComponent(appName, fileName, configName, args, false, null, null, null);

		} catch (Exception e) {
			// JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
			// "Could not start application: "+e,
			// "Application Problem", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Could not start application...." + e);
		}
	}

	/**
	 * TODO: differentiate between step and values!!!!
	 * 
	 * @param simConf
	 * @param args
	 */
	private void sweepParameters(SimulationConfiguration simConf, Map args) {
		Optimization opt = simConf.getOptimization();
		String parameterName = opt.getData().getName();
		int val = -1;

		if (opt.getParameterSweeping().getParameterSweepCounter() == 0) {
			val = opt.getParameterSweeping().getConfiguration().getStart();

		} else {
			int step = opt.getParameterSweeping().getConfiguration().getStep();
			int currentVal = opt.getParameterSweeping().getCurrentValue();
			val = currentVal + step;
		}

		// update SimulationConf to be up to date
		opt.getParameterSweeping().setCurrentValue(val);
		opt.getParameterSweeping().incrementParameterSweepCounter();

		// parametrize application-xml
		args.put(parameterName, new Integer(val));
	}
}
