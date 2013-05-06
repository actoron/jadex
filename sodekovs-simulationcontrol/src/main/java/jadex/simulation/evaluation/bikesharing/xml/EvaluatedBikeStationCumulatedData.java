package jadex.simulation.evaluation.bikesharing.xml;


/**
 * Required for storage as XML file
 * 
 * @author Vilenica
 * 
 */
public class EvaluatedBikeStationCumulatedData {

	private String stationId;
	private double realDataVsSimulated_Deviation;
	private double realDataVsSimulated_StandardDeviation;
	private double simulatedData_MeanValue;
	private double realData_MeanValue;
		

	public EvaluatedBikeStationCumulatedData(String stationId, double simulatedData_MeanValue,  double realData_MeanValue){
		this.stationId = stationId;		
		this.simulatedData_MeanValue = simulatedData_MeanValue;
		this.realData_MeanValue = realData_MeanValue;
	}
	
	public void addSimulatedData_MeanValue(double val){
		simulatedData_MeanValue+=val;	
	}
	
	public void addRealData_MeanValue(double val){
		realData_MeanValue+=val;	
	}
	
//	, double realDataVsSimulated_Deviation, double realDataVsSimulated_StandardDeviation, double simulatedData_MeanValue,  double realData_MeanValue) 
}
