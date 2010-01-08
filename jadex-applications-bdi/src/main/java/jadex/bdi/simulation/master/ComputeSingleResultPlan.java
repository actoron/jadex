package jadex.bdi.simulation.master;

import jadex.adapter.base.SComponentFactory;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.simulation.helper.Constants;
import jadex.bdi.simulation.helper.ResClass;
import jadex.service.IServiceContainer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Responsible for dealing with the resul of one single simulation experiment.
 * @author vilenica
 *
 */
public class ComputeSingleResultPlan extends Plan {

	public void body() {
		IMessageEvent msg = (IMessageEvent) getReason();
		// String content = (String)msg.getParameter(SFipa.CONTENT).getValue();
		// System.out.println("#Master# Received message: " + content);
		HashMap content = (HashMap) msg.getParameter(SFipa.CONTENT).getValue();

		// ResClass content = (ResClass)
		// msg.getParameter(SFipa.CONTENT).getValue();
		// System.out.println("#Master# Received message: " + content.getA() +
		// " - " + content.getBb()
		// );

		System.out.println("#Master# Received message: "
				+ content.get("STARTTIME") + ", "
				+ content.get("EXPERIMENT_NUMBER"));
		System.out.println("#Master# Lenght of Content: " + content.size());

		System.out.println("Results of Simulation Run: ");
		
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int experimentRow = ((Integer) facts.get(Constants.CURRENT_EXPERIMENT_ROW)).intValue();
		int expInRow = ((Integer) facts.get(Constants.ROW_EXPERIMENT_COUNTER)).intValue();
		int totalRuns = ((Integer) facts.get(Constants.TOTAL_EXPERIMENT_COUNTER)).intValue();
		String res = "ID: " + experimentRow + "." + expInRow + "("   + totalRuns  + ") ***********************************";
		
		System.out.println(res);
		for (Iterator it = content.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			Object value = content.get(key);
						
			System.out.println(key.toString() + "-->" + value.toString());
		}
		System.out.println(res);

		//trigger the start of the next experiment
		dispatchInternalEvent(createInternalEvent("triggerNewExperiment"));
		
		
//		int runs = ((Integer) getBeliefbase().getBelief("runningSimulations")
//				.getFact()).intValue();
//		runs -= 1;
//		getBeliefbase().getBelief("runningSimulations").setFact(
//				new Integer(runs));
//
//		// Check, to start second round!
//		int receivedResults = ((Integer) getBeliefbase().getBelief(
//				"runningSimulations").getFact()).intValue();
//		System.out.println("***********************Checking for Restart! -->"
//				+ receivedResults);
//
//		if (receivedResults == 0) {
//			for (int i = 0; i < 3; i++) {
//				startApplication();
//				runs = ((Integer) getBeliefbase().getBelief(
//						"runningSimulations").getFact()).intValue();
//				runs += 1;
//				getBeliefbase().getBelief("runningSimulations").setFact(
//						new Integer(runs));
//
//				runs = ((Integer) getBeliefbase().getBelief(
//						"numberOfRuns").getFact()).intValue();
//				runs += 1;
//				getBeliefbase().getBelief("numberOfRuns").setFact(
//						new Integer(runs));
//
//				waitFor(5000);
//			}
//		}
	}

//	/**
//	 * HACK!
//	 */
//	private void startApplication() {
//		int runs = ((Integer) getBeliefbase().getBelief(
//		"numberOfRuns").getFact()).intValue();
//
//		System.out.println("*******Restarted Runs...:" + runs);
//		IServiceContainer container = getExternalAccess()
//				.getApplicationContext().getServiceContainer();
//		String appName = "MarsWorld4SimulationExperiments";
//		String fileName = "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\marsworld\\MarsWorld4SimulationExperiments.application.xml";
//		String configName = "1 Sentry / 2 Producers / 3 Carries";
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
}
