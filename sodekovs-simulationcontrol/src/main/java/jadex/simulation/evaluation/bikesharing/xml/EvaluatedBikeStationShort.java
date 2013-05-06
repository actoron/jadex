package jadex.simulation.evaluation.bikesharing.xml;


/**
 * Requirred for storage as XML file
 * 
 * @author Vilenica
 * 
 */
public class EvaluatedBikeStationShort {

	private String stationId;
	private double realDataVsSimulated_Deviation;
	private double realDataVsSimulated_StandardDeviation;
	private double simulatedData_MeanValue;
	private double realData_MeanValue;
		

	public EvaluatedBikeStationShort(String stationId, double realDataVsSimulated_Deviation, double realDataVsSimulated_StandardDeviation, double simulatedData_MeanValue,  double realData_MeanValue) {
		this.stationId = stationId;
		this.realDataVsSimulated_Deviation = realDataVsSimulated_Deviation;
		this.realDataVsSimulated_StandardDeviation = realDataVsSimulated_StandardDeviation;
		this.simulatedData_MeanValue = simulatedData_MeanValue;
		this.realData_MeanValue = realData_MeanValue;
	}


	public String getStationId() {
		return stationId;
	}


	public double getSimulatedData_MeanValue() {
		return simulatedData_MeanValue;
	}


	public double getRealData_MeanValue() {
		return realData_MeanValue;
	}
	
	/**
	* IMPORTANT: ONLY TO BE USED BY THE JAVA CLASS THAT ACCUMULATES THE VALUES OF THE SINGLE RUNS, i.e. PostEvaluation!!!!
	 */
	public String resultsToString() {
		StringBuffer result = new StringBuffer();

		result.append("StationId: " + stationId + "\n");
		result.append("Real Data vs. Simulated: Deviation: " + realDataVsSimulated_Deviation + "\t Standard Deviation: " + realDataVsSimulated_StandardDeviation + "\n");
		result.append("Simulated Data: Mean Value: " + simulatedData_MeanValue + "\n");
		result.append("Real Data: Mean Value: " + realData_MeanValue + "\n");

		return result.toString();
	}
}
