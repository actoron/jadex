package jadex.bdi.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.simulation.helper.Constants;

import java.util.HashMap;

public class InitSimulationPlan extends Plan{

	public void body() {
		System.out.println("#InitSim# Initiating Simulation.");		
		parseXMLAndInit();
		IGoal goal = createGoal("StartSimulationExperiments");
		System.out.println("#InitSim# Starting first round of Simulation Experiments.");
		dispatchTopLevelGoal(goal);
		
	}

	private void parseXMLAndInit(){
		//getApplication.getProperty();
		
		//parse XML-File
		
		
		
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		facts.put(Constants.START_TIME, new Long(System.currentTimeMillis()));
		facts.put(Constants.SIMULATION_NAME, new String("MARS4Simulation"));
		facts.put(Constants.TOTAL_EXPERIMENT_COUNTER, new Integer(0));
		facts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(0));
		facts.put(Constants.CURRENT_EXPERIMENT_ROW, new Integer(0));
		facts.put(new String("TMP:EXPERIMENTS_TO_MAKE"), new Integer(5));
		
		getBeliefbase().getBelief("generalSimulationFacts").setFact(facts);
	};
}
