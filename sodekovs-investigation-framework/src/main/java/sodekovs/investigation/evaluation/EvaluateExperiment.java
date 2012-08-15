package sodekovs.investigation.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import sodekovs.investigation.model.ObservedEvent;
import sodekovs.investigation.model.result.ExperimentResult;

public class EvaluateExperiment {

	/**
	 * Transform the List of ObservedData of this Experiment into a HashMap. The HashMap key is the name (id) of the object instance. The value contains again a HashMap. The key, of this second HashMap, is
	 * the observed property of the object instance. The corresponding value is a ArrayList which contains the observed values, sorted ascendingly by time.
	 * 
	 * @param experimentResult
	 */
	public static HashMap<String, HashMap<String, ArrayList<Object>>> separateData(ExperimentResult experimentResult) {

		HashMap<String, HashMap<String, ArrayList<Object>>> observedProppertiesBucket = new HashMap<String, HashMap<String, ArrayList<Object>>>();
		
		HashMap<String, ArrayList<ObservedEvent>> instancesDataBucket = new HashMap<String, ArrayList<ObservedEvent>>();
		// Create Buckets for each observed instance, i.e. sort the ObservedEvents of this Experiment by their "DataName" which corresponds to the property ""multipleInstanceId" of the
		// configuration.xml
		for (ObservedEvent event : experimentResult.getEvents()) {
			ArrayList<ObservedEvent> eventList;
			if (instancesDataBucket.get(event.getDataName()) == null) {
				eventList = new ArrayList<ObservedEvent>();
			} else {
				eventList = (ArrayList<ObservedEvent>) instancesDataBucket.get(event.getDataName());
			}
			eventList.add(event);
			instancesDataBucket.put(event.getDataName(), eventList);
		}

		

		for (Iterator<String> it = instancesDataBucket.keySet().iterator(); it.hasNext();) {
			String instanceKey = it.next();
			
			// Get all observed events for this instance object
			ArrayList<ObservedEvent> instanceEventList = instancesDataBucket.get(instanceKey);

			//Do a HashMap, only for this instance object, that separates the observed properties of this object into buckets.
			HashMap<String, ArrayList<Object>> observedProppertiesForInstanceBucket = new HashMap<String, ArrayList<Object>>();
			// sort list ascendingly by relative_timestamp
			instanceEventList = sort(instanceEventList);

			for (ObservedEvent event : instanceEventList) {
				HashMap<String, Object> observedEventProperties = event.getObservedObjectProperties();
				ArrayList<Object> observedEventPropertiesList;

				for (Iterator<String> it1 = observedEventProperties.keySet().iterator(); it1.hasNext();) {
					String key = it1.next();
					if (observedProppertiesForInstanceBucket.get(key) == null) {
						observedEventPropertiesList = new ArrayList<Object>();
					} else {
						observedEventPropertiesList = (ArrayList<Object>) observedProppertiesForInstanceBucket.get(key);
					}
					observedEventPropertiesList.add(observedEventProperties.get(key));
					observedProppertiesForInstanceBucket.put(key, observedEventPropertiesList);
				}
			}
			observedProppertiesBucket.put(instanceKey, observedProppertiesForInstanceBucket);
		}

		return observedProppertiesBucket;
//		for (Iterator<String> it2 = observedProppertiesBucket.keySet().iterator(); it2.hasNext();) {
//			System.out.println("Key: " + it2.next());
//		}
	}

	/**
	 * Returns the list of observed events ascendingly ordered by relative_timestamp.
	 */
	public static ArrayList<ObservedEvent> sort(ArrayList<ObservedEvent> eventList) {
		Collections.sort(eventList, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return new Long(((ObservedEvent) arg0).getRelativeTimestamp()).compareTo(new Long(((ObservedEvent) arg1).getRelativeTimestamp()));
			}
		});
		return eventList;
	}
}
