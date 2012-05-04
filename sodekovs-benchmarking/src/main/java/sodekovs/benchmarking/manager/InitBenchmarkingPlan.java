package sodekovs.benchmarking.manager;

import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.MEnvSpaceType;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.evaluation.AbstractChartDataConsumer;
import jadex.extension.envsupport.evaluation.DefaultDataProvider;
import jadex.extension.envsupport.evaluation.IObjectSource;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;
import jadex.extension.envsupport.evaluation.ITableDataProvider;
import jadex.extension.envsupport.evaluation.SpaceObjectSource;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sodekovs.benchmarking.helper.Constants;
import sodekovs.benchmarking.helper.Methods;
import sodekovs.benchmarking.logger.ScheduleLogger;
import sodekovs.benchmarking.model.Data;
import sodekovs.benchmarking.model.Dataconsumer;
import sodekovs.benchmarking.model.Dataconsumers;
import sodekovs.benchmarking.model.Dataprovider;
import sodekovs.benchmarking.model.Dataproviders;
import sodekovs.benchmarking.model.Imports;
import sodekovs.benchmarking.model.Property;
import sodekovs.benchmarking.model.Schedule;
import sodekovs.benchmarking.model.Schedule.Evaluation;
import sodekovs.benchmarking.model.SemanticCondition;
import sodekovs.benchmarking.model.Sequence;
import sodekovs.benchmarking.model.Source;
import sodekovs.benchmarking.model.SuTinfo;
import sodekovs.util.gnuplot.persistence.LogDAO;
import sodekovs.util.math.GetRandom;
import sodekovs.util.misc.AgentMethods;
import sodekovs.util.misc.EvaluateExpression;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.misc.XMLHandler;

public class InitBenchmarkingPlan extends Plan {

	private IComponentManagementService cms = null;
	private IClockService clockservice = null;
	private ArrayList<Sequence> sortedSequenceList = null;
	// Component Identifier of System Under Test
	private IComponentIdentifier sutCID = null;
	// Exta of System Under Test
	private IExternalAccess sutExta = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	// Component Identifier of scheduler
	private IComponentIdentifier schedulerCID = null;
	// Component Identifier of adaptationAnalyzer
	private IComponentIdentifier adaptationAnalyzerCID = null;
	// private Log events
	private ScheduleLogger scheduleLogger = null;
	// private OnlineVisualisation vis = null;
	// The java representation of the benchmark to be conducted
	Schedule benchConf = null;
	// required for parallel execution of benchmarks
	private int localBenchmarkingCounter = -1;

	public void body() {

		// NEW ********************************************

		// Increment the number of currently running experiments on this agent
		numberOfRunningBenchmarks(1);

		// Get local id for this benchmark to be conducted and increment counter
		localBenchmarkingCounter = (Integer) getBeliefbase().getBelief("benchmarkCounter").getFact();
		getBeliefbase().getBelief("benchmarkCounter").setFact(localBenchmarkingCounter + 1);
		
		//log status of benchmark
		HashMap<Integer,String> benchmarkStatusMap = (HashMap<Integer, String>) getBeliefbase().getBelief("benchmarkStatus").getFact();
		benchmarkStatusMap.put(localBenchmarkingCounter, Constants.PREPARING_START);
		getBeliefbase().getBelief("benchmarkStatus").setFact(benchmarkStatusMap);

		// init mapping between calling service and the executed benchmark. needed in order to be able to execute benchmarks in parallel.
		HashMap<Long, Integer> callerBenchmarkReference = (HashMap<Long, Integer>) getBeliefbase().getBelief("callerBenchmarkReference").getFact();
		callerBenchmarkReference.put((Long) getParameter("callerID").getValue(), localBenchmarkingCounter);
		getBeliefbase().getBelief("callerBenchmarkReference").setFact(callerBenchmarkReference);

//		HashMap<String, Object> clientConfMap = (HashMap<String, Object>) getParameter("clientConf").getValue();
//		SimulationConfiguration simConf = (SimulationConfiguration) XMLHandler.parseXMLFromString((String) clientConfMap.get(GlobalConstants.CONFIGURATION_FILE_AS_XML_STRING),
//				SimulationConfiguration.class);
//		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);

//		startApplication((Map) getParameter("applicationConf").getValue(), clientConfMap, simConf);
//		System.out.println("#RumtimeManagerPlan# Startet Simulation Experiment Nr.:" + clientConfMap.get(GlobalConstants.EXPERIMENT_ID) + ") with Optimization Values: "
//				+ clientConfMap.get(GlobalConstants.CURRENT_PARAMETER_CONFIGURATION));
//		System.out.println("Number of Exp at this agent: " + (Integer) getBeliefbase().getBelief("numberOfRunningExperiments").getFact());

		// NEW ********************************************

		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) getScope().getServiceContainer().getRequiredService("clockservice").get(this);

//		String benchmarkDescription = (String) getBeliefbase().getBelief("scheduleDescriptionFile").getFact();
		String benchmarkDescription = (String) getParameter("benchmarkingDefinitionFile").getValue();
		System.out.println("#InitBench# Init Benchmark Agent with configuration file: " + benchmarkDescription + " and local BenchID: " + localBenchmarkingCounter);
		benchConf = (Schedule) XMLHandler.parseXMLFromString(benchmarkDescription, Schedule.class);
 
