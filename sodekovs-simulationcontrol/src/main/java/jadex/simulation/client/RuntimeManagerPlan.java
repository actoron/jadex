package jadex.simulation.client;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.MEnvSpaceInstance;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.evaluation.AbstractChartDataConsumer;
import jadex.application.space.envsupport.evaluation.DefaultDataProvider;
import jadex.application.space.envsupport.evaluation.IObjectSource;
import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
import jadex.application.space.envsupport.evaluation.ITableDataProvider;
import jadex.application.space.envsupport.evaluation.SpaceObjectSource;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.library.ILibraryService;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;
import jadex.simulation.controlcenter.OnlineVisualisation;
import jadex.simulation.evaluation.SimulationDataConsumer;
import jadex.simulation.helper.AgentMethods;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.EvaluateExpression;
import jadex.simulation.helper.FileHandler;
import jadex.simulation.helper.TimeConverter;
import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.Data;
import jadex.simulation.model.Dataconsumer;
import jadex.simulation.model.Dataprovider;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.Source;
import jadex.simulation.model.TargetFunction;
import jadex.simulation.model.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeManagerPlan extends Plan {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6999042052110256441L;
	
	
	/**
	 * External access of the application that is executed and observed by this
	 * agent
	 */
	private IApplicationExternalAccess exta = null;
	private IClockService clockservice = (IClockService) SServiceProvider.getService(getScope().getServiceProvider(), IClockService.class,RequiredServiceInfo.SCOPE_PLATFORM).get(this);
	IComponentManagementService cms = null;
	private OnlineVisualisation vis = null;
	private String appFilePath = null;

	public void body() {
		HashMap<String,Object> clientConfMap = (HashMap<String, Object>) getParameter("clientConf").getValue();
		SimulationConfiguration simConf  = (SimulationConfiguration) XMLHandler.parseXMLFromString((String) clientConfMap.get(Constants.CONFIGURATION_FILE_AS_XML_STRING), SimulationConfiguration.class);
		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class,RequiredServiceInfo.SCOPE_PLATFORM).get(this);
//		HashMap simFacts = (HashMap) getBeliefbase().getBelief("simulationFacts").getFact();
		
		
		
		startApplication((Map) getParameter("applicationConf").getValue(), clientConfMap, simConf);
		System.out.println("#RumtimeManagerPlan# Startet Simulation Experiment Nr.:" + clientConfMap.get(Constants.EXPERIMENT_ID) + ") with Optimization Values: "
				+ clientConfMap.get(Constants.CURRENT_PARAMETER_CONFIGURATION));

		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) exta.getSpace(simConf.getNameOfSpace());

		// Determine terminate condition
		// Time determines termination
		if (simConf.getRunConfiguration().getRows().getTerminateCondition().getTime() != null) {

			Time time = simConf.getRunConfiguration().getRows().getTerminateCondition().getTime();
			Long terminationTime = new Long(-1);

			if (time.getType().equals(Constants.RELATIVE_TIME_EXPRESSION)) {
				terminationTime = getTerminationTime(0, time.getValue());
			} else if (time.getType().equals(Constants.ABSOLUTE_TIME_EXPRESSION)) {
				terminationTime = getTerminationTime(1, time.getValue());
			} else {
				System.err.println("#RunTimeManagerPlan# Time type missing " + simConf);
			}
			if (terminationTime.longValue() > -1) {
				waitFor(terminationTime);
			} else {
				System.err.println("#RunTimeManagerPlan# Error on setting termination time " + simConf);
			}
			// Application semantic determines termination, e.g.
			// $homebase.NumberOfOre > 100
		} else if (simConf.getRunConfiguration().getRows().getTerminateCondition().getTargetFunction() != null) {

			TargetFunction targetFunct = simConf.getRunConfiguration().getRows().getTerminateCondition().getTargetFunction();

			// HACK: Need a observer / listener instead evaluating expression
			// every 1000ms
			while (true) {
				waitFor(1000);

				// Hack: Works right now only for single objects but not for all
				// of that type...
				// Additionally: only one part of the equation can be an
				// object...
				if (targetFunct.getObjectSource().getType().equalsIgnoreCase(Constants.ISPACE_OBJECT)) {

					// // String expression =
					// "$object.getProperty(\"ore\") >= 10";

					boolean res = EvaluateExpression.evaluate(space, targetFunct.getFunction(), targetFunct.getObjectSource().getName(), targetFunct.getObjectSource().getType());

					if (res) {
						System.out.println("#RuntimeManagerPlan# Terminate experiment: Semantic termination condition has been evaluated being true.");
						// Experiment has reached Target Function. Terminate
						break;
					}
				} else {
					IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(space, targetFunct.getObjectSource().getName());
					IFuture fut = cms.getExternalAccess(agentIdentifier);
					IExternalAccess exta = (IExternalAccess) fut.get(this);

					IOAVState state = ((ElementFlyweight) exta).getState();
					Object rCapability = ((ElementFlyweight) exta).getScope();

					// Evaluate condition/expression
					OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
					boolean res = EvaluateExpression.evaluateExpression(fetcher, targetFunct.getFunction());

					if (res) {
						System.out.println("#RuntimeManagerPlan# Terminate experiment: Semantic termination condition has been evaluated being true.");
						// Experiment has reached Target Function. Terminate
						break;
					}
				}
			}

		} else {
			System.err.println("#RunTimeManagerPlan# Terminate Condition missing " + simConf);
		}

		ConcurrentHashMap<Long, ArrayList<ObservedEvent>> results = getResult(space);

		prepareResult(results,(String) clientConfMap.get(Constants.EXPERIMENT_ID));

		System.out.println("#RuntimeManagerPlan# Killing executed application....");
		vis.setExit();
		vis.dispose();
		
		
		FileHandler.deleteFile(appFilePath);
		appFilePath = null;
		

		cms.destroyComponent(exta.getComponentIdentifier());
