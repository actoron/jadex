package jadex.bdi.simulation.client;

import jadex.adapter.base.ISimulationService;
import jadex.adapter.base.SComponentFactory;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.simulation.helper.ResClass;
import jadex.bdi.simulation.helper.TimeConverter;
import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceContainer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RuntimeManagerPlan extends Plan {

	//	
	//	
	//
	// IGoal ca = createGoal("ams_create_agent");
	// //
	// ca.getParameter("type").setValue("..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\cleanerworld\\CleanerWorld.application.xml");
	// ca.getParameter("type").setValue("/jadex-applications-bdi/src/main/java/jadex/bdi/simulation/SimulationManager.agent.xml");
	//	
	// // Map<String, Object> arguments = new HashMap<String, Object>(); //
	// Hack:
	// // this works only for agents arguments.put("conf", ap); // that are
	// // started on the same platform
	// // arguments.put("current_server", appsrv); // ...
	//
	// // arguments.put("Position", brokerObj.getPosition());
	// // arguments.put("RoadMap", createManipulatedMap());
	// // arguments.put("RoutingStrategy", brokerObj.getRoutingStrategy());
	// // ca.getParameter("arguments").setValue(arguments); // ...
	// // System.out.println("1 ->" + ca.getLifecycleState());
	// dispatchSubgoalAndWait(ca);

	public void body() {
		System.out.println("Started CLIENT Simulation run....");
		String msg = (String) getBeliefbase().getBelief("msg").getFact();
		System.out.println("*******************: " + msg);
		// boolean targetCondition = false;
//		 startApp();
		init();
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

//		getTerminationTime(1);
		waitFor(getTerminationTime(1).longValue());
//		getPlanbase().getPlans("start_observer")[0].abortPlan();
//
//		System.out.println("Simulation Finished....");
		// Stop Siumlation when target condition true.
		IServiceContainer container = getExternalAccess().getServiceContainer();
//		IExecutionService exeServ = (IExecutionService) container
//		.getService(IExecutionService.class);
		ISimulationService simServ = (ISimulationService) container
				.getService(ISimulationService.class);
//		waitFor(5000);
//		simServ.pause();
		
//		exeServ.stop(null);
//		waitFor(5000);
//		simServ.start();
//		exeServ.start();
		
		sendResult();
//		simServ.start();
//		waitFor(2000);
//		 simServ.shutdown(null);
		getExternalAccess().killAgent();
//		getExternalAccess().getApplicationContext().killComponent(null);

	}

//	private void startApp() {
//		IServiceContainer container = getExternalAccess()
//				.getApplicationContext().getServiceContainer();
//		String appName = "CleanerWorldSpace";
//		String fileName = "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\cleanerworld\\CleanerWorld.application.xml";
//		String configName = "One cleaner";
//		Map args = new HashMap();
//
//		try {
//			SComponentFactory.createApplication(container, appName, fileName,
//					configName, args);
//		} catch (Exception e) {
//			// JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
//			// "Could not start application: "+e,
//			// "Application Problem", JOptionPane.INFORMATION_MESSAGE);
//			System.out.println("Could not start application...." + e);
//		}
//	}

	private void sendResult() {

		IComponentIdentifier[] receivers = new IComponentIdentifier[1];
		receivers[0] = getMasterAgent();

		System.out.println("Now sending result message to " + receivers[0]);

		// Send message
		IMessageEvent inform = createMessageEvent("inform_master_agent");
		inform.getParameterSet(SFipa.RECEIVERS).addValue(receivers[0]);
		// inform.getParameter(SFipa.CONTENT).setValue(new String("antolino"));
		
//		ResClass ccc = new ResClass();
		
//		inform.getParameter(SFipa.CONTENT).setValue(ccc);
		
		inform.getParameter(SFipa.CONTENT).setValue(
		getBeliefbase().getBelief("simulationResults").getFact());
		
		
		try{
		sendMessage(inform);
		}catch (Exception e ){
			System.out.println("EEE");
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
		IDFServiceDescription sd = df.createDFServiceDescription(
				"master_simulation_agent", null, null);
		IDFAgentDescription ad = df.createDFAgentDescription(null, sd);
		// ISearchConstraints sc = df.createSearchConstraints(-1, 0);

		// Use a subgoal to search for a dealer-agent
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(ad);
		// ft.getParameter("constraints").setValue(sc);
		dispatchSubgoalAndWait(ft);
		IDFAgentDescription[] result = (IDFAgentDescription[]) ft
				.getParameterSet("result").getValues();

		if (result == null || result.length == 0) {
			getLogger().warning("No master simulation agent found.");
			fail();
		} else {
			// at least one matching AgentDescription found,
			getLogger().info(result.length + " master simulation agent found");

			// choose one dealer randomly out of all the dealer-agents
			// IComponentIdentifier dealer = result[new
			// Random().nextInt(result.length)].getName();
			IComponentIdentifier masterAgent = result[0].getName();
			System.out.println("Found Simulation Master Agent: "
					+ masterAgent.getName());
			return masterAgent;
		}
		return null;
	}

	/**
	 * Save initial facts of this simulation run.
	 */
	private void init() {
		Map facts = new HashMap();
		facts
				.put(new String("STARTTIME"), new Long(System
						.currentTimeMillis()));
		facts.put(new String("EXPERIMENT_NUMBER"), new Integer(1));
		getBeliefbase().getBelief("simulationResults").setFact(facts);
	}

	/**
	 * Compute Termination Time 1 = relative Time 2 = absolute Time
	 * 
	 * @return
	 */
	private Long getTerminationTime(int mode) {
		if (mode == 1) {
			Long relativeTime = new Long(10000);
			Long currentTime = new Long(System.currentTimeMillis());
			Long res = new Long(relativeTime.longValue()
					+ currentTime.longValue());
			System.out.println("StartTime: "
					+ TimeConverter.longTime2DateString(currentTime)
					+ "TerminationTime: "
					+ TimeConverter.longTime2DateString(res));
			return relativeTime;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(2010, 0, 6,  14, 16, 42);
//			Date terminationTime = cal.getTime();			
			Long currentTime = new Long(System.currentTimeMillis());			
			Long res = new Long(cal.getTimeInMillis() - currentTime.longValue());
			System.out.println("StartTime: "
					+ TimeConverter.longTime2DateString(currentTime)
					+ "TerminationTime: "
					+ TimeConverter.longTime2DateString(new Long(cal.getTimeInMillis())) + ", Duration: " + res.longValue());
			return res;
		}
	}

}
