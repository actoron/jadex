package jadex.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.EvaluationResult;
import jadex.simulation.model.result.IntermediateResult;
import jadex.simulation.model.result.RowResult;
import jadex.simulation.model.result.SimulationResult;
import jadex.simulation.persist.LogWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
		IntermediateResult interRes = (IntermediateResult) getBeliefbase().getBelief("intermediateResults").getFact();
		// check terminate condition: time or counter or semantic
		if (rowCounter == rowsDoTo) {

			// store result as XML-File
			HashMap rowResults = (HashMap) getBeliefbase().getBelief("rowResults").getFact();

			SimulationResult result = new SimulationResult();
			result.setStarttime(((Long) facts.get(Constants.SIMULATION_START_TIME)).longValue());
			result.setEndtime(getClock().getTime());
			result.setName("missing");
			result.setRowsResults(new ArrayList(rowResults.values()));

			System.out.println("#ComputeExperimentRowResultsPlan# Simulation finished. Write Res of Simulation to XML!");
			XMLHandler.writeXMLToFile(result, "SimRes" + result.getStarttime() + ".xml", SimulationResult.class);
			
			
			try {
				doShortEvaluation(rowResults, interRes,  "Final");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			
			//Print and persist intermediateResults
			System.out.println("#ComputeExperimentRowResultsPlan# Printing intermediate results!");
			try {
				doShortEvaluation((HashMap) getBeliefbase().getBelief("rowResults").getFact(), interRes, "Intermediate");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// optimize --> put new parameters
			// Start new Row

			facts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(0));
			getBeliefbase().getBelief("generalSimulationFacts").setFact(facts);
			
			//re-init intermediate results since of the start of a new row
			getBeliefbase().getBelief("intermediateResults").setFact(new IntermediateResult(rowCounter, 0, (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact()));
			
			IGoal goal = createGoal("StartSimulationExperiments");
			System.out.println("#InitSim# Starting " + rowCounter + ". round of Simulation Experiments.");
			dispatchTopLevelGoal(goal);
		}
	}

	/**
	 * Do short evaluation to see the most important results of the simulation
	 * 
	 * @param rowResults
	 * @throws UnsupportedEncodingException 
	 */
	private void doShortEvaluation(HashMap rowResults, IntermediateResult interRes, String fileAppendix) throws UnsupportedEncodingException {

		SimulationConfiguration simConf = (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact();
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();

		EvaluationResult evalRes = new EvaluationResult();
		evalRes.setNumberOfRows(simConf.getRunConfiguration().getGeneral().getRows());
		evalRes.setExperimentsPerRow(simConf.getRunConfiguration().getRows().getExperiments());
		evalRes.setSimulationDuration(getClock().getTime() - ((Long) facts.get(Constants.SIMULATION_START_TIME)).longValue());
		evalRes.setSimulationStartime(((Long) facts.get(Constants.SIMULATION_START_TIME)).longValue());
		evalRes.setRowResults(new ArrayList<RowResult>(rowResults.values()));

		System.out.println(evalRes.toString());
//		LogWriter logWriter = new LogWriter();
//		logWriter.log(evalRes.toString());
	
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("SimRes"  + evalRes.getSimulationStartime() + "-" + fileAppendix + ".txt"));
			out.write(evalRes.toString());
			out.close();
		} catch (IOException e) {
		}
		/*
		try {
			System.out.println("******Fetching from DB*****");
			logWriter.logReader();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

	}
}
