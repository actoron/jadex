package jadex.simulation.helper.bikesharing;

import jadex.simulation.evaluation.bikesharing.EvalStockLevel;
import jadex.simulation.evaluation.bikesharing.EvalStockLevelData;
import jadex.simulation.evaluation.bikesharing.xml.EvaluatedBikeStationCumulatedData;
import jadex.simulation.evaluation.bikesharing.xml.EvaluatedBikeStationShort;
import jadex.simulation.evaluation.bikesharing.xml.EvaluatedBikeStationShortList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Due to the limits of Jadex this class computes the average values of multiple simulation runs
 * 
 * @author Vilenica
 * 
 */

public class PostEvaluation {

	// the folder with the single results
	private static String folderPath = "E:\\Workspaces\\SodekoVS-SVN\\Bikesharing\\eval\\";

	// accumulated res for the stock level eval
	private static EvalStockLevel evaluatedStockLevel = new EvalStockLevel();

	// accumulated res for the single stations eval
	private static ArrayList<EvaluatedBikeStationShortList> evaluatedBikeStationShortList = new ArrayList<EvaluatedBikeStationShortList>();
					

	private static XStream xstream = new XStream(new StaxDriver());

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// get all file names in this folder
		ArrayList<String> allFileNames = getAllFileNames();

		// 1a.) Compute the average for the stock levels
		try {
			computeAverageStockLevels(allFileNames);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 1b.)Print results for accumulated stock level eval.
		// System.out.println(stockLevelResultsToString());

