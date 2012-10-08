package jadex.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.simulation.evaluation.BikeSharingEvaluation;
import jadex.simulation.evaluation.EvaluateExperiment;
import jadex.simulation.evaluation.EvaluateRow;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.ExperimentResult;
import jadex.simulation.model.result.IntermediateResult;
import jadex.simulation.model.result.RowResult;
import jadex.simulation.model.result.SimulationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sodekovs.util.misc.XMLHandler;

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
		HashMap simulationFacts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int rowCounter = ((Integer) simulationFacts.get(Constants.EXPERIMENT_ROW_COUNTER)).intValue();
		int rowsDoTo = ((Integer) simulationFacts.get(Constants.ROWS_TO_DO)).intValue();
		// IntermediateResult interRes = (IntermediateResult) getBeliefbase().getBelief("intermediateResults").getFact();
		HashMap rowResults = (HashMap) getBeliefbase().getBelief("rowResults").getFact();

		// 1. Evaluate Rows and their Experiments
		evaluate(rowResults);

		// 2. Print current state of results
		String resultsAsString = print(rowResults);
		System.out.println(resultsAsString);

		// 3. Store results
		storeResults(rowResults, simulationFacts, rowCounter, rowsDoTo, resultsAsString);


		//Do application specific evaluation, if required