		HashMap<Integer,Schedule> allBenchmarks = (HashMap<Integer, Schedule>) getBeliefbase().getBelief("allBenchmarks").getFact();
		allBenchmarks.put(localBenchmarkingCounter, benchConf);
		getBeliefbase().getBelief("allBenchmarks").setFact(allBenchmarks);

		// Create list of sequences, ordered by their start time
		sortedSequenceList = (ArrayList<Sequence>) benchConf.getSequences().getSequence();
		createOrderedSequenceList(sortedSequenceList);

		// Start System under Test (SuT) if required, i.e. has not been started by another component yet.
		// SUT is started in suspend mode.
		if (benchConf.getSytemUnderTest() != null) {
			startSuT();
		}

//		getBeliefbase().getBelief("suTinfo").setFact(new SuTinfo(sortedSequenceList, sutCID, sutExta, sutSpace));

		// initLogger
		// Attention: Logger has still to be initialized! (starttime and clockservice)
		scheduleLogger = new ScheduleLogger();

		// Start scheduler, that handles the execution of the sequences of the conducted benchmark.
		// Scheduler is started in suspend mode.
		startScheduler();
		
		// Start AdaptationAnalysis, that monitors the ability of the system to adapt to changes 
		// AdaptationAnalyzer is started in suspend mode.
		startAdaptationAnalyzer();

		// Check if there are DataConsumer and DataProvider to be added to space
		if (checkDataConcumerAndProvider()) {
			addDataConsumerAndProvider();

			// start Online Visualization
			startOnlineVisualization();
		} else {
			System.out.println("#InitBenchmarkingPlan# No DataConsumer and DataProvider to add to space.");
		}

		// TODO: Hack: Synchronize start time!
		long startTime = clockservice.getTime();
		sutSpace.setProperty("REAL_START_TIME_OF_SIMULATION", startTime);

		// Resume SuT
		cms.resumeComponent(sutCID).get(this);

		// start warm up phase, if defined. Schedule starts after this phase its execution
		if (benchConf.getWarmUpTime() != null) {
			waitFor(benchConf.getWarmUpTime());
			// myLogger.log("Warm-up finished");
		}

		// Resume scheduler & adaptationAnalyzer
		cms.resumeComponent(schedulerCID).get(this);
		cms.resumeComponent(adaptationAnalyzerCID).get(this);
		
		
		benchmarkStatusMap = (HashMap<Integer, String>) getBeliefbase().getBelief("benchmarkStatus").getFact();
		benchmarkStatusMap.put(localBenchmarkingCounter, Constants.RUNNING);
		getBeliefbase().getBelief("benchmarkStatus").setFact(benchmarkStatusMap);
		// myLogger.log("Resumed Scheduler");

