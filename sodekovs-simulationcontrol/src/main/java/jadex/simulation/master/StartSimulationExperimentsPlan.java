package jadex.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.Configuration;
import jadex.simulation.model.Data;
import jadex.simulation.model.Optimization;
import jadex.simulation.model.ParameterSweeping;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.ExperimentResult;
import jadex.simulation.model.result.IntermediateResult;
import jadex.simulation.model.result.RowResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sodekovs.util.misc.FileHandler;
import sodekovs.util.misc.GlobalConstants;

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

		// Put args into application. This args are passed to the application.xml to parameterize application.
		Map applicationArgs = new HashMap();

		// Args for the simulation execution service (Client Agent)
		HashMap<String, Object> clientArgs = new HashMap<String, Object>();
		// read the *.application.xml file from the file system
		clientArgs.put(Constants.APPLICATION_FILE_AS_XML_STRING, FileHandler.readFileAsString(simConf.getApplicationReference()));
		// read the *.configuration.xml file from the file system
		clientArgs.put(GlobalConstants.CONFIGURATION_FILE_AS_XML_STRING, FileHandler.readFileAsString((String) getBeliefbase().getBelief("simulationDescriptionFile").getFact()));

		// prepare object that handles the result
		RowResult rowResult = new RowResult();
		rowResult.setStarttime(getClock().getTime());
		rowResult.setId(String.valueOf(rowCounter));

		// check, whether parameters have to be swept
		if (simConf.getOptimization().getParameterSweeping() != null) {
			if (simConf.getOptimization().getParameterSweeping().getStrategy().equals("linear")) {
				sweepParameters(simConf, applicationArgs);
			} else if (simConf.getOptimization().getParameterSweeping().getStrategy().equals("recursively")){
				recusivelySweepParameter(simConf, applicationArgs);
			} else if (simConf.getOptimization().getParameterSweeping().getStrategy().equals("no sweeping to be performed.")){
				//Do nothing
			}
		} else {
			simConf.getOptimization().setParameterSweeping(new ParameterSweeping());
			simConf.getOptimization().getParameterSweeping().setCurrentConfiguration("Simulation without Parameter Sweeping");
			simConf.getOptimization().getParameterSweeping().setStrategy("no sweeping to be performed.");
		}

		// store simConf since it contains the current configuration of the parameters that are swept through
		getBeliefbase().getBelief("simulationConf").setFact(simConf);
		clientArgs.put(GlobalConstants.CURRENT_PARAMETER_CONFIGURATION, simConf.getOptimization().getParameterSweeping().getCurrentConfiguration());

		// update GUI: create new panel/table for new ensemble
		// ControlCenter gui = (ControlCenter)
		// getBeliefbase().getBelief("tmpGUI").getFact();
		// gui.createNewEnsembleTable(rowCounter);

		for (long i = 0; i < experimentsPerRowToMake; i++) {

			// simFacts.put(Constants.SIMULATION_FACTS_FOR_CLIENT,
			// ObjectCloner.deepCopy(simConf));

			String experimentID = rowCounter + "." + expInRow;
			clientArgs.put(GlobalConstants.EXPERIMENT_ID, experimentID);

			// startApplication(appName, fileName, configName, args);
			// startApplicationRemotley(applicationArgs,clientArgs);

			// Dispatch separate goal to start distribution of single experiment result
			IGoal eval = (IGoal) getGoalbase().createGoal("DistributeExperimentGoal");
			eval.getParameter("applicationArgs").setValue(applicationArgs);
			eval.getParameter("clientArgs").setValue(clientArgs);
			getGoalbase().dispatchTopLevelGoal(eval);

			System.out.println("#*****************************************************************");
			System.out.println("#Master#Used memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024000);
			System.out.println("#*****************************************************************");

			totalRuns++;
			expInRow++;

			// update static part of control center
			// gui.updateStaticTable(rowCounter, expInRow);

			waitForInternalEvent("triggerNextExperiment");

			beliefbaseFacts.put(Constants.TOTAL_EXPERIMENT_COUNTER, new Integer(totalRuns));
			beliefbaseFacts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(expInRow));
			getBeliefbase().getBelief("generalSimulationFacts").setFact(beliefbaseFacts);

			// update experiment counter in intermediate results
			IntermediateResult interRes = (IntermediateResult) getBeliefbase().getBelief("intermediateResults").getFact();
			interRes.setCurrentExperimentNumber(expInRow);
			getBeliefbase().getBelief("intermediateResults").setFact(interRes);
		}

		// Increment row counter
		rowCounter++;
		beliefbaseFacts.put(Constants.EXPERIMENT_ROW_COUNTER, new Integer(rowCounter));
		getBeliefbase().getBelief("generalSimulationFacts").setFact(beliefbaseFacts);

		// store results of row
		HashMap<Integer, ExperimentResult> experimentResults = (HashMap<Integer, ExperimentResult>) getBeliefbase().getBelief("experimentResults").getFact();// contains
		// the results of the experiments done in this row
		HashMap rowResults = (HashMap) getBeliefbase().getBelief("rowResults").getFact();

		ArrayList<ExperimentResult> experimentList = new ArrayList<ExperimentResult>(experimentResults.values());
		rowResult.setExperimentsResults(experimentList);
		rowResult.setEndtime(getClock().getTime());
		rowResult.setName("Tmp-Test");
		rowResult.setOptimizationConfiguration(simConf.getOptimization().getParameterSweeping().getCurrentConfiguration());
		// rowResult.setOptimizationName(experimentList.get(0).getOptimizationParameterName());
		// rowResult.setOptimizationValue(experimentList.get(0).getOptimizationValue());
		rowResult.setFinalStatsMap(((IntermediateResult) getBeliefbase().getBelief("intermediateResults").getFact()).getIntermediateStats());

		rowResults.put(rowResult.getId(), rowResult);

		getBeliefbase().getBelief("experimentResults").setFact(new HashMap());
		getBeliefbase().getBelief("rowResults").setFact(rowResults);

		// evaluate row
		dispatchInternalEvent(createInternalEvent("triggerExperimentRowEvaluation"));
	}

