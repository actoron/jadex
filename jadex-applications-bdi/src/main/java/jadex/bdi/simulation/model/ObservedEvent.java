package jadex.bdi.simulation.model;

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

	public ObservedEvent(){
		
	}
	
	public ObservedEvent(String experimentId){
		this.experimentId = experimentId;
		this.timestamp = System.currentTimeMillis();
		
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

}
