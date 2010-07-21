package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jadex.simulation.helper.TimeConverter;

/**
 * Denotes an event that has been observed at experiment runtime
 * @author Vilenica
 *
 */
@XmlRootElement(name="ObservedEvent")
public class ObservedEvent {
	
//	private Data dataReference;
	private long absoluteTimestamp;
	private long relativeTimestamp;
	private String value;
	private String experimentId;
	private String applicationName;
	private String dataName;
	//HACK: should be the same like the relative timestamp
	private double tick;
	

	public ObservedEvent(){
		
	}
			
	public ObservedEvent(String experimentId){
		this.experimentId = experimentId;
		this.absoluteTimestamp = System.currentTimeMillis();
		
	}
	
	public ObservedEvent(String applicationName, String experimentId, long absoluteTimestamp, String dataName, String value, double tick){
		this.applicationName = applicationName;
		this.experimentId = experimentId;
		this.absoluteTimestamp = absoluteTimestamp;
		this.dataName = dataName;
		this.value = value;
		this.tick = tick;
	}
	
	
//	/**
//	 * Reference to the data object of the observer within the SimulationConfiguration File
//	 * @return
//	 */
//	public Data getDataReference() {
//		return dataReference;
//	}
//	
//	public void setDataReference(Data dataReference) {
//		this.dataReference = dataReference;
//	}
	
	/**
	 * Timestamp of the observed event. The timestamp is absolute.
	 * @return
	 */
	@XmlAttribute(name="absoluteTimestamp")
	public long getAbsoluteTimestamp() {
		return absoluteTimestamp;
	}
	
	public void setAbsoluteTimestamp(long timestamp) {
		this.absoluteTimestamp = timestamp;
	}
	
	/**
	 * The observed value
	 * @return
	 */
	@XmlAttribute(name="value")
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * The ID of the Experiment where the event occurred.
	 * @return
	 */
	@XmlAttribute(name="ExperimentId")
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
	@XmlAttribute(name="ApplicationName")
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Timestamp of the observed event. In relation to the starttime of the experiment.
	 * @return
	 */
	@XmlAttribute(name="relativeTimestamp")
	public long getRelativeTimestamp() {
		return relativeTimestamp;
	}

	public void setRelativeTimestamp(long relativeTimestamp) {
		this.relativeTimestamp = relativeTimestamp;
	}
	
	
//	/**
//	 * Hack!
//	 * Returns the name of the data reference 
//	 * @return
//	 */
//	@XmlAttribute(name="DataName")
//	public String getNameOfObservedData() {
//		return this.getDataReference().getName();
//	}
	
	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	
	public double getTick() {
		return tick;
	}

	public void setTick(double tick) {
		this.tick = tick;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Application Name and ExperimentId: ");
		buffer.append(getApplicationName());
		buffer.append(" - ");
		buffer.append(getExperimentId());
		buffer.append("\n");
		buffer.append("Absolute Timestamp: ");
		buffer.append(TimeConverter.longTime2DateString(getAbsoluteTimestamp()));
		buffer.append(" - ");
		buffer.append(getAbsoluteTimestamp());
		buffer.append("\n");
		buffer.append("Relative Timestamp: ");
		buffer.append(getRelativeTimestamp());
		buffer.append(" == ?");
		buffer.append(getTick());
		buffer.append("\n");
		buffer.append("DataName: ");
		buffer.append(getDataName());
		buffer.append("\n");
		buffer.append("Observed Value: ");
		buffer.append(getValue().toString());
		buffer.append("\n");
		
		return buffer.toString();
	}
}
