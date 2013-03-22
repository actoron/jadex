package jadex.simulation.evaluation.bikesharing;

import java.util.ArrayList;

public class EvaluatedBikeStationData {
	
	private ArrayList<Long> singleValues;
	private Double meanValue;
	private Double medianValue;
	private Double devation;
	private Double standardDevation;
	
	
		
	
	public Double getMedianValue() {
		return medianValue;
	}
	public void setMedianValue(Double medianValue) {
		this.medianValue = medianValue;
	}
	public ArrayList<Long> getSingleValues() {
		return singleValues;
	}
	public void setSingleValues(ArrayList<Long> singleValues) {
		this.singleValues = singleValues;
	}
	public Double getMeanValue() {
		return meanValue;
	}
	public void setMeanValue(Double meanValue) {
		this.meanValue = meanValue;
	}
	public Double getDevation() {
		return devation;
	}
	public void setDevation(Double devation) {
		this.devation = devation;
	}
	public Double getStandardDevation() {
		return standardDevation;
	}
	public void setStandardDevation(Double standardDevation) {
		this.standardDevation = standardDevation;
	}
	

}