		// Handle termination of benchmark
		terminateBenchmark();
		benchmarkStatusMap = (HashMap<Integer, String>) getBeliefbase().getBelief("benchmarkStatus").getFact();
		benchmarkStatusMap.put(localBenchmarkingCounter, Constants.TERMINATED);
		getBeliefbase().getBelief("benchmarkStatus").setFact(benchmarkStatusMap);
		scheduleLogger.log(Constants.PREPARE_GNUPLOT_SUFFIX);
		persistLogs(scheduleLogger.getFileName());
		// ConnectionManager.getInstance().executeStatement("Over and out");
	}

	/*
	 * Start System under Test. Required if system has not been started yet.
	 */
	private void startSuT() {

		HashMap<String, String> sutProperties = Methods.propertyListToHashMapforString(benchConf.getSytemUnderTest().getProperties().getProperty());

		// create SuT in suspended modus
		IFuture fut = cms.createComponent(benchConf.getName() + GetRandom.getRandom(100000), sutProperties.get(Constants.APPLICATION_FILE_PATH),
				new CreationInfo(sutProperties.get(Constants.APPLICATION_COONFIGURATION), new HashMap(), null, true, false), null);
		sutCID = (IComponentIdentifier) fut.get(this);
		sutExta = (IExternalAccess) cms.getExternalAccess(sutCID).get(this);
		fut = sutExta.getExtension(sutProperties.get(Constants.SPACE_NAME));
		sutSpace = (AbstractEnvironmentSpace) fut.get(this);
	}

	/*
	 * Start scheduler in suspended mode.
	 */
	private void startScheduler() {
		HashMap<String,Object> args = new HashMap<String,Object>();
		args.put(Constants.SUT_INFO, new SuTinfo(sortedSequenceList, null, sutCID, sutExta, sutSpace));
		args.put(Constants.SCHEDULE_LOGGER, scheduleLogger);
		args.put(Constants.SCALE_FACTOR, benchConf.getSequences().getScaleFactor());

		IFuture fut = cms.createComponent("Scheduler" + GetRandom.getRandom(100000), Constants.PATH_OF_SCHEDULER, new CreationInfo(null, args, null, true, false), null);
		schedulerCID = (IComponentIdentifier) fut.get(this);
	}
	
	/*
	 * Start adaptationAnalyzer in suspended mode.
	 */
	private void startAdaptationAnalyzer() {
		HashMap<String,Object> args = new HashMap<String,Object>();
		args.put(Constants.SUT_INFO, new SuTinfo(null, benchConf.getAdaptationAnalysis(), sutCID, sutExta, sutSpace));
//		args.put(Constants.SCHEDULE_LOGGER, scheduleLogger);

		IFuture fut = cms.createComponent("AdaptationAnalyzer" + GetRandom.getRandom(100000), Constants.PATH_OF_ADAPTATION_ANALYZER, new CreationInfo(null, args, null, true, false), null);
		adaptationAnalyzerCID = (IComponentIdentifier) fut.get(this);
	}

	/*
	 * Handle termination of benchmark
	 * 
	 * @param benchConf
	 */
	private void terminateBenchmark() {
		if (benchConf.getTerminateCondition() != null) {
			if (benchConf.getTerminateCondition().getTerminationTime() != null) {
				waitFor(benchConf.getTerminateCondition().getTerminationTime().getValue());
				System.out.println("#InitBenchmarkingPlan# Benchmark terminated according to specified termination time.");
				destroySuT();
			} else if (benchConf.getTerminateCondition().getSemanticCondition() != null) {
				SemanticCondition semCond = benchConf.getTerminateCondition().getSemanticCondition();
				boolean terminate = false;
				// HACK: Need a observer / listener instead evaluating expression every 1000ms
				while (true) {
					waitFor(1000);

					// Hack/Limitations: Works right now only for single objects but not for all of that type...
					// Additionally: only one part of the equation can be an object...
					if (semCond.getObjectSource().getType().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {
						terminate = EvaluateExpression.evaluate(sutSpace, semCond.getCondition(), semCond.getObjectSource().getName(), semCond.getObjectSource().getType());
					} else {
						IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(sutSpace, semCond.getObjectSource().getName());
						IFuture fut = cms.getExternalAccess(agentIdentifier);
						IExternalAccess exta = (IExternalAccess) fut.get(this);

						IOAVState state = ((ElementFlyweight) exta).getState();
						Object rCapability = ((ElementFlyweight) exta).getScope();

						// Evaluate condition/expression
						OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
						terminate = EvaluateExpression.evaluateExpression(fetcher, semCond.getCondition());
					}

					// Experiment has reached Target Function. Terminate
					if (terminate) {
						System.out.println("#InitBenchmarkingPlan# Terminate experiment: Semantic termination condition has been evaluated being true.");
						break;
					}
				}
				// destroy sytem under test
				destroySuT();
			} else {
				System.out.println("#InitBenchmarkingPlan# Error: NO termination condition specified. Benchmark will not be automatically terminated by BenchmarkingAgent.");
			}
		} else {
			System.out.println("#InitBenchmarkingPlan# NO termination condition specified. Benchmark will not be automatically terminated by BenchmarkingAgent.");
		}
	}

	/*
	 * Destroy SuT
	 */
	private void destroySuT() {

		cms.destroyComponent(schedulerCID).get(this);
		cms.destroyComponent(sutExta.getComponentIdentifier()).get(this);
	}

	/*
	 * Get time stamp relative to start of benchmark (without warm up phase)
	 * 
	 * @return
	 */
//	private long getTimestamp() {
//		long starttime = ((Long) sutSpace.getProperty("REAL_START_TIME_OF_SIMULATION")).longValue();
//		return clockservice.getTime() - starttime;
//	}

	private void persistLogs(String fileName) {
		// ConnectionManager conMgr = new ConnectionManager();
		// conMgr.storeGnuPlotLogs(fileName,benchConf.getType(),benchConf.getName(), scheduleLogger.getTimestamp());
		LogDAO.getInstance().insertNewGnuPlotLog(fileName, benchConf.getType(), benchConf.getName(), scheduleLogger.getTimestamp());

	}

	/*
	 * Returns the list of sequences events ascendingly ordered by relative start time.
	 */
	private void createOrderedSequenceList(ArrayList<Sequence> list) {
		Collections.sort(list, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return Long.valueOf(((Sequence) arg0).getStarttime()).compareTo(Long.valueOf(((Sequence) arg1).getStarttime()));
			}
		});
	}

	private void addDataConsumerAndProvider() {

		IExpressionParser parser = new JavaCCExpressionParser();

		List<Dataprovider> providers = benchConf.getEvaluation().getDataproviders().getDataprovider();
		// List tmp = si.getPropertyList("dataproviders");

		// System.out.println("data providers: "+providers);
		if (providers != null) {
			// iteration through all dataprovider
			for (int i = 0; i < providers.size(); i++) {
				// Map dcol = (Map)providers.get(i);

				List<Source> sources = providers.get(i).getSource();
				IObjectSource[] provs = new IObjectSource[sources.size()];
				for (int j = 0; j < sources.size(); j++) {
					Source source = sources.get(j);
					String varname = source.getName() != null ? source.getName() : "$object";
					String objecttype = source.getObjecttype();
					boolean aggregate = source.isAggregate() == null ? false : source.isAggregate();
					IParsedExpression dataexp = EvaluateExpression.getParsedExpression(source.getContent(), parser);
					// Hack: Includeconditon is not implemented, yet.
					IParsedExpression includeexp = null;
					provs[j] = new SpaceObjectSource(varname, sutSpace, objecttype, aggregate, dataexp, includeexp);
				}

				String tablename = providers.get(i).getName();
				List<Data> subdatas = providers.get(i).getData();
				String[] columnnames = new String[subdatas.size()];
				IParsedExpression[] exps = new IParsedExpression[subdatas.size()];
				for (int j = 0; j < subdatas.size(); j++) {
					Data subdata = subdatas.get(j);
					columnnames[j] = subdata.getName();
					exps[j] = EvaluateExpression.getParsedExpression((String) subdata.getContent(), parser);
				}

				ITableDataProvider tprov = new DefaultDataProvider(sutSpace, provs, tablename, columnnames, exps);
				sutSpace.addDataProvider(tablename, tprov);
			}
		}

		// Create the data consumers.
		List<Dataconsumer> consumers = benchConf.getEvaluation().getDataconsumers().getDataconsumer();

		// System.out.println("data consumers: "+consumers);
		if (consumers != null) {
			for (int i = 0; i < consumers.size(); i++) {
				Dataconsumer dcon = consumers.get(i);
				String name = dcon.getName();

				// Class clazz = (Class)MEnvSpaceInstance.getProperty(dcon,
				// "clazz");
				Class clazz = null;

				// Hack: avoid null pointer exception for imports
				if (benchConf.getImports() == null) {
					benchConf.setImports(new Imports());
				}

				try {
//					clazz = SReflect.findClass(dcon.getClazz(), toStringArray((ArrayList<String>) benchConf.getImports().getImport()),
//							((ILibraryService) SServiceProvider.getService(getScope().getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this)).getClassLoader());
					
					//TODO: Check, if works!
					clazz = SReflect.findClass(dcon.getClazz(), toStringArray((ArrayList<String>) benchConf.getImports().getImport()),getState().getTypeModel().getClassLoader());

					// clazz = SReflect.findClass0(classname, imports, classloader);					

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ITableDataConsumer con = null;
				try {
					con = (ITableDataConsumer) clazz.newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// List of Hashmaps with dynamic=false
				List<Map> tmpPropertyList = new ArrayList<Map>();
				for (int j = 0; j < dcon.getMixedProperty().size(); j++) {
					Map map = new HashMap();
					map.put("dynamic", false);
					map.put("name", dcon.getMixedProperty().get(j).getName());
					// map.put("value",
					// dcon.getPropertyList().get(j).getValue());
					// Hack: has to be "initially" an expression that can be
					// parsed
					map.put("value", EvaluateExpression.getParsedExpression(dcon.getMixedProperty().get(j).getContent(), parser));
					tmpPropertyList.add(map);
				}
				// MEnvSpaceInstance.setProperties(con,
				// (List)dcon.get("properties"), fetcher);
				MEnvSpaceType.setProperties(con, tmpPropertyList, sutSpace.getFetcher());//
				con.setProperty("envspace", sutSpace);
				sutSpace.addDataConsumer(name, con);
			}
		}
		// System.out.println("nnnnnnnnnnneded iterations: " + counterTmp);
	}

	private void startOnlineVisualization() {

		// init online visualization
		ArrayList<AbstractChartDataConsumer> chartDataConsumer = new ArrayList<AbstractChartDataConsumer>();
		for (Iterator it = sutSpace.getDataConsumers().iterator(); it.hasNext();) {
			Object abstractConsumer = it.next();

			if (abstractConsumer instanceof AbstractChartDataConsumer)
				chartDataConsumer.add((AbstractChartDataConsumer) abstractConsumer);
		}
		// this.vis = new OnlineVisualisation(chartDataConsumer);
	}

	/***
	 * Transforms an ArrayList<String> into String[]
	 * 
	 * @param list
	 * @return
	 */
	private String[] toStringArray(ArrayList<String> list) {
		String[] array = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}

	private String getProperty(List<Property> properties, String name) {
		for (Property prop : properties) {
			if (prop.getName().equalsIgnoreCase(name)) {
				return prop.getValue();
			}
		}
		System.err.print("#InitBenchmarkingPlan# Property <" + name + "> not found in propertyList");
		return null;
	}

	/**
	 * Check whether all preconditions are fulfilled to add DataConsumer and DataProvider to space
	 * 
	 * @param benchConf
	 * @return
	 */
	private boolean checkDataConcumerAndProvider() {
		IFuture fut = (sutExta).getExtension(getProperty(benchConf.getSytemUnderTest().getProperties().getProperty(), "spaceName"));
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) fut.get(this);

		if (space == null) {
			return false;
		}

		Evaluation eval = benchConf.getEvaluation();

		if (eval == null) {
			return false;
		} else {
			Dataproviders providers = benchConf.getEvaluation().getDataproviders();
			Dataconsumers consumers = benchConf.getEvaluation().getDataconsumers();
			if (providers == null || consumers == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Increment / Decrement the counter which contains the number of currently executed benchmarks
	 * 
	 * @param i
	 */
	private void numberOfRunningBenchmarks(int i) {
		int n = (Integer) getBeliefbase().getBelief("numberOfRunningBenchmarks").getFact();
		getBeliefbase().getBelief("numberOfRunningBenchmarks").setFact(n + i);
	}
}
