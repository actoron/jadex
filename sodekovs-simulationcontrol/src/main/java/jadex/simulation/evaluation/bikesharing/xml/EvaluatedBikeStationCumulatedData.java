package jadex.simulation.evaluation.bikesharing.xml;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;


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
	
	private DoubleArrayList simulatedData_MeanValuesList = new DoubleArrayList();
		

	public EvaluatedBikeStationCumulatedData(String stationId, double simulatedData_MeanValue,  double realData_MeanValue){
		this.stationId = stationId;				
		this.realData_MeanValue = realData_MeanValue;
		
		addSimulatedData_MeanValue(simulatedData_MeanValue);
	}
	
	public void addSimulatedData_MeanValue(double val){
		simulatedData_MeanValuesList.add(new Double(val));	
	}
	
	public void setRealData_MeanValue(double val){
		realData_MeanValue = val;	
	}
	
	public void evaluate(){
		//list has to be ordered according to the Colt API
		simulatedData_MeanValuesList.sort();	
		
		simulatedData_MeanValue = Descriptive.mean(simulatedData_MeanValuesList);		
		realDataVsSimulated_Deviation = Descriptive.sampleVariance(simulatedData_MeanValuesList, realData_MeanValue);
		realDataVsSimulated_StandardDeviation = Descriptive.sampleStandardDeviation(simulatedData_MeanValuesList.size(), realDataVsSimulated_Deviation);	
	}

	public String getStationId() {
		return stationId;
	}

	public double getRealDataVsSimulated_Deviation() {
		return realDataVsSimulated_Deviation;
	}

	public double getRealDataVsSimulated_StandardDeviation() {
		return realDataVsSimulated_StandardDeviation;
	}

	public double getSimulatedData_MeanValue() {
		return simulatedData_MeanValue;
	}

	public double getRealData_MeanValue() {
		return realData_MeanValue;
	}

}
