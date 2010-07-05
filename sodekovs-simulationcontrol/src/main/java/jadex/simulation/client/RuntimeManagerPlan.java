package jadex.simulation.client;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.space2d.ContinuousSpace2D;
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
import jadex.rules.state.IOAVState;
import jadex.service.clock.IClockService;
import jadex.simulation.helper.AgentMethods;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.EvaluateExpression;
import jadex.simulation.helper.TimeConverter;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.TargetFunction;
import jadex.simulation.model.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeManagerPlan extends Plan {

	public void body() {
		HashMap simFacts = (HashMap) getBeliefbase().getBelief("simulationFacts").getFact();
		SimulationConfiguration simConf = (SimulationConfiguration) simFacts.get(Constants.SIMULATION_FACTS_FOR_CLIENT);
		String experimentID = (String) simFacts.get(Constants.EXPERIMENT_ID);
		int parameterSweepValue = simConf.getOptimization().getParameterSweeping().getCurrentValue();
		String parameterSweepName = simConf.getOptimization().getData().getName();

		// Map simFacts = new HashMap();
		// simFacts.put(Constants.SIMULATION_FACTS_FOR_CLIENT, simConf);
		// simFacts.put(Constants.EXPERIMENT_ID, experimentID);

		// Map args = new HashMap();
		// args.put(Constants.EXPERIMENT_ID, experimentID);
		// args.put(Constants.SIMULATION_FACTS_FOR_CLIENT, simFacts);

		System.out.println("#Client# Started CLIENT Simulation run....: " + simConf.getName() + " - " + experimentID + ", currentVal: " + parameterSweepValue);
		// String msg = (String) getBeliefbase().getBelief("msg").getFact();

		// System.out.println("*******************: " + msg + " - " +
		// mapTMP.get("HSV"));
		// boolean targetCondition = false;
		// startApp();



		// Get Space
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace");
//		IServiceContainer exeservice = spaceTMP.getContext().getServiceContainer(); 
		// DeltaTimeExecutor4Simulation exeservice =
		// (DeltaTimeExecutor4Simulation)
		// spaceTMP.getContext().getServiceContainer().getService(DeltaTimeExecutor4Simulation.class);
		// System.out.println(exeservice.getName());

		// Init Arguments like StartTime
		init();

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

//				ContinuousSpace2D space = (ContinuousSpace2D) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace");

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

		//Get Observed Events from space
//		IServiceContainer container = getExternalAccess().getServiceContainer();
//		DeltaTimeExecutor4Simulation simServ = (DeltaTimeExecutor4Simulation) container.getService(DeltaTimeExecutor4Simulation.class);
//		ConcurrentHashMap<Long, ArrayList<ObservedEvent>> results = simServ.getAllObservedValues();
		ConcurrentHashMap<Long, ArrayList<ObservedEvent>> results = (ConcurrentHashMap<Long, ArrayList<ObservedEvent>> ) space.getProperty("observedEvents");		

		// Stop Siumlation when target condition true.
//		IServiceContainer container = getExternalAccess().getServiceContainer();
//		ISimulationService simServ = (ISimulationService) container.getService(ISimulationService.class);
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
		// getExternalAccess().killAgent();
		IComponentManagementService ces = (IComponentManagementService) space.getContext().getServiceContainer().getService(IComponentManagementService.class);
		ces.destroyComponent(space.getContext().getComponentIdentifier());
		// getExternalAccess().getApplicationContext().killComponent(null);

	}

	private void sendResult(ConcurrentHashMap<Long, ArrayList<ObservedEvent>> observedEvents) {
		Map facts = (Map) getBeliefbase().getBelief("simulationFacts").getFact();
		facts.put(Constants.EXPERIMENT_END_TIME, new Long(getCurrentTime()));

		// Get the map of observed events from the beliefbase
//		HashMap observedEvents = (HashMap) getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP).getFact();
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
	private void init() {

		IClockService clockservice = (IClockService) getScope().getServiceContainer().getService(IClockService.class);
		long startTime = clockservice.getTime();
		Map facts = (Map) getBeliefbase().getBelief("simulationFacts").getFact();
		facts.put(Constants.EXPERIMENT_START_TIME, new Long(startTime));
		getBeliefbase().getBelief("simulationFacts").setFact(facts);		
		// Hack: Synchronize start time!
		System.out.println("-->StartTime at Client: " + startTime );
		((ContinuousSpace2D) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace")).getSpaceObjectsByType("homebase")[0].setProperty("start_time", startTime);

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
}
