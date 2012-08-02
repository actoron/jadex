package jadex.simulation.evaluation;

import jadex.simulation.helper.Constants;
import jadex.simulation.model.result.RowResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Special class for evaluating the bikesharing application. Can be used to compare the simulation results with the real data results retrieved from the live system.
 * 
 * @author Vilenica
 * 
 */
public class BikeSharingEvaluation {

	private String dealDataXMLFile = "E:/Workspaces/Jadex/Jadex mit altem Maven/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/WashingtonSimulation_Monday_100.xml";
	// conf. following method for understanding the data structure: EvaluateRow.evaluateRowData(preparedRowData)
	private HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> simulationData;
	
	

	public BikeSharingEvaluation(HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> simulationData) {
		this.simulationData = simulationData;
	}

	public void compare() {

		// 1.Compute tick siez, e.g. the "length" of the observed simulation
		int tickSize = computeTickSize();

		// 2.Transform simData into new data structure --> contains the values of each object instances sorted by tick
		HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> transformedSimDataMap = transformSimData(tickSize);
		
		//3.Compare Simulation Results with real data
		compareResults(transformedSimDataMap);

	}

	private HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>>  transformSimData(int tickSize) {
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

					// retrieve observed value for this property at this tick "i"
					result3.put(Constants.MEAN_VALUE_LIST, observedPropertyValues.get(Constants.MEAN_VALUE_LIST).get(i));
					result3.put(Constants.MEDIAN_VALUE_LIST, observedPropertyValues.get(Constants.MEDIAN_VALUE_LIST).get(i));
					result3.put(Constants.SAMPLE_VARIANCE_VALUE_LIST, observedPropertyValues.get(Constants.SAMPLE_VARIANCE_VALUE_LIST).get(i));

					// put results for this single property into the map of all properties of this object instance
					result2.put(observedPropertyValuesKey, result3);
				}

				// put results for this single object instance into the map of all object instances at this tick
				result1.put(stationInstanceKey, result2);
			}

			// put all observed values for all object instances at this tick
			resultMap.put(i, result1);
		}
		
		return resultMap;

	}

	// take a arbitrary station with a arbitrary property with a arbitrary observed value to get the tick size, e.g. how many steps have been observed
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
	
	private void compareResults(HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> transformedSimDataMap){
		
	}
}
