package sodekovs.investigation.model;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import sodekovs.util.misc.TimeConverter;

/**
 * Denotes an event that has been observed at experiment runtime
 * 
 * @author Vilenica
 * 
 */
@XmlRootElement(name = "ObservedEvent")
public class ObservedEvent {

	// private Data dataReference;
	private long absoluteTimestamp;
	private long relativeTimestamp;
	// private String value;
	private String experimentId;
	private String applicationName;
	private String dataName;
	// HACK: should be the same like the relative timestamp
	private double tick;
	// Complex Event: the observed event contains several properties, not just one
	// private boolean isComplexEvent = false;
	// The additional properties, that were observed, of this complex event
	private HashMap<String, Object> observedObjectProperties;

	// If there is more than one instance of this object that should be observed, than this String denotes the ID of the object to be distinguished.
	// private String multipleInstanceId;
	// private boolean isMultipleInstance = false;

	public ObservedEvent() {

	}

	public ObservedEvent(String experimentId) {
		this.experimentId = experimentId;
		this.absoluteTimestamp = System.currentTimeMillis();

	}

	public ObservedEvent(String applicationName, String experimentId, long absoluteTimestamp, String dataName, HashMap<String, Object> observedObjectProperties, double tick) {
		this.applicationName = applicationName;
		this.experimentId = experimentId;
		this.absoluteTimestamp = absoluteTimestamp;
		this.dataName = dataName;
		// this.value = value;
		this.observedObjectProperties = observedObjectProperties;
		this.tick = tick;
	}

	// public ObservedEvent(String applicationName, String experimentId, long absoluteTimestamp, String dataName, String value, double tick, HashMap<String, Object> complexEventProperties) {
	// this.applicationName = applicationName;
	// this.experimentId = experimentId;
	// this.absoluteTimestamp = absoluteTimestamp;
	// this.dataName = dataName;
	// // this.value = value;
	// this.tick = tick;
	// // this.isComplexEvent = isComplexEvent;
	// this.observedObjectProperties = complexEventProperties;
	// }

	// /**
	// * Reference to the data object of the observer within the SimulationConfiguration File
	// * @return
	// */
	// public Data getDataReference() {
	// return dataReference;
	// }
	//
	// public void setDataReference(Data dataReference) {
	// this.dataReference = dataReference;
	// }

	/**
	 * Timestamp of the observed event. The timestamp is absolute.
	 * 
	 * @return
	 */
	@XmlAttribute(name = "absoluteTimestamp")
	public long getAbsoluteTimestamp() {
		return absoluteTimestamp;
	}

	public void setAbsoluteTimestamp(long timestamp) {
		this.absoluteTimestamp = timestamp;
	}

	// /**
	// * The observed value
	// *
	// * @return
	// */
	// @XmlAttribute(name = "value")
	// public String getValue() {
	// return value;
	// }
	//
	// public void setValue(String value) {
	// this.value = value;
	// }

	/**
	 * The ID of the Experiment where the event occurred.
	 * 
	 * @return
	 */
	@XmlAttribute(name = "ExperimentId")
	public String getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	/**
	 * The name of the application (on the jadex plattform)
	 * 
	 * @return
	 */
	@XmlAttribute(name = "ApplicationName")
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Timestamp of the observed event. In relation to the starttime of the experiment.
	 * 
	 * @return
	 */
	@XmlAttribute(name = "relativeTimestamp")
	public long getRelativeTimestamp() {
		return relativeTimestamp;
	}

	public void setRelativeTimestamp(long relativeTimestamp) {
		this.relativeTimestamp = relativeTimestamp;
	}

	// /**
	// * Hack!
	// * Returns the name of the data reference
	// * @return
	// */
	// @XmlAttribute(name="DataName")
	// public String getNameOfObservedData() {
	// return this.getDataReference().getName();
	// }

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

	// /**
	// * Complex Event: the observed event contains several properties, not just one.
	// *
	// * @return
	// */
	// public boolean isComplexEvent() {
	// return isComplexEvent;
	// }
	//
	// /**
	// * Complex Event: the observed event contains several properties, not just one.
	// *
	// * @return
	// */
	// public void setComplexEvent(boolean isComplexEvent) {
	// this.isComplexEvent = isComplexEvent;
	// }

	/**
	 * Complex Event: the observed event contains several properties, not just one. HashMap: The additional properties, that were observed, of this complex event
	 * 
	 * @return
	 */
	public HashMap<String, Object> getObservedObjectProperties() {
		return observedObjectProperties;
	}

	/**
	 * Complex Event: the observed event contains several properties, not just one. HashMap: The additional properties, that were observed, of this complex event
	 * 
	 * @param complexEventProperties
	 */
	public void setObservedObjectProperties(HashMap<String, Object> observedObjectProperties) {
		this.observedObjectProperties = observedObjectProperties;
	}

	// /**
	// * If there is more than one instance of this object that should be observed, than this String denotes the ID of the object to be distinguished.
	// *
	// * @return
	// */
	// public String getMultipleInstanceId() {
	// return multipleInstanceId;
	// }
	//
	// /**
	// * If there is more than one instance of this object that should be observed, than this String denotes the ID of the object to be distinguished.
	// *
	// * @return
	// */
	// public void setMultipleInstanceId(String multipleInstanceId) {
	// this.multipleInstanceId = multipleInstanceId;
	// }
	//
	// /**
	// * Is there more than one instance of this object that should be observed?
	// *
	// * @return
	// */
	// public boolean isMultipleInstance() {
	// return isMultipleInstance;
	// }

	// /**
	// * Is there more than one instance of this object that should be observed?
	// *
	// * @param isMultipleInstance
	// */
	// public void setMultipleInstance(boolean isMultipleInstance) {
	// this.isMultipleInstance = isMultipleInstance;
	// }

	public String toString() {
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
		buffer.append("DataName (ID): ");
		buffer.append(getDataName());
		buffer.append("\t");
		// if(isMultipleInstance){
		// buffer.append("Multiple Instances: InstanceID: ");
		// buffer.append(getMultipleInstanceId());
		// buffer.append("\t");
		// }
		// if (isComplexEvent) {
		buffer.append("Following properties were observed for this event: ");
		buffer.append("\n");
		for (Iterator<String> it = observedObjectProperties.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			buffer.append(key);
			buffer.append(" -> observed value: ");
			buffer.append(observedObjectProperties.get(key));
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
