package jadex.bdi.simulation.master;

import java.util.HashMap;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.simulation.helper.Constants;

/**
 * Compute the results of one row of simulation experiments, e.g. experiments with the same setting but still different cause of non-determinism.
 * @author Ante Vilenica
 *
 */          
public class ComputeExperimentRowResultsPlan extends Plan{

	public void body() {
		// TODO Auto-generated method stub
		System.out.println("#ComputeExperimentRowResultsPlan# Compute Row Results");
		
		
		
		
		//check terminate condition
		//optimize --> put new parameters
		// finish
		
		//Start new Row
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int experimentRow = ((Integer) facts.get(Constants.CURRENT_EXPERIMENT_ROW)).intValue();
		experimentRow++;		
		facts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(0));
		facts.put(Constants.CURRENT_EXPERIMENT_ROW, new Integer(experimentRow));
		
		getBeliefbase().getBelief("generalSimulationFacts").setFact(facts);
		
		IGoal goal = createGoal("StartSimulationExperiments");
		System.out.println("#InitSim# Starting "+ experimentRow + ". round of Simulation Experiments.");
		dispatchTopLevelGoal(goal);
	}

}
