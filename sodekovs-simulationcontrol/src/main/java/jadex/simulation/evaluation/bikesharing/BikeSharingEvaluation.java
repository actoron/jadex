package jadex.simulation.evaluation.bikesharing;

import jadex.simulation.helper.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import sodekovs.util.bikesharing.model.SimulationDescription;
import sodekovs.util.bikesharing.model.Station;
import sodekovs.util.bikesharing.model.TimeSlice;
import sodekovs.util.misc.XMLHandler;

/**
 * Special class for evaluating the bikesharing application. Can be used to compare the simulation results with the real data results retrieved from the live system.
 * 
 * @author Vilenica
 * 
 */
public class BikeSharingEvaluation {

	// private String realDataXMLFile = "E:\\Workspaces\\Jadex\\BikeSharing2\\Jadex\\sodekovs-applications\\src\\main\\java\\sodekovs\\bikesharing\\setting\\WashingtonEvaluation_Monday_new.xml";
	private String realDataXMLFile = "C:\\Users\\Thomas\\workspaces\\jadex-sodekovs\\trunk\\sodekovs-applications\\src\\main\\java\\sodekovs\\bikesharing\\setting\\WashingtonEvaluation_Monday_new.xml";
	// private String realDataXMLFile = "E:/Workspaces/Jadex/BikeSharing2/Jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/WashingtonEvaluation_Monday_new.xml";
	private SimulationDescription realData;
	// conf. following method for understanding the data structure:
	// EvaluateRow.evaluateRowData(preparedRowData)
	private HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> simulationData;
	// contains the evaluation of the stock level, sorted by time slice
	private EvalStockLevel evaluatedStockLevel = new EvalStockLevel();
	// contains the evaluation of the single bike stations sorted by time slice
	private HashMap<Integer, HashMap<String, EvaluatedBikeStation>> timeSlicesBikeStationMap = new HashMap<Integer, HashMap<String, EvaluatedBikeStation>>();

	public BikeSharingEvaluation(HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> simulationData) {
		this.simulationData = simulationData;
	}

