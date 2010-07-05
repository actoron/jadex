package jadex.simulation.master;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.simulation.evaluation.IntermediateEvaluation;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.Observer;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.ExperimentResult;
import jadex.simulation.model.result.IntermediateResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for dealing with the result of one single simulation experiment.
 * 
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

		// System.out.println("#Master# Received message: "
		// + content.get("STARTTIME") + ", "
		// + content.get("EXPERIMENT_NUMBER"));
		// System.out.println("#Master# Lenght of Content: " + content.size());

		// System.out.println("#Master# Results of Simulation Run: ");

		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		int experimentRow = ((Integer) facts.get(Constants.EXPERIMENT_ROW_COUNTER)).intValue();
		int expInRow = ((Integer) facts.get(Constants.ROW_EXPERIMENT_COUNTER)).intValue();
		int totalRuns = ((Integer) facts.get(Constants.TOTAL_EXPERIMENT_COUNTER)).intValue();

		SimulationConfiguration simConf = (SimulationConfiguration) getBeliefbase().getBelief("simulationConf").getFact();

		// System.out.println("Map of Observed Events: ");
		ConcurrentHashMap<Long, ArrayList<ObservedEvent>> observedEventsMap = (ConcurrentHashMap<Long, ArrayList<ObservedEvent>>) content.get(Constants.OBSERVED_EVENTS_MAP);
		ArrayList sortedResultList = new ArrayList(observedEventsMap.keySet());
		// Sort by timestamp
		Collections.sort(sortedResultList);

 
		//Hack: Since the Executor starts observing the application while the application is initilazed the early events have to be deleted:
		//"the official" start time -> StartTime at Executor != StartTime at application (is delayed)
		deleteEventsBefore(sortedResultList, observedEventsMap, ((Long)content.get(Constants.EXPERIMENT_START_TIME)).longValue());

		ExperimentResult experimentRes = toExperimentResult(content, new ArrayList(observedEventsMap.values()), simConf);

		System.out.println("#Master#************************* Received message:\n " + experimentRes.toString() + "\n\n");

		// Sorted output of results
		for (Object key : sortedResultList) {
			ArrayList<ObservedEvent> values = (ArrayList<ObservedEvent>) observedEventsMap.get(key);
			String tmp = "";

			for (ObservedEvent event : values) {
				tmp += " - " + event.toString();
				// Persist value
				// XMLHandler.writeXML(event, "abcresult.xml",
				// ObservedEvent.class);
			}
			System.out.println(key.toString() + " : " + tmp);
		}

		String res = "ID: " + experimentRow + "." + expInRow + " - Total #" + totalRuns + " (Size of ObservedEvents: " + sortedResultList.size() + ") ***********************************";
		System.out.println(res);
		// for (Iterator it = content.keySet().iterator(); it.hasNext();) {
		// Object key = it.next();
		// Object value = content.get(key);
		//						
		// System.out.println(key.toString() + "-->" + value.toString());
		// }
		// System.out.println(res);

		// store result
		HashMap experimentResults = (HashMap) getBeliefbase().getBelief("experimentResults").getFact();
		// HACK! - 26-5-10
		experimentResults.put(totalRuns, experimentRes);
		getBeliefbase().getBelief("experimentResults").setFact(experimentResults);

		// do evaluation of intermediate results of an ensemble, i.e. the
		// results of the already conducted experiments of this ensemble.

		// HACK! - 26-5-10
		// experimentRes.setEvents(new ArrayList<ObservedEvent> ());

		IntermediateResult interRes = IntermediateEvaluation.updateIntermediateResults(simConf, (IntermediateResult) getBeliefbase().getBelief("intermediateResults").getFact(), experimentRes);
		getBeliefbase().getBelief("intermediateResults").setFact(interRes);

		// ControlCenter gui = (ControlCenter)
		// getBeliefbase().getBelief("tmpGUI").getFact();
		// gui.updateCurrentEnsembleTable(experimentRow, expInRow + 1,
		// interRes);

		// trigger the start of the next experiment
		dispatchInternalEvent(createInternalEvent("triggerNewExperiment"));

	}

	// /**
	// * HACK!
	// */
	// private void startApplication() {
	// int runs = ((Integer) getBeliefbase().getBelief(
	// "numberOfRuns").getFact()).intValue();
	//
	// System.out.println("*******Restarted Runs...:" + runs);
	// IServiceContainer container = getExternalAccess()
	// .getApplicationContext().getServiceContainer();
	// String appName = "MarsWorld4SimulationExperiments";
	// String fileName =
	// "..\\jadex-applications-bdi\\target\\classes\\jadex\\bdi\\examples\\marsworld\\MarsWorld4SimulationExperiments.application.xml";
	// String configName = "1 Sentry / 2 Producers / 3 Carries";
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

	private ExperimentResult toExperimentResult(Map content, ArrayList<ArrayList<ObservedEvent>> events, SimulationConfiguration simConf) {

		long startTime = ((Long) content.get(Constants.EXPERIMENT_START_TIME)).longValue();
		long endTime = ((Long) content.get(Constants.EXPERIMENT_END_TIME)).longValue();
		String experimentId = (String) content.get(Constants.EXPERIMENT_ID);

		// init buckets, in order to filter events, that belong to observers
		// which only need the last "observedValue".
		HashMap<String, ArrayList<ObservedEvent>> eventsToFilter = getBuckets(simConf);

		// transform events list, little hack...
		ArrayList<ObservedEvent> result = new ArrayList<ObservedEvent>();

		for (ArrayList<ObservedEvent> target : events) {
			for (ObservedEvent myEvent : target) {
				long relativeTimestamp = myEvent.getAbsoluteTimestamp() - startTime;
				myEvent.setRelativeTimestamp(relativeTimestamp);
				// event does not have to be filtered
				if (eventsToFilter.get(myEvent.getNameOfObservedData()) == null) {
					result.add(myEvent);
				} else {// --- put event into the right bucket
					ArrayList<ObservedEvent> list = eventsToFilter.get(myEvent.getNameOfObservedData());
					list.add(myEvent);
					eventsToFilter.put(myEvent.getNameOfObservedData(), list);
				}
			}
		}

		// apply filter on selected events
		result = filterResults(result, eventsToFilter);

		return new ExperimentResult(startTime, endTime, experimentId, simConf.getName(), String.valueOf(simConf.getOptimization().getParameterSweeping().getCurrentValue()), simConf.getOptimization()
				.getData().getName(), result);
	}

	/**
	 * Create buckets, in order to filter events, that belong to observers which
	 * only need the last "observedValue".
	 * 
	 * @param simConf
	 */
	private HashMap<String, ArrayList<ObservedEvent>> getBuckets(SimulationConfiguration simConf) {

		HashMap<String, ArrayList<ObservedEvent>> result = new HashMap<String, ArrayList<ObservedEvent>>();
		// last indicates this "special" type of observer
		for (Observer obs : simConf.getObserverList()) {
			if (obs.getFilter().getMode().equals("last")) {
				result.put(obs.getData().getName(), new ArrayList<ObservedEvent>());
			}
		}
		return result;
	}

	/**
	 * Filter those events that are of interest. For some observer data (like
	 * duration) only the last observed event is of interest, whereas others
	 * like a Logger need every observed value. Right now: Add last value for a
	 * observer to results list.
	 * 
	 * @param events
	 * @param eventsToFilter
	 * @return
	 */
	private ArrayList<ObservedEvent> filterResults(ArrayList<ObservedEvent> resultList, HashMap<String, ArrayList<ObservedEvent>> eventsToFilter) {

		// --- add the last value for each observer to result list

		for (Iterator it = eventsToFilter.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			ArrayList<ObservedEvent> list = eventsToFilter.get(key);
			sortList(list);
			// hack: to avoid arrayOutOfBoundsException if list empty
			if (list.size() > 0) {
				resultList.add(list.get(list.size() - 1));
			}
		}
		return resultList;
	}

	/**
	 * Returns the list of observed events ascendingly ordered by relative
	 * timestamp.
	 */
	public void sortList(ArrayList<ObservedEvent> list) {
		Collections.sort(list, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				// return new Long(((RowResult) arg0).getId()).compareTo(new
				// Long(((RowResult) arg1).getId()));
				return Long.valueOf(((ObservedEvent) arg0).getRelativeTimestamp()).compareTo(Long.valueOf(((ObservedEvent) arg1).getRelativeTimestamp()));
			}
		});
	}
	
	/**
	 * Deletes events from List and Map that occurred before the starttime. 
	 * @param sortedResultList
	 * @param observedEventsMap
	 * @param startTime
	 */
	private void deleteEventsBefore(ArrayList sortedResultList, ConcurrentHashMap<Long, ArrayList<ObservedEvent>> observedEventsMap, long startTime){
		ArrayList<Long> values2Remove = filterSortedList(sortedResultList, startTime);
		
		//Remove values from both: list and hash map
		for(int i=0; i<values2Remove.size(); i++){
			sortedResultList.remove(0);
			observedEventsMap.remove(values2Remove.get(i));
		}		
	}
	
	
	/**
	 * Filters given list. Returns a new list that contains all values that are smaller than the given timestamp.
	 * @param sortedList
	 * @param timestamp
	 * @return
	 */
	private ArrayList<Long> filterSortedList(ArrayList sortedList, long timestamp){
		ArrayList<Long> res = new ArrayList<Long>();
		
		for(int i=0; i<sortedList.size(); i++){
			if((Long) sortedList.get(i) < timestamp){
				res.add((Long) sortedList.get(i));
			}else{
				break;
			}			
		}		
		return res;		
	}
	
}
