package jadex.simulation.master;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.ExperimentResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jfree.ui.ArrowPanel;

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


		
		
//		System.out.println("#Master# Received message: "
//				+ content.get("STARTTIME") + ", "
//				+ content.get("EXPERIMENT_NUMBER"));
//		System.out.println("#Master# Lenght of Content: " + content.size());

		//System.out.println("#Master# Results of Simulation Run: ");
		
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int experimentRow = ((Integer) facts.get(Constants.EXPERIMENT_ROW_COUNTER)).intValue();
		int expInRow = ((Integer) facts.get(Constants.ROW_EXPERIMENT_COUNTER)).intValue();
		int totalRuns = ((Integer) facts.get(Constants.TOTAL_EXPERIMENT_COUNTER)).intValue();

		
	//	System.out.println("Map of Observed Events: ");
		HashMap<Long, ArrayList<ObservedEvent>> observedEventsMap = (HashMap<Long, ArrayList<ObservedEvent>>) content.get(Constants.OBSERVED_EVENTS_MAP);
		ArrayList sortedResultList = new ArrayList(observedEventsMap.keySet());  
		//Sort by timestamp
		Collections.sort(sortedResultList);
		
		
		ExperimentResult exRes = toExperimentResult(content, new ArrayList(observedEventsMap.values()));
		
		System.out.println("#Master#************************* Received message:\n " + exRes.toString());
		
		//Sorted output of results
		for (Object key  : sortedResultList) {			
			ArrayList<ObservedEvent> values = (ArrayList<ObservedEvent>) observedEventsMap.get(key);
			String tmp = "";
						
			
			for(ObservedEvent event : values){
				tmp += " - " + event.toString();
				//Persist value				
//				XMLHandler.writeXML(event, "abcresult.xml", ObservedEvent.class);
			}
			System.out.println(key.toString()  + " : " + tmp);
		}
		
		
		
		String res = "ID: " + experimentRow + "." + expInRow + " - Total #"   + totalRuns  + " (Size of ObservedEvents: " + sortedResultList.size() + ") ***********************************";
		System.out.println(res);
//		for (Iterator it = content.keySet().iterator(); it.hasNext();) {
//			Object key = it.next();
//			Object value = content.get(key);
//						
//			System.out.println(key.toString() + "-->" + value.toString());
//		}
//		System.out.println(res);

		
		//store result
		HashMap experimentResults = (HashMap) getBeliefbase().getBelief("experimentResults").getFact();
		experimentResults.put(totalRuns, exRes);
		getBeliefbase().getBelief("experimentResults").setFact(experimentResults);
		
		
		//trigger the start of the next experiment
		dispatchInternalEvent(createInternalEvent("triggerNewExperiment"));
				
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
	
	private ExperimentResult toExperimentResult(Map content, ArrayList<ArrayList<ObservedEvent>> events){
		SimulationConfiguration simConf = (SimulationConfiguration) content.get(Constants.SIMULATION_FACTS_FOR_CLIENT);
		
		long startTime = ((Long) content.get(Constants.EXPERIMENT_START_TIME)).longValue();
		long endTime = ((Long) content.get(Constants.EXPERIMENT_END_TIME)).longValue();
		String experimentId = (String) content.get(Constants.EXPERIMENT_ID);

		//transform events list, little hack...
		ArrayList<ObservedEvent> result = new ArrayList<ObservedEvent>();
		
		for(ArrayList<ObservedEvent> target : events){			
				for(ObservedEvent myEvent : target){
					long relativeTimestamp =  myEvent.getAbsoluteTimestamp() - startTime;
					myEvent.setRelativeTimestamp(relativeTimestamp);
					result.add(myEvent);	
				}				
		}

		return new ExperimentResult(startTime, endTime, experimentId, simConf.getName(), String.valueOf(simConf.getOptimization().getParameterSweeping().getCurrentValue()), simConf.getOptimization().getData().getName(), result);		
	}
}
