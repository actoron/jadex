package jadex.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.simulation.evaluation.EvaluateExperiment;
import jadex.simulation.evaluation.EvaluateRow;
import jadex.simulation.evaluation.bikesharing.BikeSharingEvaluation;
import jadex.simulation.evaluation.bikesharing.EvalStockLevelData;
import jadex.simulation.evaluation.bikesharing.xml.EvaluatedBikeStationShortList;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.ObservedEvent;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sodekovs.util.misc.XMLHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Compute the results of one row of simulation experiments, e.g. experiments with the same setting but still different cause of non-determinism.
 * 
 * @author Ante Vilenica
 * 
 */
public class ComputeExperimentRowResultsPlan extends Plan {

	public void body() {
		// TODO Auto-generated method stub
		SimulationConfiguration simConf = (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact();
		System.out.println("#ComputeExperimentRowResultsPlan# Compute Row Results");
		HashMap simulationFacts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int rowCounter = ((Integer) simulationFacts.get(Constants.EXPERIMENT_ROW_COUNTER)).intValue();
		int rowsDoTo = ((Integer) simulationFacts.get(Constants.ROWS_TO_DO)).intValue();
		// IntermediateResult interRes = (IntermediateResult) getBeliefbase().getBelief("intermediateResults").getFact();
		HashMap rowResults = (HashMap) getBeliefbase().getBelief("rowResults").getFact();

		// New parameter exerimentDescription: Contains short description of the evaluation: settings etc.
		String experimentDescription = simConf.getDescription() != null ? simConf.getDescription() : "";

		// 1. Evaluate Rows and their Experiments
		evaluate(rowResults);

		// 2. Print current state of results
		String resultsAsString = print(rowResults);
		System.out.println(resultsAsString);

		// 3. Store results
		storeResults(rowResults, simulationFacts, rowCounter, rowsDoTo, resultsAsString, experimentDescription);

		// Do application specific evaluation, if required
		// compareSimulationWithRealData(rowResults);

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
		} else {
			// Do application specific evaluation
			if (simConf.getApplicationReference().indexOf("BikesharingSimulation") != -1) {
				// Do specific evaluation for Bikesharing
				compareSimulationWithRealData(rowResults, experimentDescription);
			} else if (simConf.getApplicationReference().contains("MarsWorld")) {
				// TODO: Auswertung der MarsWorld
				evaluteMarsWorldMedian(rowResults, experimentDescription);
				evaluteMarsWorldCSV(rowResults, experimentDescription);
			}
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

	@SuppressWarnings("rawtypes")
	private void evaluteMarsWorldCSV(HashMap rowResults, String experimentDescription) {
		StringBuffer mb = new StringBuffer();
		mb.append("time,sensedOre,producedOre,collectedOre,targetSeen,targetAnalyzed,targetProduced,votingAttempts,adaptations,failures\n");
		
		for (Object o : rowResults.values()) {
			RowResult result = (RowResult) o;
			List<ExperimentResult> experimentResults = result.getExperimentsResults();
			List<Integer> experimentSizes = new ArrayList<Integer>();
			// single experiment eval
			for (ExperimentResult experimentResult : experimentResults) {
				StringBuffer sb = new StringBuffer();
				experimentSizes.add(experimentResult.getEvents().size());
				
				List<ObservedEvent> events = experimentResult.getEvents();
				sb.append("time,sensedOre,producedOre,collectedOre,targetSeen,targetAnalyzed,targetProduced,votingAttempts,adaptations,failures\n");
				for (ObservedEvent event : events) {
					Map properties = event.getObservedObjectProperties();
					Double time = Double.valueOf((String) properties.get("time"));
					Integer collectedOre = Integer.valueOf((String) properties.get("collectedOre"));
					Integer sensedOre = Integer.valueOf((String) properties.get("sensedOre"));
					Integer producedOre = Integer.valueOf((String) properties.get("producedOre"));
					Integer targetSeen = Integer.valueOf((String) properties.get("targetSeen"));
					Integer targetAnalyzed = Integer.valueOf((String) properties.get("targetAnalyzed"));
					Integer targetProduced = Integer.valueOf((String) properties.get("targetProduced"));
					Integer votingAttempts = Integer.valueOf((String) properties.get("votingAttempts"));
					Integer adaptations = Integer.valueOf((String) properties.get("adaptations"));
					Integer failures = Integer.valueOf((String) properties.get("failures"));
					
					sb.append(time + "," + sensedOre + "," + producedOre + "," + collectedOre + "," + targetSeen + "," + targetAnalyzed + "," + targetProduced + "," + votingAttempts + "," + adaptations + "," + failures + "\n");
				}
				
				String id = getDateAsString();
				// Persists result in file on disk as txt file
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter("MarsWorld4SASOSingle" + "-" + id + "-" + experimentResult.getId() + ".csv"));
					out.write(sb.toString());
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// median eval of all experiments
			for (int i = 0; i < Collections.min(experimentSizes); i++) {
				double time = 0.0, collectedOre = 0.0, sensedOre = 0.0, producedOre = 0.0, targetSeen = 0.0, targetAnalyzed = 0.0, targetProduced = 0.0, votingAttempts = 0.0, adaptations = 0.0, failures = 0.0;
				
				for (ExperimentResult experimentResult : experimentResults) {
					ObservedEvent event = experimentResult.getEvents().get(i);
					Map properties = event.getObservedObjectProperties();
					time += Double.valueOf((String) properties.get("time"));
					collectedOre += Double.valueOf((String) properties.get("collectedOre"));
					sensedOre += Double.valueOf((String) properties.get("sensedOre"));
					producedOre += Double.valueOf((String) properties.get("producedOre"));
					targetSeen += Double.valueOf((String) properties.get("targetSeen"));
					targetAnalyzed += Double.valueOf((String) properties.get("targetAnalyzed"));
					targetProduced += Double.valueOf((String) properties.get("targetProduced"));
					votingAttempts += Double.valueOf((String) properties.get("votingAttempts"));
					adaptations += Double.valueOf((String) properties.get("adaptations"));
					failures += Double.valueOf((String) properties.get("failures"));
				}
				
				time /= experimentResults.size();
				collectedOre /= experimentResults.size();
				sensedOre /= experimentResults.size();
				producedOre /= experimentResults.size();
				targetSeen /= experimentResults.size();
				targetAnalyzed /= experimentResults.size();
				targetProduced /= experimentResults.size();
				votingAttempts /= experimentResults.size();
				adaptations /= experimentResults.size();
				failures /= experimentResults.size();
				
				mb.append(time + "," + sensedOre + "," + producedOre + "," + collectedOre + "," + targetSeen + "," + targetAnalyzed + "," + targetProduced + "," + votingAttempts + "," + adaptations + "," + failures + "\n");
			}
			
			String id = getDateAsString();
			// Persists result in file on disk as txt file
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("MarsWorld4SASOMedian" + "-" + id + "-" + ".csv"));
				out.write(mb.toString());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void evaluteMarsWorldMedian(HashMap rowResults, String experimentDescription) {
		List<Double> times = new ArrayList<Double>();
		List<Double> costs = new ArrayList<Double>();
		List<Double> ores = new ArrayList<Double>();
		List<Double> adaptationCosts = new ArrayList<Double>();
		List<Integer> collectedOres = new ArrayList<Integer>();
		List<Integer> sensedOres = new ArrayList<Integer>();
		List<Integer> producedOres = new ArrayList<Integer>();
		List<Integer> targetsSeen = new ArrayList<Integer>();
		List<Integer> targetsAnalyzed = new ArrayList<Integer>();
		List<Integer> targetsProduced = new ArrayList<Integer>();
		List<Integer> votingAttemptsList = new ArrayList<Integer>();
		List<Integer> adaptationsList = new ArrayList<Integer>();
		List<Integer> failuresList = new ArrayList<Integer>();
		Integer noExps = 0;

		for (Object o : rowResults.values()) {
			RowResult result = (RowResult) o;
			List<ExperimentResult> experimentResults = result.getExperimentsResults();
			for (ExperimentResult experimentResult : experimentResults) {
				List<ObservedEvent> events = experimentResult.getEvents();
				ObservedEvent lastEvent = events.get(events.size() - 1);
				Map properties = lastEvent.getObservedObjectProperties();
				Double time = Double.valueOf((String) properties.get("time"));
				Double energyCost = Double.valueOf((String) properties.get("energy_costs"));
				Double oreAmount = Double.valueOf((String) properties.get("ore_amount"));
				Double adaptationCost = Double.valueOf((String) properties.get("adaptation_costs"));
				Integer collectedOre = Integer.valueOf((String) properties.get("collectedOre"));
				Integer sensedOre = Integer.valueOf((String) properties.get("sensedOre"));
				Integer producedOre = Integer.valueOf((String) properties.get("producedOre"));
				Integer targetSeen = Integer.valueOf((String) properties.get("targetSeen"));
				Integer targetAnalyzed = Integer.valueOf((String) properties.get("targetAnalyzed"));
				Integer targetProduced = Integer.valueOf((String) properties.get("targetProduced"));
				Integer votingAttempts = Integer.valueOf((String) properties.get("votingAttempts"));
				Integer adaptations = Integer.valueOf((String) properties.get("adaptations"));
				Integer failures = Integer.valueOf((String) properties.get("failures"));

				times.add(time);
				costs.add(energyCost);
				ores.add(oreAmount);
				adaptationCosts.add(adaptationCost);
				collectedOres.add(collectedOre);
				sensedOres.add(sensedOre);
				producedOres.add(producedOre);
				targetsSeen.add(targetSeen);
				targetsAnalyzed.add(targetAnalyzed);
				targetsProduced.add(targetProduced);
				votingAttemptsList.add(votingAttempts);
				adaptationsList.add(adaptations);
				failuresList.add(failures);

				noExps++;
			}
		}

		double medianTime = 0;
		for (Double time : times) {
			medianTime += time;
		}
		medianTime = medianTime / noExps;

		double medianCost = 0;
		for (Double cost : costs) {
			medianCost += cost;
		}
		medianCost = medianCost / noExps;

		double medianOre = 0;
		for (Double ore : ores) {
			medianOre += ore;
		}
		medianOre = medianOre / noExps;
		
		double medianAdaptations = 0;
		for (Double adaptation : adaptationCosts) {
			medianAdaptations += adaptation;
		}
		medianAdaptations = medianAdaptations / noExps;
		
		double medianCollectedOre = 0;
		for (Integer collectedOre : collectedOres) {
			medianCollectedOre += collectedOre;
		}
		medianCollectedOre = medianCollectedOre / noExps;
		
		double medianSensedOre = 0;
		for (Integer ore : sensedOres) {
			medianSensedOre += ore;
		}
		medianSensedOre = medianSensedOre / noExps;
		
		double medianProducedOre = 0;
		for (Integer ore : producedOres) {
			medianProducedOre += ore;
		}
		medianProducedOre = medianProducedOre / noExps;
		
		double medianTargetsSeen = 0;
		for (Integer target : targetsSeen) {
			medianTargetsSeen += target;
		}
		medianTargetsSeen = medianTargetsSeen / noExps;
		
		double medianTargetsAnalyzed = 0;
		for (Integer target : targetsAnalyzed) {
			medianTargetsAnalyzed += target;
		}
		medianTargetsAnalyzed = medianTargetsAnalyzed / noExps;
		
		double medianTargetsProduced = 0;
		for (Integer target : targetsProduced) {
			medianTargetsProduced += target;
		}
		medianTargetsProduced = medianTargetsProduced / noExps;
		
		double medianVoting = 0;
		for (Integer vote : votingAttemptsList) {
			medianVoting += vote;
		}
		medianVoting = medianVoting / noExps;
		
		double medianAdaptationsReal = 0;
		for (Integer adaptation : adaptationsList) {
			medianAdaptationsReal += adaptation;
		}
		medianAdaptationsReal = medianAdaptationsReal / noExps;
		
		double medianFailures = 0;
		for (Integer failure : failuresList) {
			medianFailures += failure;
		}
		medianFailures = medianFailures / noExps;
		
		StringBuffer sb = new StringBuffer();
		sb.append("Experiment: " + experimentDescription + "\n");
		sb.append("No. Experiments: " + noExps + "\n");
		sb.append("Median Time: " + medianTime + "\n");
		sb.append("Median Energy Costs: " + medianCost + "\n");
		sb.append("Median Adaptation Costs: " + medianAdaptations + "\n");
		sb.append("Median Ore at homebase: " + medianOre + "\n");
		sb.append("Median Collected Ore: " + medianCollectedOre + "\n");
		sb.append("Median Sensed Ore: " + medianSensedOre + "\n");
		sb.append("Median Produced Ore: " + medianProducedOre + "\n");
		sb.append("Median Targets Seen: " + medianTargetsSeen + "\n");
		sb.append("Median Targets Analyzed: " + medianTargetsAnalyzed + "\n");
		sb.append("Median Targets Produced: " + medianTargetsProduced + "\n");
		sb.append("Median Voting Attempts: " + medianVoting + "\n");
		sb.append("Median Adaptations: " + medianAdaptationsReal + "\n");
		sb.append("Median Failures: " + medianFailures + "\n");

		System.out.println(sb);

		String id = getDateAsString();
		// Persists result in file on disk as txt file
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("MarsWorldCoordSpaceAndEnergy" + "-" + id + ".txt"));
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	private void storeResults(HashMap rowResults, HashMap simFacts, int rowCounter, int rowsDoTo, String resultsAsString, String experimentDescription) {

		// Simulation has finished. Store final result
		if (rowCounter == rowsDoTo) {

			// store result as XML-File
			SimulationResult result = new SimulationResult();
			result.setStarttime(((Long) simFacts.get(Constants.SIMULATION_START_TIME)).longValue());
			result.setEndtime(getClock().getTime());
			result.setName("missing");
			result.setRowsResults(new ArrayList(rowResults.values()));
			result.setDescription(experimentDescription);

			System.out.println("\n\n#ComputeExperimentRowResultsPlan# Simulation finished. Write Results of Simulation to XML!");
			XMLHandler.writeXMLToFile(result, "SimRes" + getDateAsString() + ".xml", SimulationResult.class);
			
			// Store also the evaluation in a file
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("SimulationEVALUATIONResults" + "-" + getDateAsString() + ".txt"));
				out.write("Experiment Description: " + experimentDescription + "\n");
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

	// Application specific evaluation --> compare simulations results with real data
	private void compareSimulationWithRealData(HashMap rowResults, String experimentDescription) {

		for (Iterator<String> it = rowResults.keySet().iterator(); it.hasNext();) {

			BikeSharingEvaluation bikeSharEval = new BikeSharingEvaluation(((RowResult) rowResults.get(it.next())).getEvaluatedRowData());
			bikeSharEval.compare();

			System.out.println("\n\n\nResults contain: \n1) Stock level eval. \n2)Single Bike Stations eval.");
			System.out.println("\nExperiment Description: " + experimentDescription + "\n");
			System.out.println(bikeSharEval.stockLevelResultsToString());
			System.out.println(bikeSharEval.bikestationResultsToString());

			String id = getDateAsString();
			// Persists result in file on disk as txt file
			try {
				// BufferedWriter out = new BufferedWriter(new FileWriter("BikeShareEval-" + "-" + String.valueOf(getClock().getTime()) + ".txt"));
				BufferedWriter out = new BufferedWriter(new FileWriter("BikeShareEval-ALL" + "-" + id + ".txt"));

				out.write("Results contain: \n1) Stock level eval. \n2)Single Bike Stations eval.");
				out.write("\nExperiment Description: " + experimentDescription + "\n");
				out.write(bikeSharEval.stockLevelResultsToString());
				out.write(bikeSharEval.bikestationResultsToString());

				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Persists result in file on disk as XML file
			// 1) The Stock Level Evaluation
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("BikeShareEval-StockLevel" + "-" + id + ".xml"));
				XStream xstream = new XStream(new StaxDriver());
				// add to arbitrary time slice experimentDescription
				ArrayList<EvalStockLevelData> res = bikeSharEval.stockLevelResultsToList();
				res.get(0).setExperimentDescription(experimentDescription);
				out.write(xstream.toXML(res));
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 2) The Results for the single stations
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("BikeShareEval-BikeStationResults" + "-" + id + ".xml"));
				XStream xstream = new XStream(new StaxDriver());
				// add to arbitrary time slice experimentDescription
				ArrayList<EvaluatedBikeStationShortList> res = bikeSharEval.bikestationResultsForXMLPersist();
				res.get(0).setExperimentDescription(experimentDescription);
				out.write(xstream.toXML(res));
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getDateAsString() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date(System.currentTimeMillis()));
		String date = String.valueOf(cal.get(Calendar.DATE)) + "-";
		date += String.valueOf(cal.get(Calendar.MONTH) + 1) + "-";
		date += String.valueOf(cal.get(Calendar.YEAR)) + "--";
		date += String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + "-";
		date += String.valueOf(cal.get(Calendar.MINUTE)) + "-";
		date += String.valueOf(cal.get(Calendar.SECOND));

		return date;
	}
}