//		compareSimulationWithRealData(rowResults);
		
		
		// if (rowCounter == rowsDoTo) {
		//
		// // store result as XML-File
		//
		// SimulationResult result = new SimulationResult();
		// result.setStarttime(((Long) simulationFacts.get(Constants.SIMULATION_START_TIME)).longValue());
		// result.setEndtime(getClock().getTime());
		// result.setName("missing");
		// result.setRowsResults(new ArrayList(rowResults.values()));
		//
		// System.out.println("#ComputeExperimentRowResultsPlan# Simulation finished. Write Res of Simulation to XML!");
		// XMLHandler.writeXMLToFile(result, "SimRes" + result.getStarttime() + ".xml", SimulationResult.class);
		//
		// try {
		// doShortEvaluation(rowResults, "Final");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// } else {
		//
		// // Print and persist intermediateResults
		// System.out.println("#ComputeExperimentRowResultsPlan# Printing intermediate results!");
		// try {
		// doShortEvaluation(rowResults, "Intermediate");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// optimize --> put new parameters
		// Start new Row

		// Simulation has not finished. Start next row
		if (rowCounter < rowsDoTo) {

			simulationFacts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(0));
			getBeliefbase().getBelief("generalSimulationFacts").setFact(simulationFacts);

			// re-init intermediate results since of the start of a new row
			getBeliefbase().getBelief("intermediateResults").setFact(new IntermediateResult(rowCounter, 0, (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact()));

			IGoal goal = createGoal("StartSimulationExperiments");
			System.out.println("#InitSim# Starting " + rowCounter + ". round of Simulation Experiments.");
			dispatchTopLevelGoal(goal);
		}else{			
			//Do application specific evaluation
			compareSimulationWithRealData(rowResults);
		}

	}

	// /**
	// * Do short evaluation to see the most important results of the simulation
	// *
	// * @param rowResults
	// * @throws UnsupportedEncodingException
	// */
	// private void doShortEvaluation(HashMap rowResults, String fileAppendix) throws UnsupportedEncodingException {
	//
	// SimulationConfiguration simConf = (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact();
	// HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
	//
	// for (Iterator<String> it = rowResults.keySet().iterator(); it.hasNext();) {
	//
	// ArrayList<HashMap<String, HashMap<String, ArrayList<Object>>>> preparedExperimentResList = new ArrayList<HashMap<String, HashMap<String, ArrayList<Object>>>>();
	//
	// RowResult rowRes = (RowResult) rowResults.get(it.next());
	// ArrayList<ExperimentResult> experimentResultsList = rowRes.getExperimentsResults();
	//
	// for (ExperimentResult experimentResult : experimentResultsList) {
	// // Separate/transform observed events into a new data structure which enables their statistical evaluation
	// experimentResult.setSortedObserveEventsMap(EvaluateExperiment.separateData(experimentResult));
	// preparedExperimentResList.add(experimentResult.getSortedObserveEventsMap());
	// System.out.println(experimentResult.toStringShort());
	// }
	//
	// // Separate/transform the data again. Required in order to access the properties of the object instances in ALL experiments of this row. Till now they are separated by Experiment, now they
	// // will be separated by objectInstance.
	// HashMap<String, HashMap<String, ArrayList<ArrayList<Object>>>> preparedRowData = EvaluateRow.separateData(preparedExperimentResList);
	// rowRes.setEvaluatedRowData(EvaluateRow.evaluateRowData(preparedRowData));
	//
	// System.out.println(rowRes.toStringShortNew());
	// }
	// }

	private void evaluate(HashMap rowResults) {

		for (Iterator<String> it = rowResults.keySet().iterator(); it.hasNext();) {

			ArrayList<HashMap<String, HashMap<String, ArrayList<Object>>>> preparedExperimentResList = new ArrayList<HashMap<String, HashMap<String, ArrayList<Object>>>>();

			RowResult rowRes = (RowResult) rowResults.get(it.next());

			// Check whether this row has already been evaluated, avoid therefore unnecessary work.
			if (rowRes.getEvaluatedRowData() == null) {
				ArrayList<ExperimentResult> experimentResultsList = rowRes.getExperimentsResults();

				for (ExperimentResult experimentResult : experimentResultsList) {
					// Separate/transform observed events into a new data structure which enables their statistical evaluation
					experimentResult.setSortedObserveEventsMap(EvaluateExperiment.separateData(experimentResult));
					preparedExperimentResList.add(experimentResult.getSortedObserveEventsMap());
					// System.out.println(experimentResult.toStringShort());
				}

				// Separate/transform the data again. Required in order to access the properties of the object instances in ALL experiments of this row. Till now they are separated by Experiment, now
				// they will be separated by objectInstance.
				HashMap<String, HashMap<String, ArrayList<ArrayList<Object>>>> preparedRowData = EvaluateRow.separateData(preparedExperimentResList);
				rowRes.setEvaluatedRowData(EvaluateRow.evaluateRowData(preparedRowData));

				// System.out.println(rowRes.toStringShortNew());
			}
		}
	}

	private void storeResults(HashMap rowResults, HashMap simFacts, int rowCounter, int rowsDoTo, String resultsAsString) {

		// Simulation has finished. Store final result
		if (rowCounter == rowsDoTo) {

			// store result as XML-File
			SimulationResult result = new SimulationResult();
			result.setStarttime(((Long) simFacts.get(Constants.SIMULATION_START_TIME)).longValue());
			result.setEndtime(getClock().getTime());
			result.setName("missing");
			result.setRowsResults(new ArrayList(rowResults.values()));

			System.out.println("\n\n#ComputeExperimentRowResultsPlan# Simulation finished. Write Results of Simulation to XML!");
			XMLHandler.writeXMLToFile(result, "SimRes" + result.getStarttime() + ".xml", SimulationResult.class);

			// Store also the evaluation in a file
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("SimulationEVALUATIONResults" + "-" + String.valueOf(getClock().getTime()) + ".txt"));
				out.write(resultsAsString);
				out.close();
			} catch (IOException e) {
			}
		} else {
			// Store intermediate results

			// EvaluationResult evalRes = new EvaluationResult();
			// evalRes.setNumberOfRows(simConf.getRunConfiguration().getGeneral().getRows());
			// evalRes.setExperimentsPerRow(simConf.getRunConfiguration().getRows().getExperiments());
			// evalRes.setSimulationDuration(getClock().getTime() - ((Long) facts.get(Constants.SIMULATION_START_TIME)).longValue());
			// evalRes.setSimulationStartime(((Long) facts.get(Constants.SIMULATION_START_TIME)).longValue());
			// evalRes.setRowResults(new ArrayList<RowResult>(rowResults.values()));
			//
			// System.out.println(evalRes.toString());
			// LogWriter logWriter = new LogWriter();
			// logWriter.log(evalRes.toString());

			// try {
			// BufferedWriter out = new BufferedWriter(new FileWriter("SimRes" + evalRes.getSimulationStartime() + "-" + fileAppendix + ".txt"));
			// out.write(evalRes.toString());
			// out.close();
			// } catch (IOException e) {
			// }
			/*
			 * try { System.out.println("******Fetching from DB*****"); logWriter.logReader(); } catch (SQLException e) { // TODO Auto-generated catch block e.printStackTrace(); } catch (IOException
			 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 */
		}
	}

	/**
	 * Do short evaluation to see the most important results of the simulation
	 * 
	 * @param rowResults
	 * @throws UnsupportedEncodingException
	 */
	private String print(HashMap rowResults) {

		// SimulationConfiguration simConf = (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact();
		// HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		StringBuffer buffer = new StringBuffer();

		for (Iterator<String> it = rowResults.keySet().iterator(); it.hasNext();) {

			ArrayList<HashMap<String, HashMap<String, ArrayList<Object>>>> preparedExperimentResList = new ArrayList<HashMap<String, HashMap<String, ArrayList<Object>>>>();

			RowResult rowRes = (RowResult) rowResults.get(it.next());

			// Print evaluation results for the row
			buffer.append(rowRes.toStringShortNew());
			// System.out.println(rowRes.toStringShortNew());

			ArrayList<ExperimentResult> experimentResultsList = rowRes.getExperimentsResults();

			// Print evaluation results for the experiments of this row
			for (ExperimentResult experimentResult : experimentResultsList) {
				// Separate/transform observed events into a new data structure which enables their statistical evaluation
				// experimentResult.setSortedObserveEventsMap(EvaluateExperiment.separateData(experimentResult));
				// preparedExperimentResList.add(experimentResult.getSortedObserveEventsMap());
				buffer.append("Values of the single experiments, conducted within this row: ");
				buffer.append(experimentResult.toStringShort());
				// System.out.println("Values of the single experiments, conducted within this row: ");
				// System.out.println(experimentResult.toStringShort());
			}

			// Separate/transform the data again. Required in order to access the properties of the object instances in ALL experiments of this row. Till now they are separated by Experiment, now they
			// will be separated by objectInstance.
			// HashMap<String, HashMap<String, ArrayList<ArrayList<Object>>>> preparedRowData = EvaluateRow.separateData(preparedExperimentResList);
			// rowRes.setEvaluatedRowData(EvaluateRow.evaluateRowData(preparedRowData));

			// System.out.println(rowRes.toStringShortNew());
		}
		return buffer.toString();
	}
	
	//Application specific evaluation --> compare simulations results with real data
	private void compareSimulationWithRealData(HashMap rowResults){
		
		for (Iterator<String> it = rowResults.keySet().iterator(); it.hasNext();) {
		
			BikeSharingEvaluation bikeSharEval = new BikeSharingEvaluation(((RowResult) rowResults.get(it.next())).getEvaluatedRowData());
			bikeSharEval.compare();
			
			System.out.println("\n\n\n Results of comparison between simulation data and real data");
			System.out.println(bikeSharEval.resultsToString());
		}
	}
}
