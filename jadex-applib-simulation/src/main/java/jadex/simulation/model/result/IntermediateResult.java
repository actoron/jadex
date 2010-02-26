package jadex.simulation.model.result;

import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.Observer;
import jadex.simulation.model.SimulationConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains the intermediate results of an ensemble, i.e. the results of the already conducted experiments of this ensemble. The results contain the aggregated information of the ExperimentResults classes of the ensemble.
 * 
 * @author vilenica
 * 
 */
public class IntermediateResult {

	private SimulationConfiguration simConf;
	private int currentEnsembleNumber = 0;
	private int currentExperimentNumber = 0;
	// --- contains all observed events of all experiments sorted by the name of the observer
	private HashMap<String, ArrayList<ObservedEvent>> sortedObservedEvents = new HashMap<String, ArrayList<ObservedEvent>>();
	// --- contains the results of the observers from the last experiment; this is need for the control center to display the values of the latest experiment
	private HashMap<String, ArrayList<String>> latestObserverResults = new HashMap<String, ArrayList<String>>();	
	// --- contains the results of the intermediate statistical evaluation for each observer type
	private HashMap<String, HashMap<String, String>> intermediateStats = new HashMap<String, HashMap<String,String>>();

	public IntermediateResult(SimulationConfiguration simConf) {
		this.simConf = simConf;
		initAllHashMaps();
	}

	public IntermediateResult(int currentEnsembleNumber, int currentExperimentNumber, SimulationConfiguration simConf) {
		this.currentEnsembleNumber = currentEnsembleNumber;
		this.currentExperimentNumber = currentExperimentNumber;
		this.simConf = simConf;
		initAllHashMaps();
	}

	public int getCurrentEnsembleNumber() {
		return currentEnsembleNumber;
	}

	public void setCurrentEnsembleNumber(int currentEnsembleNumber) {
		this.currentEnsembleNumber = currentEnsembleNumber;
	}

	public int getCurrentExperimentNumber() {
		return currentExperimentNumber;
	}

	public void setCurrentExperimentNumber(int currentExperimentNumber) {
		this.currentExperimentNumber = currentExperimentNumber;
	}

	public HashMap<String, ArrayList<ObservedEvent>> getSortedObservedEvents() {
		return sortedObservedEvents;
	}

	/**
	 * Sorted according to observer name
	 * 
	 * @param sortedObservedEvents
	 */
	public void setSortedObservedEvents(HashMap<String, ArrayList<ObservedEvent>> sortedObservedEvents) {
		this.sortedObservedEvents = sortedObservedEvents;
	}

	/**
	 * Add observed event to hash map sorted by observer name
	 * 
	 * @param observedEvent
	 */
	public void addObservedEventToSortedList(ObservedEvent observedEvent) {
		ArrayList<ObservedEvent> list = this.sortedObservedEvents.get(observedEvent.getNameOfObservedData());
		list.add(observedEvent);
		this.sortedObservedEvents.put(observedEvent.getNameOfObservedData(), list);
	}

	/**
	 * Add list of observed events to hash map sorted by observer name
	 * 
	 * @param observedEvent
	 */
	public void addObservedEventsListToSortedList(ArrayList<ObservedEvent> observedEventsList) {
		for (ObservedEvent observedEvent : observedEventsList) {
			addObservedEventToSortedList(observedEvent);
		}
	}

	public HashMap<String, ArrayList<String>> getLatestObserverResults() {
		return latestObserverResults;
	}

	public void setLatestObserverResults(HashMap<String, ArrayList<String>> latestObserverResults) {
		this.latestObserverResults = latestObserverResults;
	}
	
	/**
	 * Add observed event from latest experiment to hash map sorted by observer name
	 * 
	 * @param observedEvent
	 */
	public void addLatestObserverResults(ObservedEvent observedEvent) {
		ArrayList<String> list = this.latestObserverResults.get(observedEvent.getNameOfObservedData());
		list.add(observedEvent.getValue());
		this.latestObserverResults.put(observedEvent.getNameOfObservedData(), list);
	}
	
	/**
	 * Add list of observed events from latest experiment to hash map sorted by observer name
	 * 
	 * @param observedEvent
	 */
	public void addLatestObserverResultsList(ArrayList<ObservedEvent> observedEventsList) {
		for (ObservedEvent observedEvent : observedEventsList) {
			addLatestObserverResults(observedEvent);
		}
	}
	
//	/**
//	 * List/ Hash Map needs to be cleaned before a new experiment starts.
//	 */
//	public void removeAllFromLatestObserverResults(){
//		this.latestObserverResults = new HashMap<String, ArrayList<String>>();
//	}
	
	/**
	 * 1. HashMap: Key: Name of observer
	 * 2. HashMap: Key: Name of statistical type, Value: computed value
	 * @return
	 */
	public HashMap<String, HashMap<String, String>> getIntermediateStats() {
		return intermediateStats;
	}

	public void setIntermediateStats(HashMap<String, HashMap<String, String>> intermediateStats) {
		this.intermediateStats = intermediateStats;
	}
	
//	/**
//	 * List/ Hash Map needs to be cleaned before a new experiment starts.
//	 */
//	public void removeAllFromIntermediateStats(){
//		this.intermediateStats = new HashMap<String, HashMap<String,String>>();
//	}

	/**
	 * Prepare "buckets" for each observer to store the results there
	 * 
	 * @param simConf
	 */
	private void initAllHashMaps() {

		//--- do it for both lists: containing all observed events and for the list containing the results from the latest experiment
		for (Observer obs : simConf.getObserverList()) {
			this.sortedObservedEvents.put(obs.getData().getName(), new ArrayList<ObservedEvent>());
			this.latestObserverResults.put(obs.getData().getName(), new ArrayList<String>());
			this.intermediateStats.put(obs.getData().getName(), new HashMap<String,String>());			
		}
	}
	
	/**
	 * First, remove old values from hash maps. Needed, in order separate the old values from the new ones.
	 * Then, init them again:
	 * 
	 * Prepare "buckets" for each observer to store the results there
	 * 
	 * @param simConf
	 */
	public void reInitSomeHashMaps() {
		
		this.latestObserverResults = new HashMap<String, ArrayList<String>>();
		this.intermediateStats = new HashMap<String, HashMap<String,String>>();		
		
		//--- do it for both lists: containing all observed events and for the list containing the results from the latest experiment
		for (Observer obs : simConf.getObserverList()) {			
			this.latestObserverResults.put(obs.getData().getName(), new ArrayList<String>());
			this.intermediateStats.put(obs.getData().getName(), new HashMap<String,String>());			
		}
	}
}
