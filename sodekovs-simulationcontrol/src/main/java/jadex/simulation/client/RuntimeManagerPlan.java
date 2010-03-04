package jadex.simulation.client;

import jadex.base.ISimulationService;
import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.base.fipa.SFipa;
import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.TimeConverter;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.TargetFunction;
import jadex.simulation.model.Time;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

		// Init Arguments like StartTime

		// TEST
		ContinuousSpace2D spaceTMP = (ContinuousSpace2D) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace");
		// IExecutionService exeservice = (IExecutionService) space.getContext().getServiceContainer().getService(IExecutionService.class);
		IServiceContainer exeservice = spaceTMP.getContext().getServiceContainer();
		// DeltaTimeExecutor4Simulation exeservice = (DeltaTimeExecutor4Simulation) spaceTMP.getContext().getServiceContainer().getService(DeltaTimeExecutor4Simulation.class);
		// System.out.println(exeservice.getName());

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

			// HACK
			while (true) {
				waitFor(1000);
				ContinuousSpace2D space = (ContinuousSpace2D) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace");
				// ContinuousSpace2D space = (ContinuousSpace2D) getExternalAccess()
				// .getApplicationContext().getSpace("my2dspace");
				ISpaceObject object = space.getSpaceObjectsByType("homebase")[0];
				Integer ore = (Integer) object.getProperty("ore");
				if (ore.intValue() == 55) {
					// Experiment has reached Target Function. Terminate
					break;
				}
				// long currentTime = System.currentTimeMillis();
				// Long.valueOf(currentTime);
				// if (missiontime.compareTo(Long.valueOf(currentTime)) <= 0) {
				// Long missiontime = (Long) object.getProperty("missiontime");
				// break;
				// }
			}

		} else {
			System.err.println("#RunTimeManagerPlan# Terminate Condition missing " + simConf);
		}

		// waitFor(3000);

		// while (true) {
		// waitFor(1000);
		// ContinuousSpace2D space = (ContinuousSpace2D) getExternalAccess()
		// .getApplicationContext().getSpace("my2dspace");
		// ISpaceObject object = space.getSpaceObjectsByType("homebase")[0];
		// Integer ore = (Integer) object.getProperty("ore");
		// Long missiontime = (Long) object.getProperty("missiontime");
		// System.out.println("Trace: " + ore + " - " + missiontime);
		// long currentTime = System.currentTimeMillis();
		// Long.valueOf(currentTime);
		// if (missiontime.compareTo(Long.valueOf(currentTime)) <= 0) {
		// break;
		// }
		// }

		// getTerminationTime(1);
		// waitFor(getTerminationTime(1).longValue());
		// getPlanbase().getPlans("start_observer")[0].abortPlan();
		//
		// System.out.println("Simulation Finished....");
		// Stop Siumlation when target condition true.
		IServiceContainer container = getExternalAccess().getServiceContainer();
		// IExecutionService exeServ = (IExecutionService) container
		// .getService(IExecutionService.class);
		ISimulationService simServ = (ISimulationService) container.getService(ISimulationService.class);
		// waitFor(5000);
		// simServ.pause();

		// exeServ.stop(null);
		// waitFor(5000);
		// simServ.start();
		// exeServ.start();

		sendResult();
		// simServ.start();
		// waitFor(2000);
		// simServ.shutdown(null);
		System.out.println("Trying to kill component....");
		// getExternalAccess().killAgent();
		IComponentManagementService ces = (IComponentManagementService) spaceTMP.getContext().getServiceContainer().getService(IComponentManagementService.class);
		ces.destroyComponent(spaceTMP.getContext().getComponentIdentifier(), null);
		// getExternalAccess().getApplicationContext().killComponent(null);

	}

	// private void startApp() {
	// IServiceContainer container = getExternalAccess()
	// .getApplicationContext().getServiceContainer();
	// String appName = "CleanerWorldSpace";
	// String fileName =
	// "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\cleanerworld\\CleanerWorld.application.xml";
	// String configName = "One cleaner";
	// Map args = new HashMap();
	//
	// try {
	// SComponentFactory.createApplication(container, appName, fileName,
	// configName, args);
	// } catch (Exception e) {
	// // JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
	// // "Could not start application: "+e,
	// // "Application Problem", JOptionPane.INFORMATION_MESSAGE);
	// System.out.println("Could not start application...." + e);
	// }
	// }

	private void sendResult() {
		Map facts = (Map) getBeliefbase().getBelief("simulationFacts").getFact();
		facts.put(Constants.EXPERIMENT_END_TIME, new Long(getCurrentTime()));

		// Get the map of observed events from the beliefbase
		HashMap observedEvents = (HashMap) getBeliefbase().getBelief(Constants.OBSERVED_EVENTS_MAP).getFact();
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

		// IMessageEvent mevent = createMessageEvent("inform_target");
		// mevent.getParameterSet(SFipa.RECEIVERS).addValues(sentries);
		// mevent.getParameter(SFipa.CONTENT).setValue(target);
		// sendMessage(mevent);
	}

	private IComponentIdentifier getMasterAgent() {
		// System.out.println("Searching dealer...");
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

			// choose one dealer randomly out of all the dealer-agents
			// IComponentIdentifier dealer = result[new
			// Random().nextInt(result.length)].getName();
			IComponentIdentifier masterAgent = result[0].getName();
			// System.out.println("Found Simulation Master Agent: "
			// + masterAgent.getName());
			return masterAgent;
		}
		return null;
	}

	/**
	 * Save initial facts of this simulation run.
	 */
	private void init() {

		// final IClockService clockservice = (IClockService)container.getService(IClockService.class);

		// ContinuousSpace2D space = (ContinuousSpace2D) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace");

		IClockService clockservice = (IClockService) getScope().getServiceContainer().getService(IClockService.class);
		// long clockService = clockservice.getTime();
		// long systemTime = new Long(System.currentTimeMillis());

		// System.out.println("***********************************************************" + clockService + " - " + systemTime);

		Map facts = (Map) getBeliefbase().getBelief("simulationFacts").getFact();
		facts.put(Constants.EXPERIMENT_START_TIME, new Long(clockservice.getTime()));
		getBeliefbase().getBelief("simulationFacts").setFact(facts);
		//Hack: Synchronize start time! 
		((ContinuousSpace2D) ((IApplicationExternalAccess) getScope().getParent()).getSpace("my2dspace")).getSpaceObjectsByType("homebase")[0].setProperty("start_time", clockservice.getTime());
		
	}

	/**
	 * Compute Termination: Input: Mode=0 ->relative Time; Mode=1 ->absolute Time
	 * 
	 * @return the termination time as relative time, e.g. the time the simulation has to run.
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
