package jadex.simulation.master;

import java.util.ArrayList;
import java.util.HashMap;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.TimeConverter;
import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.result.RowResult;
import jadex.simulation.model.result.SimulationResult;
import jadex.simulation.persist.HelpElement;

/**
 * Compute the results of one row of simulation experiments, e.g. experiments with the same setting but still different cause of non-determinism.
 * 
 * @author Ante Vilenica
 * 
 */
public class ComputeExperimentRowResultsPlan extends Plan {

	public void body() {
		// TODO Auto-generated method stub
		System.out.println("#ComputeExperimentRowResultsPlan# Compute Row Results");
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int rowCounter = ((Integer) facts.get(Constants.EXPERIMENT_ROW_COUNTER)).intValue();
		int rowsDoTo = ((Integer) facts.get(Constants.ROWS_TO_DO)).intValue();

		// check terminate condition: time or counter or semantic
		if (rowCounter == rowsDoTo) {
			System.out.println("#ComputeExperimentRowResultsPlan# Simulation finished!");

			// store result as XML-File
			HashMap rowResults = (HashMap) getBeliefbase().getBelief("rowResults").getFact();			

			
			SimulationResult result = new SimulationResult();			
			result.setStarttime(((Long) facts.get(Constants.SIMULATION_START_TIME)).longValue());
			result.setEndtime(getClock().getTime());
			result.setName("missing");
			result.setRowsResults(new ArrayList(rowResults.values()));
			
			System.out.println("Write Res of Simulation to XML");			
			XMLHandler.writeXML(result, "SimRes" + result.getStarttime() + ".xml", SimulationResult.class);
			
		} else {

			// optimize --> put new parameters
			// Start new Row

			facts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(0));
			getBeliefbase().getBelief("generalSimulationFacts").setFact(facts);

			IGoal goal = createGoal("StartSimulationExperiments");
			System.out.println("#InitSim# Starting " + rowCounter + ". round of Simulation Experiments.");
			dispatchTopLevelGoal(goal);
		}
	}
}
