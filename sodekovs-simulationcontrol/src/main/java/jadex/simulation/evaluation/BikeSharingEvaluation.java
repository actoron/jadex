package jadex.simulation.evaluation;

import jadex.simulation.helper.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sodekovs.bikesharing.model.SimulationDescription;
import sodekovs.bikesharing.model.Station;
import sodekovs.bikesharing.model.TimeSlice;
import sodekovs.util.misc.XMLHandler;

/**
 * Special class for evaluating the bikesharing application. Can be used to compare the simulation results with the real data results retrieved from the live system.
 * 
 * @author Vilenica
 * 
 */
public class BikeSharingEvaluation {

	private String dealDataXMLFile = "E:\\Workspaces\\Jadex\\BikeSharing\\MyProject\\sodekovs-applications\\src\\main\\java\\sodekovs\\bikesharing\\setting\\WashingtonEvaluation_Monday.xml";
	// conf. following method for understanding the data structure:
	// EvaluateRow.evaluateRowData(preparedRowData)
	private HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> simulationData;
	//contains the final results of the evaluation
	private HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> finalResultsMap = new HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>>();

	public BikeSharingEvaluation(HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> simulationData) {
		this.simulationData = simulationData;
	}

	public void compare() {

		// 1.Compute tick siez, e.g. the "length" of the observed simulation
		int tickSize = computeTickSize();

		// 2.Transform simData into new data structure --> contains the values
		// of each object instances sorted by tick
		HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> transformedSimDataMap = transformSimData(tickSize);

		// 3.Compare Simulation Results with real data
		compareResults(transformedSimDataMap);

	}

	private HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> transformSimData(int tickSize) {
		HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> resultMap = new HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>>();

		for (int i = 0; i < tickSize; i++) {

			// result Map for all object instances for this tick
			HashMap<String, HashMap<String, HashMap<String, String>>> result1 = new HashMap<String, HashMap<String, HashMap<String, String>>>();

			// station instance iterator
			for (Iterator<String> stationInstanceIt = simulationData.keySet().iterator(); stationInstanceIt.hasNext();) {

				// result Map for the properties of one object instance
				HashMap<String, HashMap<String, String>> result2 = new HashMap<String, HashMap<String, String>>();

				// station instance properties Map
				String stationInstanceKey = stationInstanceIt.next();
				HashMap<String, HashMap<String, ArrayList<String>>> stationInstancePropertiesMap = simulationData.get(stationInstanceKey);

				// station instance properties iterator
				for (Iterator<String> stationInstancePropertiesIt = stationInstancePropertiesMap.keySet().iterator(); stationInstancePropertiesIt.hasNext();) {

					// result Map for one property
					HashMap<String, String> result3 = new HashMap<String, String>();

					// station instance properties-> observed values Map
					String observedPropertyValuesKey = stationInstancePropertiesIt.next();
					HashMap<String, ArrayList<String>> observedPropertyValues = stationInstancePropertiesMap.get(observedPropertyValuesKey);

					// retrieve observed value for this property at this tick
					// "i"
					result3.put(Constants.MEAN_VALUE, observedPropertyValues.get(Constants.MEAN_VALUE_LIST).get(i));
					result3.put(Constants.MEDIAN_VALUE, observedPropertyValues.get(Constants.MEDIAN_VALUE_LIST).get(i));
					result3.put(Constants.SAMPLE_VARIANCE_VALUE, observedPropertyValues.get(Constants.SAMPLE_VARIANCE_VALUE_LIST).get(i));

					// put results for this single property into the map of all
					// properties of this object instance
					result2.put(observedPropertyValuesKey, result3);
				}

				// put results for this single object instance into the map of
				// all object instances at this tick
				result1.put(stationInstanceKey, result2);
			}

			// put all observed values for all object instances at this tick
			resultMap.put(i, result1);
		}

		return resultMap;

	}

	// take a arbitrary station with a arbitrary property with a arbitrary
	// observed value to get the tick size, e.g. how many steps have been
	// observed
	private int computeTickSize() {

		// arbitrary station instance iterator
		Iterator<String> stationInstanceIt = simulationData.keySet().iterator();
		String randomStationInstanceKey = stationInstanceIt.next();

		// arbitrary station instance properties iterator
		HashMap<String, HashMap<String, ArrayList<String>>> abritrayStationInstance = simulationData.get(randomStationInstanceKey);
		Iterator<String> stationInstancePropertiesIt = abritrayStationInstance.keySet().iterator();
		String randomStationPropertyInstanceKey = stationInstancePropertiesIt.next();

		// arbitrary station instance observed property values iterator
		HashMap<String, ArrayList<String>> abritrayObservedPropertyValues = abritrayStationInstance.get(randomStationPropertyInstanceKey);
		Iterator<String> abritrayObservedPropertyValuesIt = abritrayObservedPropertyValues.keySet().iterator();
		String abritrayObservedPropertyValuesKey = abritrayObservedPropertyValuesIt.next();

		// get tick size
		return abritrayObservedPropertyValues.get(abritrayObservedPropertyValuesKey).size();
	}

