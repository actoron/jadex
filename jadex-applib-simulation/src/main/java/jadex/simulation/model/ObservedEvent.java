package jadex.simulation.model;

import jadex.simulation.helper.TimeConverter;

/**
 * Denotes an event that has been observed at experiment runtime
 * @author Vilenica
 *
 */
public class ObservedEvent {
	
	private Data dataReference;
	private long timestamp;
	private Object value;
	private String experimentId;
	private String applicationName;

	public ObservedEvent(){
		
	}
	
	public ObservedEvent(String experimentId){
		this.experimentId = experimentId;
		this.timestamp = System.currentTimeMillis();
		
	}
	
	public ObservedEvent(String applicationName, String experimentId, long timestamp, Data dataReference, Object value){
		this.applicationName = applicationName;
		this.experimentId = experimentId;
		this.timestamp = timestamp;
		this.dataReference = dataReference;
		this.value = value;
	}
	
	
	/**
	 * Reference to the data object of the observer within the SimulationConfiguration File
	 * @return
	 */
	public Data getDataReference() {
		return dataReference;
	}
	
	public void setDataReference(Data dataReference) {
		this.dataReference = dataReference;
	}
	
	/**
	 * Timestamp of the observed event
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * The observed value
	 * @return
	 */
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * The ID of the Experiment where the event occurred.
	 * @return
	 */
	public String getExperimentId() {
		return experimentId;
	}
	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	/**
	 * The name of the application (on the jadex plattform)
	 * @return
	 */
	
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Application Name and ExperimentId: ");
		buffer.append(getApplicationName());
		buffer.append(" - ");
		buffer.append(getExperimentId());
		buffer.append("\n");
		buffer.append("Timestamp: ");
		buffer.append(TimeConverter.longTime2DateString(getTimestamp()));
		buffer.append(" - ");
		buffer.append(getTimestamp());
		buffer.append("\n");
		buffer.append("DataName: ");
		buffer.append(getDataReference().getName());
		buffer.append("\n");
		buffer.append("Observed Value: ");
		buffer.append(getValue().toString());
		buffer.append("\n");
		
		return buffer.toString();
	}
}
