package sodekovs.investigation.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sodekovs.investigation.helper.Constants;
import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

public class EvaluateRow {

	/**
	 * Transform the HashMap of observedEvents of all Experiments of this Row into a new structure, that prepares the evaluation of the results: The idea of the structure is roughly:
	 * ObjectInstance.Property.List_of_Lists --> For each ObjectInstance, observed in all experiments, a corresponding HashMap is created, that contains all observed properties of this object. This
	 * HashMap again, contains a List of List, which means it contains a list that holds a list that contains the observed values of the single experiments for this property. By evaluating these
	 * lists, it is possible to calculate mean etc. values for the properties.
	 * 
	 * @param experimentResult
	 */
	public static HashMap<String, HashMap<String, ArrayList<ArrayList<Object>>>> separateData(ArrayList<HashMap<String, HashMap<String, ArrayList<Object>>>> preparedExperimentResList) {

		HashMap<String, HashMap<String, ArrayList<ArrayList<Object>>>> result = new HashMap<String, HashMap<String, ArrayList<ArrayList<Object>>>>();

		for (HashMap<String, HashMap<String, ArrayList<Object>>> objectInstancesMap : preparedExperimentResList) {

			for (Iterator<String> it1 = objectInstancesMap.keySet().iterator(); it1.hasNext();) {
				String objectInstancesMapKey = it1.next();

				// init bucket
				if (result.containsKey(objectInstancesMapKey) == false) {
					result.put(objectInstancesMapKey, new HashMap<String, ArrayList<ArrayList<Object>>>());
				}

				HashMap<String, ArrayList<Object>> objectInstancePropertiesMap = objectInstancesMap.get(objectInstancesMapKey);

				// ArrayList<ArrayList<Object>> propertiesList1 = new ArrayList<ArrayList<Object>>();

				for (Iterator<String> it2 = objectInstancePropertiesMap.keySet().iterator(); it2.hasNext();) {
					String objectInstancePropertiesMapKey = it2.next();

					// init bucket
					if (result.get(objectInstancesMapKey).containsKey(objectInstancePropertiesMapKey) == false) {
						result.get(objectInstancesMapKey).put(objectInstancePropertiesMapKey, new ArrayList<ArrayList<Object>>());
					}

					// create list of list: this is later used to calculate the mean etc. values for this property.
					ArrayList<Object> observedPropertyValueList = objectInstancePropertiesMap.get(objectInstancePropertiesMapKey);
					ArrayList<ArrayList<Object>> list = result.get(objectInstancesMapKey).get(objectInstancePropertiesMapKey);
					list.add(observedPropertyValueList);
					result.get(objectInstancesMapKey).put(objectInstancePropertiesMapKey, list);
				}
				// System.out.println();
			}
		}
		// System.out.println();
		return result;
	}

	/**
	 * Computet statistics for this row, i.e. cumulate the results of the conducted experiments within this row. IMPORTANT: The input (HashMap) has to be sorted before (c.f. method separateData()
	 * above)
	 * 
	 * @param input
	 */
	public static HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> evaluateRowData(HashMap<String, HashMap<String, ArrayList<ArrayList<Object>>>> input) {

		// contains the stats for each object instance: key =object instance id
		HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> statsForObjectInstances = new HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>();

		// get object instances
		for (Iterator<String> it1 = input.keySet().iterator(); it1.hasNext();) {
			String objectInstanceKey = it1.next();
			HashMap<String, ArrayList<ArrayList<Object>>> objectInstanceMap = input.get(objectInstanceKey);

			// contains the stats for the properties of this object instance
			HashMap<String, HashMap<String, ArrayList<String>>> propertyStatsForObjectInstance = new HashMap<String, HashMap<String, ArrayList<String>>>();

			// get list of properties for this object instance
			for (Iterator<String> it2 = objectInstanceMap.keySet().iterator(); it2.hasNext();) {
				String objectInstancePropertiesKey = it2.next();
				// contains for each property a list, which contains again a list of observed vlaues for each experiment run.
				ArrayList<ArrayList<Object>> propertiesList = objectInstanceMap.get(objectInstancePropertiesKey);

//				System.out.println();

				// Key; mean, median etc. values; value: list (every position is a tick)
				propertyStatsForObjectInstance.put(objectInstancePropertiesKey, doStatistics(propertiesList));
			}

			statsForObjectInstances.put(objectInstanceKey, propertyStatsForObjectInstance);
		}
//		System.out.println();
		return statsForObjectInstances;

	}