	private HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> compareResults(HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> transformedSimDataMap) {
		
		SimulationDescription scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(dealDataXMLFile, SimulationDescription.class);

		for (TimeSlice tSlice : scenario.getTimeSlices().getTimeSlice()) {
			long startTime = tSlice.getStartTime();

			for (Station station : tSlice.getStations().getStation()) {
//				station.getNumberOfBikes();
				// String observedVal =
				// transformedSimDataMap.get((int)startTime).get(station.getStationID()).get("stock").get(Constants.MEAN_VALUE);

				// check, whether there are suimulation results for this station
//				HashMap<String, HashMap<String, String>> stationInstancesMap = transformedSimDataMap.get((int) startTime).get(station.getStationID());
				
				//check , whether there are results for this tick
				//check also, whether there are simulation results for this station
				if (transformedSimDataMap.get((int) startTime) != null && transformedSimDataMap.get((int) startTime).get(station.getStationID()) != null) {
					// HashMap<String, String> prop = statID.get("stock");
					// String val = prop.get(Constants.MEAN_VALUE);
					HashMap<String, HashMap<String, String>> stationInstancesMap = transformedSimDataMap.get((int) startTime).get(station.getStationID());

					// compare
					double difference = station.getNumberOfBikes() / Double.valueOf(stationInstancesMap.get("stock").get(Constants.MEAN_VALUE));

					// compute relative difference
					if (difference <= 1.0) {
						difference = 1.0 - difference;
					} else {
						difference = difference - 1.0;
					}

					// check, if hashMaps have been already initialized
					if (finalResultsMap.get((int) startTime) == null) {
						finalResultsMap.put((int) startTime, new HashMap<String, HashMap<String, HashMap<String, String>>>());
					}

					if (finalResultsMap.get((int) startTime).get(station.getStationID()) == null) {
						finalResultsMap.get((int) startTime).put(station.getStationID(), new HashMap<String, HashMap<String, String>>());
					}

					if (finalResultsMap.get((int) startTime).get(station.getStationID()).get("stock") == null) {
						finalResultsMap.get((int) startTime).get(station.getStationID()).put("stock", new HashMap<String, String>());
					}

					HashMap<String, String> statsForProperty = finalResultsMap.get((int) startTime).get(station.getStationID()).get("stock");

					// put the value, that denotes the difference betwenn the
					// sim data and real data into this HashMap
					statsForProperty.put(Constants.MEAN_VALUE_DIFF_BETWEEN_SIM_AND_REAL_DATA, String.valueOf(difference));

					// add res for this station property to ResultMap
					finalResultsMap.get((int) startTime).get(station.getStationID()).put("stock", statsForProperty);
				}
			}
		}
		return finalResultsMap;
	}
	
	public String resultsToString(){
		StringBuffer result = new StringBuffer();
		
//		ew HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>>();
		
		// get object instances
				for (Iterator<Integer> it1 = finalResultsMap.keySet().iterator(); it1.hasNext();) {
					int timeSliceKey = it1.next();
					HashMap<String, HashMap<String, HashMap<String, String>>> timeSlicesMap = finalResultsMap.get(timeSliceKey);
					
					result.append("TIME SLICE: ");
					result.append("\t");
					result.append(timeSliceKey);
					result.append("\n");
					
					for (Iterator<String> it2 = timeSlicesMap.keySet().iterator(); it2.hasNext();) {
						String objectInstancesKey = it2.next();
						HashMap<String, HashMap<String, String>> objectInstancesMap = timeSlicesMap.get(objectInstancesKey);
						
//						for (Iterator<String> it3 = propertiesMap.keySet().iterator(); it3.hasNext();) {
//							String observedPropertyValuesKey = it3.next();
							HashMap<String, String> observedPropertyValues = objectInstancesMap.get("stock");
							
//							result.append("Station ");
							result.append(objectInstancesKey);
							result.append("\t: ");
							result.append(observedPropertyValues.get(Constants.MEAN_VALUE_DIFF_BETWEEN_SIM_AND_REAL_DATA));
							result.append("\n");
//						}
						
					}
					result.append("***************************************");
				}


		return result.toString();
	}
}
