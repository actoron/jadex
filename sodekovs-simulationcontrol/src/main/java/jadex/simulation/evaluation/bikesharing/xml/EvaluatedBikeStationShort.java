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
		

	public EvaluatedBikeStationShort(String stationId, double realDataVsSimulated_Deviation, double realDataVsSimulated_StandardDeviation, double simulatedData_MeanValue) {
		this.stationId = stationId;
		this.realDataVsSimulated_Deviation = realDataVsSimulated_Deviation;
		this.realDataVsSimulated_StandardDeviation = realDataVsSimulated_StandardDeviation;
		this.simulatedData_MeanValue = simulatedData_MeanValue;
	}
}