//		System.out.println("#RuntimeManagerPlan# Goal over???");
	}

	private void prepareResult(ConcurrentHashMap<Long, ArrayList<ObservedEvent>> observedEvents, String experimentID) {
		Map facts = (Map) getBeliefbase().getBelief("simulationFacts").getFact();
		facts.put(Constants.EXPERIMENT_END_TIME, new Long(clockservice.getTime()));
		facts.put(Constants.OBSERVED_EVENTS_MAP, observedEvents);
		facts.put(Constants.EXPERIMENT_ID, experimentID);
		// does not need to be send back to master agent
//		facts.remove(Constants.SIMULATION_FACTS_FOR_CLIENT);
		
		getBeliefbase().getBelief("simulationFacts").setFact(facts);
	}

	/**
	 * Returns the observedEvents from the SimulationDataConsumer Hack: Can only
	 * process one SimualtioDataConsumer, i.e. it returns the events from the
	 * FIRST SimulatioDataConsumer
	 * 
	 * @param space
	 * @return
	 */
	private ConcurrentHashMap<Long, ArrayList<ObservedEvent>> getResult(AbstractEnvironmentSpace space) {
		Collection collection = space.getDataConsumers();

		Iterator itr = collection.iterator();

		while (itr.hasNext()) {
			Object con = itr.next();
			if (con instanceof SimulationDataConsumer)
				return ((SimulationDataConsumer) con).getResults();
		}
		return null;
	}

	/**
	 * Compute Termination: Input: Mode=0 ->relative Time; Mode=1 ->absolute
	 * Time
	 * 
	 * @return the termination time as relative time, e.g. the time the
	 *         simulation has to run.
	 */
	private Long getTerminationTime(int mode, long value) {
		Long currentTime = new Long(clockservice.getTime());

		if (mode == 0) {
			// Long relativeTime = new Long(10000);
			Long terminationTime = new Long(value + currentTime.longValue());
			System.out.println("#RuntimeManagerPlan# StartTime: " + TimeConverter.longTime2DateString(currentTime) + " - TerminationTime: " + TimeConverter.longTime2DateString(terminationTime));
			return new Long(value);
		} else {// TODO: There might be a problem with Day Light Savings Time!
			Calendar cal = Calendar.getInstance();
			// Date terminationTime = cal.getTime();
			Long duration = new Long(value - currentTime.longValue());
			System.out.println("#RuntimeManagerPlan# StartTime: " + TimeConverter.longTime2DateString(currentTime) + " TerminationTime: " + TimeConverter.longTime2DateString(new Long(value)) + ", Duration: "
					+ duration.longValue());
			return duration;
		}
	}

	private void startApplication(Map appConf, HashMap<String,Object> clientConf, SimulationConfiguration simConf) {
		
		//store file application.xml
		appFilePath = System.getProperty("user.dir") + "\\"+ simConf.getName()+ (String) clientConf.get(Constants.EXPERIMENT_ID)+ ".application.xml";
		FileHandler.writeToFile(appFilePath, (String) clientConf.get(Constants.APPLICATION_FILE_AS_XML_STRING));
		
		// create application in suspended modus
		IFuture fut = cms.createComponent(simConf.getName() + (String) clientConf.get(Constants.EXPERIMENT_ID), appFilePath, new CreationInfo(simConf.getApplicationConfiguration(), appConf, null, true, false), null);
		IComponentIdentifier cid = (IComponentIdentifier) fut.get(this);
		this.exta = (IApplicationExternalAccess) cms.getExternalAccess(cid).get(this);

		// add DataConsumer and Provider
		addDataConsumerAndProvider(simConf);
		
		//Start Online Visualization
		startOnlineVisualization(simConf);
		
		//resume application
		cms.resumeComponent(cid);

		// Save initial facts of this simulation run.
		long startTime = clockservice.getTime();
		Map facts = new HashMap();
		facts.put(Constants.EXPERIMENT_START_TIME, new Long(startTime));
		getBeliefbase().getBelief("simulationFacts").setFact(facts);

		// Hack: Synchronize start time!
//		System.out.println("-->StartTime at Client: " + startTime);
		AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) (exta).getSpace(simConf.getNameOfSpace()));
		space.setProperty("REAL_START_TIME_OF_SIMULATION", startTime);
		
		// *************************************************************
		// This is a hack for this special application.xml -> MarsWorld
		if (space.getSpaceObjectsByType("homebase").length > 0) {
			space.getSpaceObjectsByType("homebase")[0].setProperty("start_time", startTime);
		}
	}

	private void addDataConsumerAndProvider(SimulationConfiguration simConf) {

		AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) (exta).getSpace(simConf.getNameOfSpace()));
		IExpressionParser parser = new JavaCCExpressionParser();

		// add new data provider
		List<Dataprovider> providers = simConf.getDataproviders().getDataprovider();
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
					provs[j] = new SpaceObjectSource(varname, space, objecttype, aggregate, dataexp, includeexp);
				}

				String tablename = providers.get(i).getName();
				List<Data> subdatas = providers.get(i).getData();
				String[] columnnames = new String[subdatas.size()];
				IParsedExpression[] exps = new IParsedExpression[subdatas.size()];
				for (int j = 0; j < subdatas.size(); j++) {
					Data subdata = subdatas.get(j);
					columnnames[j] = subdata.getName();
					exps[j] = EvaluateExpression.getParsedExpression((String) subdata.getContent().get(0), parser);
				}

				ITableDataProvider tprov = new DefaultDataProvider(space, provs, tablename, columnnames, exps);
				space.addDataProvider(tablename, tprov);
			}
		}

		// Create the data consumers.
		List<Dataconsumer> consumers = simConf.getDataconsumers().getDataconsumer();

		// System.out.println("data consumers: "+consumers);
		if (consumers != null) {
			for (int i = 0; i < consumers.size(); i++) {
				Dataconsumer dcon = consumers.get(i);
				String name = dcon.getName();

				// Class clazz = (Class)MEnvSpaceInstance.getProperty(dcon,
				// "clazz");
				Class clazz = null;
				try {
					clazz = SReflect.findClass(dcon.getClazz(), toStringArray((ArrayList<String>) simConf.getImports().getImport()),
							((ILibraryService) SServiceProvider.getService(getScope().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this)).getClassLoader());

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
				for (int j = 0; j < dcon.getProperty().size(); j++) {
					Map map = new HashMap();
					map.put("dynamic", false);
					map.put("name", dcon.getProperty().get(j).getName());
					// map.put("value",
					// dcon.getPropertyList().get(j).getValue());
					// Hack: has to be "initially" an expression that can be
					// parsed
					map.put("value", EvaluateExpression.getParsedExpression(dcon.getProperty().get(j).getContent(), parser));
					tmpPropertyList.add(map);
				}
				// MEnvSpaceInstance.setProperties(con,
				// (List)dcon.get("properties"), fetcher);
				MEnvSpaceInstance.setProperties(con, tmpPropertyList, space.getFetcher());//
				con.setProperty("envspace", space);
				space.addDataConsumer(name, con);
			}
		}
		// System.out.println("nnnnnnnnnnneded iterations: " + counterTmp);
	}

	private void startOnlineVisualization(SimulationConfiguration simConf){
		AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) (exta).getSpace(simConf.getNameOfSpace()));

		// init online visualization
		ArrayList<AbstractChartDataConsumer> chartDataConsumer = new ArrayList<AbstractChartDataConsumer>();
		for (Iterator it = space.getDataConsumers().iterator(); it.hasNext();) {
			Object abstractConsumer = it.next();

			if (abstractConsumer instanceof AbstractChartDataConsumer)
				chartDataConsumer.add((AbstractChartDataConsumer) abstractConsumer);
		}
		this.vis =  new OnlineVisualisation(chartDataConsumer);
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
}
