package jadex.simulation.model;

import jadex.simulation.helper.TimeConverter;

/**
 * Class contains the results of one experiment
 * @author Vilenica
 *
 */
public class ExperimentResult {

	private long startTime;
	private long endTime;
	private String experimentID;
	private String name;
	private String optimizationValue;
	private String optimizationParameterName;
	
	public ExperimentResult(){	
	}
	
	public ExperimentResult(long startTime, long endTime, String experimentID, String name, String optimizationValue, String optimizationParameterName){
		this.startTime = startTime;
		this.endTime = endTime;
		this.experimentID = experimentID;
		this.name = name;
		this.optimizationValue = optimizationValue;
		this.optimizationParameterName = optimizationParameterName;
	}
	
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public String getExperimentID() {
		return experimentID;
	}
	public void setExperimentID(String experimentID) {
		this.experimentID = experimentID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOptimizationValue() {
		return optimizationValue;
	}
	public void setOptimizationValue(String optimizationValue) {
		this.optimizationValue = optimizationValue;
	}
	public String getOptimizationParameterName() {
		return optimizationParameterName;
	}
	public void setOptimizationParameterName(String optimizationParameterName) {
		this.optimizationParameterName = optimizationParameterName;
	}
	
	/**
	 * Returns the duration of the experiment
	 * @return
	 */
	public long getDuraration(){		
		return getEndTime() - getStartTime();		
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name and ID: ");
		buffer.append(getName());
		buffer.append(" - ");
		buffer.append(getExperimentID());
		buffer.append("\n");
		buffer.append("Start and End Time: ");
		buffer.append(TimeConverter.longTime2DateString(getStartTime()));
		buffer.append(" - ");
		buffer.append(TimeConverter.longTime2DateString(getEndTime()));
		buffer.append("\n");
		buffer.append("Duration: ");
		buffer.append(getDuraration() / 1000);
		buffer.append(" sec");
		buffer.append("\n");
		buffer.append("Optimization: Parameter Name and Value: ");
		buffer.append(getOptimizationParameterName());
		buffer.append(" - ");
		buffer.append(getOptimizationValue());
				
		return buffer.toString();
		
	}
}