//	private void startApplication(String appName, String fileName, String configName, Map args) {
//
//		try {
//			IComponentManagementService executionService = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class,RequiredServiceInfo.SCOPE_PLATFORM).get(this);
//
//			// create application
//			IFuture fut = executionService.createComponent(appName, fileName, new CreationInfo(configName, args, null, false, false), null);
//
//		} catch (Exception e) {
//			System.out.println("Could not start application...." + e);
//		}
//	}

//	private void startApplicationRemotley(Map applicationArgs, HashMap<String, Object> clientArgs) {
//
//		try {
//			ArrayList<IRemoteSimulationExecutionService> services = null;
//
//			int counter = 0;
//			do {
//				// find appropriate service
//				services = (ArrayList<IRemoteSimulationExecutionService>) SServiceProvider.getServices(getScope().getServiceProvider(), IRemoteSimulationExecutionService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(this);
//				System.out.println("#StartSimulationExpPlan# Nr. of found remote services: " + services.size());
//				if (services.size() <= 0) {
//					System.out.println("#StartSimulationExpPlan# Could not find remote simulation execution service! Retry in 5 sec.");
//					waitFor(5000);
//				}
//				counter++;
//			} while (services.size() <= 0 && counter < 5);
//
//			if (services.size() > 0) {
//
//				// read the *.application.xml File from the file system
//				// String applicationDescription =
//				// FileHandler.readFileAsString(fileName);
//
//				System.out.println("#StartSimulationExpPlan# Distributed Simulation. Waiting for res at Master...");
//				IFuture fut = services.get(0).executeExperiment(applicationArgs, clientArgs);
//				// fut.addResultListener(new IResultListener() {
//				// public void resultAvailable(Object source, Object result) {
//				// System.out.println("#StartSimulationExpPlan#Received res from remote simulation execution");
//				//
//				// // Start Evaluation of single experiment result
//				// IGoal eval = (IGoal)
//				// getGoalbase().createGoal("EvaluateSingleResult");
//				// eval.getParameter("args").setValue(result);
//				// getGoalbase().dispatchTopLevelGoal(eval);
//				// }
//				//
//				// public void exceptionOccurred(Object source, Exception
//				// exception) {
//				// System.out.println("#StartSimulationExpPlan#Error: Remote simulation execution failed!");
//				// }
//				// });
//
//				Map resMap = (Map) fut.get(this);
//				System.out.println("#StartSimulationExpPlan# RECEIVED res at Master...");
//				IGoal eval = (IGoal) getGoalbase().createGoal("EvaluateSingleResult");
//				eval.getParameter("args").setValue(resMap);
//				getGoalbase().dispatchTopLevelGoal(eval);
//
//			} else {
//				System.out.println("Error: Could not find remote simulation execution service!");
//			}
//
//		} catch (Exception e) {
//			System.out.println("Could not start simulation experiment for remote execution" + e);
//		}
//	}

	/**
	 * TODO: differentiate between step and values!!!!
	 * 
	 * @param simConf
	 * @param args
	 */
	private void sweepParameters(SimulationConfiguration simConf, Map args) {
		String currentParameterValues = "";
		Optimization opt = simConf.getOptimization();
		List<Data> data = opt.getData();

		Iterator<Configuration> confIterator = opt.getParameterSweeping().getConfiguration().iterator();
		for (Iterator<Data> i = data.iterator(); i.hasNext();) {
			Data dt = i.next();
			Configuration conf = confIterator.next();
			
			String clazz = conf.getClazz(); //double or int
			double  value = -1;
			// iterate through parameter space with step size; only appropriate to int & double parameter
			if (conf.getType().equalsIgnoreCase(Constants.OPTIMIZATION_TYPE_SPACE)) {
				if (conf.getParameterSweepCounter() == 0) {
					value = Double.parseDouble(conf.getStart());
				} else {
					value = Double.parseDouble(conf.getCurrentValue()) + Double.parseDouble(conf.getStep()); 
				}
			} else if (conf.getType().equalsIgnoreCase(Constants.OPTIMIZATION_TYPE_LIST)) {
				if (conf.getParameterSweepCounter() == 0) {
					value = Double.parseDouble(conf.getValuesAsList().get(0));
				} else {
					value = Double.parseDouble(conf.getValuesAsList().get(conf.getParameterSweepCounter()));
				}
			} else {
				System.err.println("#StartSimulationExperiment# Error on identifying type for sweeping parameter(s): " + opt.getParameterSweeping().getCurrentConfiguration());
			}
			conf.incrementParameterSweepCounter();

			// update SimulationConf to be up to date
			conf.setCurrentValue(String.valueOf(value));

			// parametrize application-xml
			if(clazz.equalsIgnoreCase("int") || clazz.equalsIgnoreCase("integer")){
				int intValue = new Double(value).intValue();
				args.put(dt.getName(), new Integer(intValue));
			}else if(clazz.equalsIgnoreCase("double")){
				args.put(dt.getName(), new Double(value));
			}
			
			currentParameterValues += dt.getName() + " = " + conf.getCurrentValue() + " ";
			opt.getParameterSweeping().setCurrentConfiguration(currentParameterValues);			

		}
		// return currentParameterValues;
	}

	private void recusivelySweepParameter(SimulationConfiguration simConf, Map args) {
		String parametersConfiguration = "";
		Optimization opt = simConf.getOptimization();
		List<Data> data = opt.getData();
		List<Configuration> parameterConf = opt.getParameterSweeping().getConfiguration();

		for (int i = parameterConf.size() - 1; i >= 0; i--) {
			Data dt = data.get(i);
			Configuration conf = parameterConf.get(i);
			double currentValue = Double.parseDouble(conf.getCurrentValue());			
			double endValue = 0;
			double stepValue = 0;
			double startValue = 0;

			// loop only if lower configuration has reached the end and backed
			// to "end";the lowest configuraion loops for always
			if ((i == parameterConf.size() - 1) || conf.getParameterSweepCounter() == 0 || (parameterConf.get(parameterConf.size() - 1).atEnd() && parameterConf.get(i + 1).atEnd())) {
				if (conf.getType().equalsIgnoreCase(Constants.OPTIMIZATION_TYPE_SPACE)) {
					endValue = Double.parseDouble(conf.getEnd());
					stepValue = Double.parseDouble(conf.getStep());
					startValue = Double.parseDouble(conf.getStart());

					if (conf.getParameterSweepCounter() == 0) {
						currentValue = startValue;
						conf.incrementParameterSweepCounter();
					} else {
						if (currentValue < endValue) {
							// reset all lower level
							if (i < parameterConf.size() - 1 && parameterConf.get(i + 1).atEnd()) {
								for (int j = i + 1; j <= parameterConf.size() - 1; j++) {
									parameterConf.get(j).setParameterSweepCounter(1);
								}
							}

							currentValue += stepValue;
							conf.incrementParameterSweepCounter();
							// parameterConf.get(i).setStatus("on running");
						} else {
							// value like a indicator for higher configuration
							// whether loop or not
							// conf.setValues("end");
							currentValue = startValue;
							conf.incrementParameterSweepCounter();
						}
					}
				} else if (conf.getType().equalsIgnoreCase(Constants.OPTIMIZATION_TYPE_LIST)) {
					if (conf.getParameterSweepCounter() == 0) {
						currentValue = Double.parseDouble(conf.getValuesAsList().get(0));
						conf.incrementParameterSweepCounter();
					} else {
						if (conf.getParameterSweepCounter() >= conf.getValuesAsList().size())
							currentValue = Double.parseDouble(conf.getValuesAsList().get(0));
						else {
							currentValue = Double.parseDouble(conf.getValuesAsList().get(conf.getParameterSweepCounter()));
							// reset all lower level
							if (i < parameterConf.size() - 1 && parameterConf.get(i + 1).atEnd()) {
								for (int j = i + 1; j <= parameterConf.size() - 1; j++) {
									parameterConf.get(j).setParameterSweepCounter(1);
								}
							}
						}
						conf.incrementParameterSweepCounter();
					}
				} else {
					System.err.println("#StartSimulationExperiment# Error on identifying type for sweeping parameter(s): " + opt.getParameterSweeping().getCurrentConfiguration());
				}
			} else {
				if (conf.getParameterSweepCounter() == 0) {
					currentValue = startValue;
				}
			}
			
			// update SimulationConf to be up to date
			conf.setCurrentValue(String.valueOf(currentValue));
			
			
			// parametrize application-xml
			if(conf.getClazz().equalsIgnoreCase("int") || conf.getClazz().equalsIgnoreCase("integer")){
				int intValue = new Double(currentValue).intValue();
				args.put(dt.getName(), new Integer(intValue));
			}else if(conf.getClazz().equalsIgnoreCase("double")){
				args.put(dt.getName(), new Double(currentValue));
			}
			
			parametersConfiguration = dt.getName() + " = " + currentValue + " " + parametersConfiguration;
			opt.getParameterSweeping().setCurrentConfiguration(parametersConfiguration);

		}

		// reach the end of recusion when first parameter and last parameter
		// reach maximum at the sametime = SPACE
		if (parameterConf.get(0).atEnd() && parameterConf.get(parameterConf.size() - 1).atEnd()) {
			System.out.println("********************RECURSION END*************************");
		}

		// return parametersConfiguration;
	}

}