	/**
	 * Do stats for list of lists that contains the values for each experiment run.
	 * 
	 * @param input
	 * @return returns a Map, that contains ArrayLists for the different observed and computed values for this property. The lists are ordered by time (ascending).
	 */
	private static HashMap<String, ArrayList<String>> doStatistics(ArrayList<ArrayList<Object>> input) {

		HashMap<String, ArrayList<String>> resultsMap = new HashMap<String, ArrayList<String>>();

		// contain the respective values. Pos "X" inside a lists corresponds to tick "x" of a the simulation. The values are cummulated above of values at this tick at all experiments.
		ArrayList<String> meanValueList = new ArrayList<String>();
		ArrayList<String> medianValueList = new ArrayList<String>();
		ArrayList<String> sampleVarianceValueList = new ArrayList<String>();
		ArrayList<String> singleObservedValuesList = new ArrayList<String>();

		// transform lists: get now a list for all values of property "a" at tick x -> the new list is now sorted by ticks, i.e. list "0" has all values of all experiments for this property at this
		// tick.
		ArrayList<ArrayList<Object>> transformedList = new ArrayList<ArrayList<Object>>();

		// init: input.get(x) -> determines the amount of observed values, could be any number here instead of "0"
		for (int i = 0; i < input.get(0).size(); i++) {
			transformedList.add(new ArrayList<Object>());
		}

		for (ArrayList<Object> singleExperiment : input) {
			for (int j = 0; j < singleExperiment.size(); j++) {

				// ToDo: Avoid index out of Bounds Exception -> may happen if one experiment has observed more/less values because of delays at ticks
				// HACK:
				try {
					transformedList.get(j).add(singleExperiment.get(j));
				} catch (IndexOutOfBoundsException e) {
//					System.out.println("\n\n\n\n HELLO ERROR!");
				}
			}
		}

//		System.out.println();
		// do statistics: compute stats for each list
		for (ArrayList<Object> list : transformedList) {

			DoubleArrayList observedValues = new DoubleArrayList();
			String singleValues = "[";

			for (Object value : list) {
				observedValues.add(Double.valueOf((String) value));
				singleValues += value + ";";
			}

			singleValues += "] ";

			// list has to be ordered according to the Colt API
			observedValues.sort();
			double meanValue = Descriptive.mean(observedValues);
			double medianValue = Descriptive.median(observedValues);
			double sampleVarianceValue = Descriptive.sampleVariance(observedValues, meanValue);

			meanValueList.add(String.valueOf(meanValue));
			medianValueList.add(String.valueOf(medianValue));
			sampleVarianceValueList.add(String.valueOf(sampleVarianceValue));
			singleObservedValuesList.add(singleValues);

//			System.out.println("Eval : " + meanValue + " , " + medianValue + ", " + sampleVarianceValue + ", single val: " + singleValues);
		}

		resultsMap.put(Constants.MEAN_VALUE_LIST, meanValueList);
		resultsMap.put(Constants.MEDIAN_VALUE_LIST, medianValueList);
		resultsMap.put(Constants.SAMPLE_VARIANCE_VALUE_LIST, sampleVarianceValueList);
		resultsMap.put(Constants.SINGLE_OBSERVED_VALUES_LIST, singleObservedValuesList);

		return resultsMap;
	}
}