	public void compare() {

		realData = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(realDataXMLFile, SimulationDescription.class);

		// 1.Compute tick size, e.g. the "length" of the observed simulation
		int tickSize = computeTickSize();

		// 2.Transform simData into new data structure --> contains the values of each object instances sorted by tick
		HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> transformedSimDataMap = transformSimData(tickSize);

		// put the single ticks into buckets according to the time slices
		HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>>> timeSlicesSimDataMap = transformToTimeSlices(transformedSimDataMap);

		// create new HashMapp: for every timeslice a map of all bikestation that are also evaluated
		timeSlicesBikeStationMap = statsForBikeStations(timeSlicesSimDataMap);

		// 3.Compare Simulation Results with real data
		compareResults(timeSlicesBikeStationMap);

		// 4. Evaluate the stock level
		evalStockLevel(timeSlicesBikeStationMap);

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
					result3.put(Constants.SINGLE_OBSERVED_VALUES_LIST, observedPropertyValues.get(Constants.SINGLE_OBSERVED_VALUES_LIST).get(i));

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

	/**
	 * Compare the sim data with real data: compute the difference with respect to the property "stock"
	 * 
	 * @param transformedSimDataMap
	 * @return
	 */
	private void compareResults(HashMap<Integer, HashMap<String, EvaluatedBikeStation>> simulatedDataEval) {

		for (TimeSlice tSlice : realData.getTimeSlices().getTimeSlice()) {
			long startTime = tSlice.getStartTime();

			for (Station station : tSlice.getStations().getStation()) {

				// check, whether there are results for this tick
				// check also, whether there are simulation results for this station
				if (simulatedDataEval.get((int) startTime) != null && simulatedDataEval.get((int) startTime).get(station.getStationID()) != null) {

					EvaluatedBikeStation evalStation = simulatedDataEval.get((int) startTime).get(station.getStationID());
					EvaluatedBikeStationData realData = new EvaluatedBikeStationData();
					// handles the data from the real systems as mean value!!!
					realData.setMeanValue(new Double(station.getNumberOfBikes()));
					evalStation.setRealData(realData);
					evalStation.compareSimulationVsReality();
				}
			}
		}
	}

	/**
	 * Since now, the results are sorted by each tick. Now, the ticks are put into buckets according to the time slices: Tick 1-60 into TimeSlice60, Tick 61-120 into Tick 120 and so on. Therefore,
	 * each bucket value represents the average value of the single observed values
	 * 
	 * @param transformedSimDataMap
	 * @return
	 */
	private HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>>> transformToTimeSlices(
			HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, String>>>> transformedSimDataMap) {

		HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>>> resultMap = new HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>>>();

		// 1)Transform data. From single values for each tick to buckets containing the values for a timeSlice
		for (int i = 0; realData.getTimeSlices().getTimeSlice().size() > i; i++) {
			TimeSlice tSlice = realData.getTimeSlices().getTimeSlice().get(i);

			int currentTickBucket = 0;

			for (Iterator<Integer> tickIt = transformedSimDataMap.keySet().iterator(); tickIt.hasNext();) {
				currentTickBucket = tickIt.next();

				// check, if it belongs still to this bucket
				if ((i + 1 < realData.getTimeSlices().getTimeSlice().size() && ((tSlice.getStartTime() <= currentTickBucket) && realData.getTimeSlices().getTimeSlice().get(i + 1).getStartTime() > Long
						.valueOf(currentTickBucket))) || (i + 1 == realData.getTimeSlices().getTimeSlice().size() && tSlice.getStartTime() <= currentTickBucket)) {

					HashMap<String, HashMap<String, HashMap<String, String>>> stationsMap = transformedSimDataMap.get(currentTickBucket);

					// station instance iterator
					for (Iterator<String> stationInstanceIt = stationsMap.keySet().iterator(); stationInstanceIt.hasNext();) {
						String currentStation = stationInstanceIt.next();
						HashMap<String, HashMap<String, String>> stationInstancePropertiesMap = stationsMap.get(currentStation);

						// check, if hashMaps have been already initialized
						if (resultMap.get((int) tSlice.getStartTime()) == null) {
							resultMap.put((int) tSlice.getStartTime(), new HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>>());
						}

						if (resultMap.get((int) tSlice.getStartTime()).get(currentStation) == null) {
							resultMap.get((int) tSlice.getStartTime()).put(currentStation, new HashMap<String, HashMap<String, ArrayList<Long>>>());
						}

						if (resultMap.get((int) tSlice.getStartTime()).get(currentStation).get("stock") == null) {
							resultMap.get((int) tSlice.getStartTime()).get(currentStation).put("stock", new HashMap<String, ArrayList<Long>>());
						}

						if (resultMap.get((int) tSlice.getStartTime()).get(currentStation).get("stock").get(Constants.SINGLE_OBSERVED_VALUES_LIST) == null) {
							resultMap.get((int) tSlice.getStartTime()).get(currentStation).get("stock").put(Constants.SINGLE_OBSERVED_VALUES_LIST, new ArrayList<Long>());
						}

						// Get single values from simulated data. they are stored as a string, separated by ";" and brackets and the beginning/end
						String singleValues = stationInstancePropertiesMap.get("stock").get(Constants.SINGLE_OBSERVED_VALUES_LIST);
						// Get List of already transformed data for this station: This this will contain all single observed data for the stock level for this station at all experiments and the whole
						// time slice
						ArrayList<Long> singleValueList = resultMap.get((int) tSlice.getStartTime()).get(currentStation).get("stock").get(Constants.SINGLE_OBSERVED_VALUES_LIST);

						// System.out.println(singleValues);
						// Delete brackets from string at beginning and end
						singleValues = singleValues.substring(1, singleValues.length() - 2);
						// System.out.println(singleValues);

						while (singleValues.contains(";")) {
							long val = Long.valueOf(singleValues.substring(0, singleValues.indexOf(";")));
							singleValueList.add(val);
							singleValues = singleValues.substring(singleValues.indexOf(";") + 1);
							// System.out.println("New string: " + singleValues + "\n" + "val:" + val);
						}

						resultMap.get((int) tSlice.getStartTime()).get(currentStation).get("stock").put(Constants.SINGLE_OBSERVED_VALUES_LIST, singleValueList);
					}
				}
			}
		}
//		System.out.println("#BikeSharingEval# Nr. of Eval Time Slices: " + resultMap.size());

		return resultMap;
	}

	/**
	 * Compute the stats for the simulated data w.r.t to the Bike Stations. Result: a Hashmap that contains for each time slice a HashMap that contains for each bike station a evaluation.
	 * 
	 * @param transformedSimDataMap
	 * @return
	 */
	private HashMap<Integer, HashMap<String, EvaluatedBikeStation>> statsForBikeStations(HashMap<Integer, HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>>> timeSlicesDataMap) {

		// Key: Time Slice: contains for every time slice a Hash Map that contains a EvalObject for Each Station
		HashMap<Integer, HashMap<String, EvaluatedBikeStation>> resultMap = new HashMap<Integer, HashMap<String, EvaluatedBikeStation>>();

		// Iterate through all Time Slices
		for (Iterator<Integer> tickIt = timeSlicesDataMap.keySet().iterator(); tickIt.hasNext();) {
			HashMap<String, EvaluatedBikeStation> timeSliceMap = new HashMap<String, EvaluatedBikeStation>();

			int currentTimeSlice = tickIt.next();
			HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>> stationInstancesMap = timeSlicesDataMap.get(currentTimeSlice);

			// Iterate through all Bike Stations in this time slice
			for (Iterator<String> stationInstancesMapIt = stationInstancesMap.keySet().iterator(); stationInstancesMapIt.hasNext();) {
				String stationId = stationInstancesMapIt.next();
				HashMap<String, HashMap<String, ArrayList<Long>>> stationPropertiesMap = stationInstancesMap.get(stationId);

				// create new Data Object that will contain the evaluated results
				EvaluatedBikeStation station = new EvaluatedBikeStation(stationId);
				EvaluatedBikeStationData simData = new EvaluatedBikeStationData();
				simData.setSingleValues(stationPropertiesMap.get("stock").get(Constants.SINGLE_OBSERVED_VALUES_LIST));
				station.setSimulatedData(simData);
				station.evalSimulatedData();

				timeSliceMap.put(station.getStationId(), station);
			}

			resultMap.put(currentTimeSlice, timeSliceMap);
		}
		return resultMap;
	}

	// Compute the stock level (of the simulated scenario) of the bike stations using three buckets:
	// 1.) stock < 1
	// 2.) stock > 0 && stock < capacity
	// 3.) stock >= capacity
	private void evalStockLevel(HashMap<Integer, HashMap<String, EvaluatedBikeStation>> timeSlicesBikeStationMap) {
		SimulationDescription realData = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(realDataXMLFile, SimulationDescription.class);

		// Iterate through all Time Slices
		for (Iterator<Integer> tickIt = timeSlicesBikeStationMap.keySet().iterator(); tickIt.hasNext();) {

			int currentTimeSlice = tickIt.next();
			HashMap<String, EvaluatedBikeStation> evaluatedStationsMap = timeSlicesBikeStationMap.get(currentTimeSlice);

			EvalStockLevelData evalStockLevelData = new EvalStockLevelData();
			int red = 0;
			int blue = 0;
			int green = 0;

			// Iterate through all Bike Stations in this time slice
			for (Iterator<String> stationInstancesMapIt = evaluatedStationsMap.keySet().iterator(); stationInstancesMapIt.hasNext();) {
				String stationId = stationInstancesMapIt.next();
				EvaluatedBikeStation evaluatedBikeStation = evaluatedStationsMap.get(stationId);

				if (evaluatedBikeStation.getSimulatedData().getMeanValue() < 1.0) {
					red++;
				} else if (evaluatedBikeStation.getSimulatedData().getMeanValue() >= getStockNrOfDocks(realData, stationId)) {
					blue++;
				} else {
					green++;
				}
			}
			evalStockLevelData.setBlueLevelAbsolute(blue);
			evalStockLevelData.setRedLevelAbsolute(red);
			evalStockLevelData.setGreenLevelAbsolute(green);
			evalStockLevelData.computeRelativeValues();

			evaluatedStockLevel.setStockLevelData(currentTimeSlice, evalStockLevelData);
		}
	}	

	/**
	 * Retrieve number of docks for a station
	 * 
	 * @param realData
	 * @param stationId
	 * @return
	 */
	private int getStockNrOfDocks(SimulationDescription realData, String stationId) {
		// Take arbitrary time slice to get number of docks for this station

		for (Station station : realData.getTimeSlices().getTimeSlice().get(0).getStations().getStation()) {
			if (station.getStationID().equals(stationId))
				return station.getNumberOfDocks();
		}
		return -1;
	}

	public String stockLevelResultsToString() {
		StringBuffer result = new StringBuffer();

		result.append("\nResults of the evalation of the stock levels from the simulated data:\n");
		result.append("\n The evaluation of the stock levels contains for each time slice following three buckets:");
		result.append("\n stock < 1 --> \"red\"");
		result.append("\n stock > 0 && stock < capacity  --> \"green\"");
		result.append("\n stock >= capacity --> \"blue\"");
		result.append("\n");
		result.append("\n**********************************************************************");
		result.append("\n**********************************************************************\n");
		result.append(evaluatedStockLevel.resultsToString());
		result.append("\n**********************************************************************");
		result.append("\n**********************************************************************\n");

		return result.toString();
	}

	public String bikestationResultsToString() {
		StringBuffer result = new StringBuffer();

		result.append("Results of the evalation of the single bikestations :\n");
		result.append("\n The evaluation is separated by time slice.\n");
		result.append("\n**********************************************************************");
		result.append("\n**********************************************************************\n");
		// sort by time slice
		SortedSet<Integer> timeSliceKeys = new TreeSet<Integer>(timeSlicesBikeStationMap.keySet());

		// iterate through time slices
		for (Integer timeSliceKey : timeSliceKeys) {
			HashMap<String, EvaluatedBikeStation> timeSlicesMap = timeSlicesBikeStationMap.get(timeSliceKey);

			result.append("\n");
			result.append("TIME SLICE: ");
			result.append("\t");
			result.append(timeSliceKey);
			result.append("\n");
			result.append("\n########################################################################\n");

			// iterate through bikestations
			for (Iterator<String> it2 = timeSlicesMap.keySet().iterator(); it2.hasNext();) {
				String objectInstancesKey = it2.next();
				EvaluatedBikeStation evalutedBikeStation = timeSlicesMap.get(objectInstancesKey);
				result.append(evalutedBikeStation.resultsToString() + "\n");
			}
			result.append("\n########################################################################\n");
		}

		return result.toString();
	}
}
