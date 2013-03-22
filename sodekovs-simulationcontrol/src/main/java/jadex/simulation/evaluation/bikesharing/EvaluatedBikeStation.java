package jadex.simulation.evaluation.bikesharing;

import java.util.SortedSet;
import java.util.TreeSet;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

/**
 * Contains the evaluated/cumulated Data for a bike station for one timeslice. Contains object with a)simualated data b)real data -> from database c)Compared data: Uses the mean value from the
 * "real data" and computes the (standard) deviation of the simulated data
 * 
 * @author Vilenica
 * 
 */
public class EvaluatedBikeStation {

	private String stationId;
	private EvaluatedBikeStationData realData;
	private EvaluatedBikeStationData simulatedData;
	private EvaluatedBikeStationData comparedData;

	public EvaluatedBikeStation(String stationId) {
		this.stationId = stationId;
	}

	/**
	 * Precondition: Has to contain the single observed values within the data object "simulatedData".
	 */
	public void evalSimulatedData() {

		DoubleArrayList values = new DoubleArrayList();
		for (Long simData : simulatedData.getSingleValues()) {
			values.add(new Double(simData));
		}

		// list has to be ordered according to the Colt API
		values.sort();

		simulatedData.setMeanValue(Descriptive.mean(values));
		simulatedData.setMedianValue(Descriptive.median(values));
		simulatedData.setDevation(Descriptive.sampleVariance(values, simulatedData.getMeanValue()));
		simulatedData.setStandardDevation(Descriptive.sampleStandardDeviation(values.size(), simulatedData.getDevation()));

		// System.out.println("Eval : " + meanValue + " , " + medianValue + ", " + sampleVarianceValue + ", single val: " + sampleStandardVarianceValue);

	}

	/**
	 * Precondition: Has to contain the single observed values within the data object "simulatedData" AND "realData!!!! !!!! Use the value observed in reality as mean-value and use the simData to
	 * compute (standard) deviation w.r.t to this mean value!!!!
	 */
	public void compareSimulationVsReality() {
		comparedData = new EvaluatedBikeStationData();

		// !!!! Use the value observed in reality as mean-value and use the simData to compute (standard) deviation w.r.t to this mean value!!!

		DoubleArrayList values = new DoubleArrayList();
		for (Long simData : simulatedData.getSingleValues()) {
			values.add(new Double(simData));
		}

		// list has to be ordered according to the Colt API
		values.sort();

		comparedData.setMeanValue(realData.getMeanValue());
		comparedData.setDevation(Descriptive.sampleVariance(values, comparedData.getMeanValue()));
		comparedData.setStandardDevation(Descriptive.sampleStandardDeviation(values.size(), comparedData.getDevation()));

		// System.out.println("Eval : " + meanValue + " , " + medianValue + ", " + sampleVarianceValue + ", single val: " + sampleStandardVarianceValue);

	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public EvaluatedBikeStationData getRealData() {
		return realData;
	}

	public void setRealData(EvaluatedBikeStationData realData) {
		this.realData = realData;
	}

	public EvaluatedBikeStationData getSimulatedData() {
		return simulatedData;
	}

	public void setSimulatedData(EvaluatedBikeStationData simulatedData) {
		this.simulatedData = simulatedData;
	}

	public String resultsToString() {
		StringBuffer result = new StringBuffer();

		result.append("StationId: " + stationId + "\n");
		result.append("Real Data vs. Simulated: Deviation: " + comparedData.getDevation() + "\t Standard Deviation: " + +comparedData.getStandardDevation() + "\n");
		result.append("Simulated Data: Mean Value" + simulatedData.getMeanValue() + "\n");

		return result.toString();
	}
}
