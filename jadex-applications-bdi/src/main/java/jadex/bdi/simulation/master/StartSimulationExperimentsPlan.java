package jadex.bdi.simulation.master;

import jadex.adapter.base.SComponentFactory;
import jadex.adapter.base.fipa.IDF;
import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.agr.AGRSpace;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.simulation.helper.Constants;
import jadex.bridge.IComponentExecutionService;
import jadex.service.IServiceContainer;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartSimulationExperimentsPlan extends Plan {

	// // IServiceContainer container =
	// getExternalAccess().getApplicationContext().getServiceContainer();
	// // String appName = "CleanerWorldSpace";
	// // String fileName =
	// "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\cleanerworld\\CleanerWorld.application.xml";
	// // String configName = "One cleaner";
	// // Map args = new HashMap();
	// //
	// // try
	// // {
	// // SComponentFactory.createApplication(container, appName, fileName,
	// configName, args);
	// // }
	// // catch(Exception e)
	// // {
	// ////
	// JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
	// "Could not start application: "+e,
	// //// "Application Problem", JOptionPane.INFORMATION_MESSAGE);
	// // System.out.println("Could not start application...." + e);
	// // }
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
		System.out.println("#StartSimulationExpPlan# Start Simulation Experiments at Master.");
		
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int experimentsToMake = ((Integer) facts.get(new String("TMP:EXPERIMENTS_TO_MAKE"))).intValue();
		int totalRuns = ((Integer) facts.get(Constants.TOTAL_EXPERIMENT_COUNTER)).intValue();
		int expInRow = ((Integer) facts.get(Constants.ROW_EXPERIMENT_COUNTER)).intValue();
		int experimentRow = ((Integer) facts.get(Constants.CURRENT_EXPERIMENT_ROW)).intValue();
		// init();

//		 startClientSimulators();

		
//		for (int i = 0; i < 3; i++) {
//			startApplication();
//			int runs = ((Integer) getBeliefbase().getBelief(
//					"runningSimulations").getFact()).intValue();
//			runs += 1;
//			getBeliefbase().getBelief("runningSimulations").setFact(
//					new Integer(runs));
//			waitFor(5000);
//		}

		for (int i = 0; i < experimentsToMake; i++) {
		
			String experimentID = 	experimentRow + "." + expInRow;
			
			startApplication(experimentID);
//			waitFor(2000);
//			startApplication();
		
//			int runs = ((Integer) getBeliefbase().getBelief("numberOfRuns")
//					.getFact()).intValue();
			System.out.println("#StartSimulationExpPlan# Start new Simulation Experiment. Nr.:" + totalRuns);
			totalRuns ++;
			expInRow++;
//			getBeliefbase().getBelief("numberOfRuns").setFact(new Integer(runs));
			
//			int runs = ((Integer) getBeliefbase().getBelief(
//					"runningSimulations").getFact()).intValue();
//			runs += 1;
//			getBeliefbase().getBelief("runningSimulations").setFact(
//					new Integer(runs));
//			waitFor(5000);
			waitForInternalEvent("triggerNewExperiment");
			System.out.println("1Received Results!!!!");
			//HACK: Ein warten scheint notwendig zu sein..., damit Ausführung korrekt läuft.
			waitFor(2000);
//			System.out.println("2Received Results!!!!");
			facts.put(Constants.TOTAL_EXPERIMENT_COUNTER, new Integer(totalRuns));
			facts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(expInRow));
			getBeliefbase().getBelief("generalSimulationFacts").setFact(facts);
		}
		
		
		dispatchInternalEvent(createInternalEvent("triggerExperimentRowEvaluation"));
		//
		
		// IServiceContainer container =
		// getExternalAccess().getApplicationContext().getServiceContainer();
		// getExternalAccess().
		// IServiceContainer cc = (IServiceContainer)
		// getExternalAccess().getPlatformComponent();
		// IApplicationContext context =
		// getExternalAccess().getApplicationContext();
		// IServiceContainer container =
		// ((ApplicationContext)context).getServiceContainer();

		// String appName = "CleanerWorldSpace";
		// String fileName =
		// "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\cleanerworld\\CleanerWorld.application.xml";
		// String configName = "One cleaner";
		// Map args = new HashMap();
		//	
		try {
			// SComponentFactory.createApplication(container, appName, fileName,
			// configName, args);
			// waitFor(5000);
			// SComponentFactory.createApplication(container, appName, fileName,
			// configName, args);

			// TODO: Stop execution of experiment
			// Stop Siumlation when target condition true.
			// IServiceContainer container =
			// getExternalAccess().getServiceContainer();
			// ISimulationService simServ =
			// (ISimulationService)container.getService(ISimulationService.class);
			// simServ.pause();
		} catch (Exception e) {
			// JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
			// "Could not start application: "+e,
			// "Application Problem", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Could not start application...." + e);
		}
		// System.out.println("Started AGENT Simulation Client!!!!!!!!");
	}

	private void startClientSimulators() {
		IGoal ca = createGoal("ams_create_agent");
		String type = "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\simulation\\client\\ClientSimulator.agent.xml";
		ca.getParameter("type").setValue(type);
		
		
		Map args = new HashMap();
//		java jadex.adapter.standalone.Platform "hello:jadex.examples.helloworld.HelloWorld(default, msg=\"Hi!\")"
		args.put("msg", "Novo");
//		args.put("dealer", dealeraid);
		ca.getParameter("arguments").setValue(args);
//		agent.dispatchTopLevelGoalAndWait(start);
		
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
		dispatchSubgoalAndWait(ca);
	}

	private void startApplication(String experimentID) {
//		IApplicationExternalAccess app = (IApplicationExternalAccess)getScope().getServiceContainer()		
//		AGRSpace agrs = (AGRSpace)app.getSpace("myagrspace");
		
//		IServiceContainer container = getExternalAccess()
//				.getApplicationContext().getServiceContainer();
//		IServiceContainer container = (IServiceContainer)getScope().getServiceContainer(); 
		String appName = "MarsWorld4SimulationExperiments#" + experimentID; // change Name here!
		String fileName = "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\marsworld\\MarsWorld4SimulationExperiments.application.xml";
		String configName = "1 Sentry / 2 Producers / 3 Carries";
		Map args = new HashMap();

		try {			
			IComponentExecutionService executionService =  (IComponentExecutionService) getScope().getServiceContainer().getService(IComponentExecutionService.class);
			
//			SComponentFactory.createApplication(container, appName, fileName,
//					configName, args);
			executionService.createComponent(appName, fileName, configName, args, false, null, null, null);
//			createApplication(container, appName, fileName,
//					configName, args);
		} catch (Exception e) {
			// JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
			// "Could not start application: "+e,
			// "Application Problem", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Could not start application...." + e);
		}		
	}
}