		// 1c.) Persists result in file on disk as XML file
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "CUMULATED-StockLevelEval" + "-" + getDateAsString() + ".xml"));
			XStream xstream = new XStream(new StaxDriver());
			// add to arbitrary time slice experimentDescription
			ArrayList<EvalStockLevelData> res = evaluatedStockLevel.resultsAsList();
			res.get(0).setExperimentDescription("Contains the CUMULATED RES FROM MANY SINGLE RUNS");
			out.write(xstream.toXML(res));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// *************************************************************************************************

		// 2a.)Compute the average for the single stations
		try {
			computeAverageBikestationResults(allFileNames);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 2b.)Print results for the average for the single stations.
		System.out.println(averageBikestationsResultsToString());

		// 2c.) Persists result in file on disk as XML file
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "CUMULATED-BikeStationResults" + "-" + getDateAsString()  +  ".xml"));
			XStream xstream = new XStream(new StaxDriver());
			// add to arbitrary time slice experimentDescription
			// ArrayList<EvaluatedBikeStationShortList> res = bikeSharEval.bikestationResultsForXMLPersist();
			evaluatedBikeStationShortList.get(0).setExperimentDescription("Contains the CUMULATED RES FROM MANY SINGLE RUNS");
			out.write(xstream.toXML(evaluatedBikeStationShortList));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	

	}

	private static void computeAverageStockLevels(ArrayList<String> allFileNames) throws FileNotFoundException {
		ArrayList<ArrayList<EvalStockLevelData>> singleRes = new ArrayList<ArrayList<EvalStockLevelData>>();

		// Parse all files that contain the stock level results
		for (String fileName : allFileNames) {
			if (fileName.indexOf("StockLevel") != -1) {
				singleRes.add((ArrayList<EvalStockLevelData>) xstream.fromXML(new FileReader(folderPath + fileName)));
			}
		}
		System.out.println(singleRes.size());

		// Compute new stock levels for each time slice
		for (int i = 0; i <= 1380; i += 60) {
			EvalStockLevelData data = new EvalStockLevelData();
			data.setTimeSliceKey(i);

			// get the single eval data for one experiment
			for (ArrayList<EvalStockLevelData> singleResList : singleRes) {

				// get the right results from one result for one time slice
				for (EvalStockLevelData singleData : singleResList) {

					if (singleData.getTimeSliceKey() == i) {

						data.setGreenLevelAbsolute(data.getGreenLevelAbsolute() + singleData.getGreenLevelAbsolute());
						data.setBlueLevelAbsolute(data.getBlueLevelAbsolute() + singleData.getBlueLevelAbsolute());
						data.setRedLevelAbsolute(data.getRedLevelAbsolute() + singleData.getRedLevelAbsolute());
						break;
					}

				}

			}
			data.computeRelativeValues();
			evaluatedStockLevel.setStockLevelData(data.getTimeSliceKey(), data);
			// / System.out.println("Key eval finished: " + data.getTimeSliceKey());
		}
	}

	private static void computeAverageBikestationResults(ArrayList<String> allFileNames) throws FileNotFoundException {
		ArrayList<ArrayList<EvaluatedBikeStationShortList>> singleRes = new ArrayList<ArrayList<EvaluatedBikeStationShortList>>();

		// Parse all files that contain the stock level results
		for (String fileName : allFileNames) {
			if (fileName.indexOf("BikeShareEval-BikeStationResults") != -1) {
				singleRes.add((ArrayList<EvaluatedBikeStationShortList>) xstream.fromXML(new FileReader(folderPath + fileName)));
			}
		}
		System.out.println(singleRes.size());

		// Compute new stock levels for each time slice
		for (int i = 0; i <= 1380; i += 60) {
			HashMap<String, EvaluatedBikeStationCumulatedData> accumulatedStationsMap = new HashMap<String, EvaluatedBikeStationCumulatedData>();
			EvaluatedBikeStationShortList res = new EvaluatedBikeStationShortList();
			res.setTimeSliceKey(i);

			// get the single eval data for one experiment
			for (ArrayList<EvaluatedBikeStationShortList> singleResList : singleRes) {

				// get the right results from one result for one time slice
				for (EvaluatedBikeStationShortList singleData : singleResList) {

					if (singleData.getTimeSliceKey() == i) {

						// compute average for all stations for this time slice
						for (EvaluatedBikeStationShort station : singleData.getStationDataShort()) {

							if (accumulatedStationsMap.get(station.getStationId()) == null) {
								EvaluatedBikeStationCumulatedData accumulatedStation = new EvaluatedBikeStationCumulatedData(station.getStationId(), station.getSimulatedData_MeanValue(),
										station.getRealData_MeanValue());
								accumulatedStationsMap.put(station.getStationId(), accumulatedStation);
							} else {
								EvaluatedBikeStationCumulatedData accumulatedStation = accumulatedStationsMap.get(station.getStationId());
								accumulatedStation.addSimulatedData_MeanValue(station.getSimulatedData_MeanValue());
								accumulatedStationsMap.put(station.getStationId(), accumulatedStation);
							}
						}
						break;
					}

				}
			}

			// do eval on accumulated results for this time slice
			ArrayList<EvaluatedBikeStationShort> finalList = new ArrayList<EvaluatedBikeStationShort>();
			for (Iterator<String> stationId = accumulatedStationsMap.keySet().iterator(); stationId.hasNext();) {
				EvaluatedBikeStationCumulatedData station = accumulatedStationsMap.get(stationId.next());
				station.evaluate();

				// transform object
				EvaluatedBikeStationShort bikestationShort = new EvaluatedBikeStationShort(station.getStationId(), station.getRealDataVsSimulated_Deviation(),
						station.getRealDataVsSimulated_StandardDeviation(), station.getSimulatedData_MeanValue(), station.getRealData_MeanValue());
				finalList.add(bikestationShort);
			}

			res.setStationDataShort(finalList);
			evaluatedBikeStationShortList.add(res);
		}
	}

	private static String stockLevelResultsToString() {
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

	private static String averageBikestationsResultsToString() {
		StringBuffer result = new StringBuffer();
		// Contains the ids of the stations that could not be evaluated because of some error.
		ArrayList<String> faultStations = new ArrayList<String>();

		result.append("Results of the evalation of the single bikestations :\n");
		result.append("\n The evaluation is separated by time slice.\n");
		result.append("\n**********************************************************************");
		result.append("\n**********************************************************************\n");

		// iterate through time slices
		for (EvaluatedBikeStationShortList timeSliceResults : evaluatedBikeStationShortList) {
			

			result.append("\n");
			result.append("TIME SLICE: ");
			result.append("\t");
			result.append(timeSliceResults.getTimeSliceKey());
			result.append("\n");
			result.append("\n########################################################################\n");

			// iterate through bikestations
			for (EvaluatedBikeStationShort station : timeSliceResults.getStationDataShort()) {				
				try {
					result.append(station.resultsToString() + "\n");
				} catch (Exception e) {
					// Happens, if station is not found in Simulation Data AND Real Data. Then evaluation fails in "EvaluatedBikeStation.compareSimulationVsReality()" and corresponding String is
					// empty!
					// System.out.println("Exception@BikeSharingEvaluation.bikestationResultsToString()# Station not found in real data: " + objectInstancesKey);
					boolean containsStationAlready = false;
					for (String stationID : faultStations) {
						if (stationID.equals(station.getStationId())) {
							containsStationAlready = true;
							break;
						}
					}
					if (!containsStationAlready) {
						faultStations.add(station.getStationId());
					}
				}
			}
			result.append("\n########################################################################\n");
		}

		if (faultStations.size() > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("Exception@BikeSharingEvaluation.bikestationResultsToString()# Station not found in real data: \n");
			for (String station : faultStations) {
				buf.append(station + "\n");
			}
			System.out.println(buf.toString());
		}

		return result.toString();
	}

	/**
	 * 
	 * @return
	 */
	private static ArrayList<String> getAllFileNames() {
		ArrayList<String> results = new ArrayList<String>();
		File[] files = new File(folderPath).listFiles();

		for (File file : files) {
			if (file.isFile()) {
				results.add(file.getName());
				System.out.println(file.getName());
			}
		}

		return results;
	}

	private static String getDateAsString() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date(System.currentTimeMillis()));
		String date = String.valueOf(cal.get(Calendar.DATE)) + "-";
		date += String.valueOf(cal.get(Calendar.MONTH) + 1) + "-";
		date += String.valueOf(cal.get(Calendar.YEAR)) + "--";
		date += String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + "-";
		date += String.valueOf(cal.get(Calendar.MINUTE)) + "-";
		date += String.valueOf(cal.get(Calendar.SECOND));

		return date;
	}

}
