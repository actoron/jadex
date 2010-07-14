package jadex.simulation.client;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.MEnvSpaceInstance;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.application.space.envsupport.evaluation.AbstractChartDataConsumer;
import jadex.application.space.envsupport.evaluation.DefaultDataProvider;
import jadex.application.space.envsupport.evaluation.IObjectSource;
import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
import jadex.application.space.envsupport.evaluation.ITableDataProvider;
import jadex.application.space.envsupport.evaluation.SpaceObjectSource;
import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;
import jadex.service.clock.IClockService;
import jadex.service.library.ILibraryService;
import jadex.simulation.controlcenter.OnlineVisualisation;
import jadex.simulation.helper.AgentMethods;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.EvaluateExpression;
import jadex.simulation.helper.TimeConverter;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeManagerPlan extends Plan {

	public void body() {
		HashMap simFacts = (HashMap) getBeliefbase().getBelief("simulationFacts").getFact();
		SimulationConfiguration simConf = (SimulationConfiguration) simFacts.get(Constants.SIMULATION_FACTS_FOR_CLIENT);
		String experimentID = (String) simFacts.get(Constants.EXPERIMENT_ID);
		int parameterSweepValue = simConf.getOptimization().getParameterSweeping().getCurrentValue();
		String parameterSweepName = simConf.getOptimization().getData().getName();

		System.out.println("#Client# Started CLIENT Simulation run....: " + simConf.getName() + " - " + experimentID + ", currentVal: " + parameterSweepValue);

		// Get Space
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace");

		// Init Arguments like StartTime
		init(simConf);

		// init online visualization
		ArrayList<AbstractChartDataConsumer> chartDataConsumer = new ArrayList<AbstractChartDataConsumer>();
		for (Iterator it = space.getDataConsumers().iterator(); it.hasNext();) {
			Object abstractConsumer = it.next();

			if (abstractConsumer instanceof AbstractChartDataConsumer)
				chartDataConsumer.add((AbstractChartDataConsumer) abstractConsumer);
		}
		OnlineVisualisation vis = new OnlineVisualisation(chartDataConsumer);

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
			System.out.println("#TERMINATION TIME: # "+ terminationTime );
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

				// Hack:
//				vis.update();

				// Hack: Works right now only for single objects but not for all
				// of that type...
				// Additionally: only one part of the equation can be an
				// object...
				if (targetFunct.getObjectSource().getType().equalsIgnoreCase(Constants.ISPACE_OBJECT)) {

					// // String expression =
					// "$object.getProperty(\"ore\") >= 10";

					boolean res = EvaluateExpression.evaluate(space, targetFunct.getFunction(), targetFunct.getObjectSource().getName(), targetFunct.getObjectSource().getType());

					if (res) {
						System.out.println("#Client:RuntimeManagerPlan# Terminate experiment: Semantic termination condition has been evaluated being true.");
						// Experiment has reached Target Function. Terminate
						break;
					}
				} else {
					IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(space, targetFunct.getObjectSource().getName());
					IFuture fut = ((IComponentManagementService) space.getContext().getServiceContainer().getService(IComponentManagementService.class)).getExternalAccess(agentIdentifier);
					IExternalAccess exta = (IExternalAccess) fut.get(this);

					IOAVState state = ((ElementFlyweight) exta).getState();
					Object rCapability = ((ElementFlyweight) exta).getScope();

					// Evaluate condition/expression
					OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
					boolean res = EvaluateExpression.evaluateExpression(fetcher, targetFunct.getFunction());

					if (res) {
						System.out.println("#Client:RuntimeManagerPlan# Terminate experiment: Semantic termination condition has been evaluated being true.");
						// Experiment has reached Target Function. Terminate
						break;
					}
				}
			}

		} else {
			System.err.println("#RunTimeManagerPlan# Terminate Condition missing " + simConf);
		}

		// Get Observed Events from space
		// IServiceContainer container =
		// getExternalAccess().getServiceContainer();
		// DeltaTimeExecutor4Simulation simServ = (DeltaTimeExecutor4Simulation)
		// container.getService(DeltaTimeExecutor4Simulation.class);
		// ConcurrentHashMap<Long, ArrayList<ObservedEvent>> results =
		// simServ.getAllObservedValues();
		ConcurrentHashMap<Long, ArrayList<ObservedEvent>> results = (ConcurrentHashMap<Long, ArrayList<ObservedEvent>>) space.getProperty("observedEvents");

		// Stop Siumlation when target condition true.
		// IServiceContainer container =
		// getExternalAccess().getServiceContainer();
		// ISimulationService simServ = (ISimulationService)
		// container.getService(ISimulationService.class);
		// waitFor(5000);
		// simServ.pause();

		// exeServ.stop(null);
		// waitFor(5000);
		// simServ.start();
		// exeServ.start();

		sendResult(results);
		// simServ.start();
		// waitFor(2000);
		// simServ.shutdown(null);
		System.out.println("Trying to kill component....");
		vis.setExit();
		vis.dispose();
		// getExternalAccess().killAgent();
		IComponentManagementService ces = (IComponentManagementService) space.getContext().getServiceContainer().getService(IComponentManagementService.class);
		ces.destroyComponent(space.getContext().getComponentIdentifier());
		// getExternalAccess().getApplicationContext().killComponent(null);

	}

	private void sendResult(ConcurrentHashMap<Long, ArrayList<ObservedEvent>> observedEvents) {
		Map facts = (Map) getBeliefbase().getBelief("simulationFacts").getFact();
		facts.put(Constants.EXPERIMENT_END_TIME, new Long(getCurrentTime()));

		// Get the map of observed events from the beliefbase
		// HashMap observedEvents = (HashMap)
		// getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP).getFact();
		facts.put(Constants.OBSERVED_EVENTS_MAP, observedEvents);

		IComponentIdentifier[] receivers = new IComponentIdentifier[1];
		receivers[0] = getMasterAgent();

		System.out.println("Now sending result message to " + receivers[0]);

		// Send message
		IMessageEvent inform = createMessageEvent("inform_master_agent");
		inform.getParameterSet(SFipa.RECEIVERS).addValue(receivers[0]);
		inform.getParameter(SFipa.CONTENT).setValue(facts);

		try {
			sendMessage(inform);
		} catch (Exception e) {
			System.out.println("#RuntimeManagerPlan# Error on sending result message to Master Simulation Manager");
		}
	}

	private IComponentIdentifier getMasterAgent() {
		// Create a service description to search for.
		IDF df = (IDF) getScope().getServiceContainer().getService(IDF.class);
		IDFServiceDescription sd = df.createDFServiceDescription("master_simulation_agent", null, null);
		IDFComponentDescription ad = df.createDFComponentDescription(null, sd);
		// ISearchConstraints sc = df.createSearchConstraints(-1, 0);

		// Use a subgoal to search for a dealer-agent
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(ad);
		// ft.getParameter("constraints").setValue(sc);
		dispatchSubgoalAndWait(ft);
		IDFComponentDescription[] result = (IDFComponentDescription[]) ft.getParameterSet("result").getValues();

		if (result == null || result.length == 0) {
			getLogger().warning("No master simulation agent found.");
			fail();
		} else {
			// at least one matching component found,
			getLogger().info(result.length + " master simulation agent found");

			IComponentIdentifier masterAgent = result[0].getName();
			return masterAgent;
		}
		return null;
	}

	/**
	 * Save initial facts of this simulation run.
	 */
	private void init(SimulationConfiguration simConf) {

		IClockService clockservice = (IClockService) getScope().getServiceContainer().getService(IClockService.class);
		long startTime = clockservice.getTime();
		Map facts = (Map) getBeliefbase().getBelief("simulationFacts").getFact();
		facts.put(Constants.EXPERIMENT_START_TIME, new Long(startTime));
		getBeliefbase().getBelief("simulationFacts").setFact(facts);

		// Hack: Synchronize start time!
		System.out.println("-->StartTime at Client: " + startTime);
		AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace"));
		space.setProperty("REAL_START_TIME_OF_SIMULATION", startTime);
		// This is a hack for this special application.xml
		space.getSpaceObjectsByType("homebase")[0].setProperty("start_time", startTime);
		
		// Hack: This should happen when application is initialized and before
		// is starts --> when it is suspended
		addDataConsumerAndProvider(simConf);
	}

	private void addDataConsumerAndProvider(SimulationConfiguration simConf) {
		// IComponentManagementService executionService =
		// (IComponentManagementService)
		// getScope().getServiceContainer().getService(IComponentManagementService.class);
		AbstractEnvironmentSpace space = ((ContinuousSpace2D) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace"));

		// IFuture fut = executionService.getExternalAccess(space.get);
		// IApplicationExternalAccess exta = (IApplicationExternalAccess)
		// fut.get(this);
		// AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)
		// exta.getSpace("my2dspace");

		// Hack: Make sure space has been initialized...
		// int counterTmp = 0;
		// executionService.suspendComponent(comp);

		// while (exta.getSpace("my2dspace") == null) {
		// counterTmp++;
		// waitFor(100);
		// }
		// executionService.resumeComponent(comp);

		// AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)
		// exta.getSpace("my2dspace");
		IExpressionParser parser = new JavaCCExpressionParser();

		// add new data provider
		List<Dataprovider> providers = simConf.getDataproviderList();
		// List tmp = si.getPropertyList("dataproviders");

		// if(providers==null && tmp!=null)
		// providers = tmp;
		// else if(providers!=null && tmp!=null)
		// providers.addAll(tmp);

		// System.out.println("data providers: "+providers);
		if (providers != null) {
			// iteration through all dataprovider
			for (int i = 0; i < providers.size(); i++) {
				// Map dcol = (Map)providers.get(i);

				List<Source> sources = providers.get(i).getSourceList();
				IObjectSource[] provs = new IObjectSource[sources.size()];
				for (int j = 0; j < sources.size(); j++) {
					Source source = sources.get(j);
					String varname = source.getName() != null ? source.getName() : "$object";
					String objecttype = source.getObjecttype();
					boolean aggregate = source.isAggregate();
					// IParsedExpression dataexp =
					// getParsedExpression(source.getValue(), parser);
					IParsedExpression dataexp = null;
					IParsedExpression includeexp = getParsedExpression(source.getIncludecondition(), parser);// 
					provs[j] = new SpaceObjectSource(varname, space, objecttype, aggregate, dataexp, includeexp);
				}

				String tablename = providers.get(i).getName();
				List<Data> subdatas = providers.get(i).getDataList();
				String[] columnnames = new String[subdatas.size()];
				IParsedExpression[] exps = new IParsedExpression[subdatas.size()];
				for (int j = 0; j < subdatas.size(); j++) {
					Data subdata = subdatas.get(j);
					columnnames[j] = subdata.getName();
					exps[j] = getParsedExpression(subdata.getValue(), parser);
				}

				ITableDataProvider tprov = new DefaultDataProvider(space, provs, tablename, columnnames, exps);
				space.addDataProvider(tablename, tprov);
			}
		}

		// Create the data consumers.
		List<Dataconsumer> consumers = simConf.getDataconsumerList();

		// System.out.println("data consumers: "+consumers);
		if (consumers != null) {
			for (int i = 0; i < consumers.size(); i++) {
				Dataconsumer dcon = consumers.get(i);
				String name = dcon.getName();

				// Class clazz = (Class)MEnvSpaceInstance.getProperty(dcon,
				// "clazz");
				Class clazz = null;
				try {
					clazz = SReflect.findClass(dcon.getClazz(), toStringArray(simConf.getImportList()), ((ILibraryService) space.getContext().getServiceContainer().getService(ILibraryService.class))
							.getClassLoader());
					// clazz =
					// SUtil.class.getClassLoader().loadClass(tokenizer.nextToken().trim());
					// clazz =
					// SUtil.class.getClassLoader().loadClass(dcon.getClazz());
					// clazz
					// =((ILibraryService)space.getContext().getServiceContainer().getService(ILibraryService.class)).getClassLoader().loadClass(dcon.getClazz());
					// clazz
					// =((IContext)space.getContext()).getClassLoader().loadClass(dcon.getClazz());
					// clazz =
					// Class.forName(dcon.getClazz());//jadex.application.space.envsupport.evaluation.XYChartDataConsumer
					// clazz =
					// Class.forName("jadex.application.space.envsupport.evaluation.XYChartDataConsumer");
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
				for (int j = 0; j < dcon.getPropertyList().size(); j++) {
					Map map = new HashMap();
					map.put("dynamic", false);
					map.put("name", dcon.getPropertyList().get(j).getName());
					// map.put("value",
					// dcon.getPropertyList().get(j).getValue());
					// Hack: has to be "initially" an expression that can be
					// parsed
					map.put("value", getParsedExpression("\"" + dcon.getPropertyList().get(j).getValue() + "\"", parser));
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

	/**
	 * Helper method to convert string into parsed expression
	 * 
	 * @param expression
	 * @param parser
	 * @return
	 */
	private IParsedExpression getParsedExpression(String expression, IExpressionParser parser) {
		return expression == null ? null : parser.parseExpression(expression, null, null, null);
	}

	/**
	 * Compute Termination: Input: Mode=0 ->relative Time; Mode=1 ->absolute
	 * Time
	 * 
	 * @return the termination time as relative time, e.g. the time the
	 *         simulation has to run.
	 */
	private Long getTerminationTime(int mode, long value) {
		Long currentTime = new Long(getCurrentTime());

		if (mode == 0) {
			// Long relativeTime = new Long(10000);
			Long terminationTime = new Long(value + currentTime.longValue());
			System.out.println("StartTime: " + TimeConverter.longTime2DateString(currentTime) + "TerminationTime: " + TimeConverter.longTime2DateString(terminationTime));
			return new Long(value);
		} else {// TODO: There might be a problem with Day Light Savings Time!
			Calendar cal = Calendar.getInstance();
			// Date terminationTime = cal.getTime();
			Long duration = new Long(value - currentTime.longValue());
			System.out.println("StartTime: " + TimeConverter.longTime2DateString(currentTime) + "TerminationTime: " + TimeConverter.longTime2DateString(new Long(value)) + ", Duration: "
					+ duration.longValue());
			return duration;
		}
	}

	/**
	 * Returns current Time using the IClockService
	 * 
	 * @return
	 */
	private long getCurrentTime() {
		IClockService clockservice = (IClockService) getScope().getServiceContainer().getService(IClockService.class);
		return clockservice.getTime();
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
