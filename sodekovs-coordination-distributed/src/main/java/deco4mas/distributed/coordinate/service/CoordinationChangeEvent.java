/**
 * 
 */
package deco4mas.distributed.coordinate.service;

import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * This class holds all the information of a coordination change event.
 * 
 * @author Thomas Preisler
 */
public class CoordinationChangeEvent {

	public static final String MECHANISM_CHANGE_EVENT = "MECHANISM_CHANGE_EVENT";
	public static final String CONFIGURATION_CHANGE_EVENT = "CONFIGURATION_CHANGE_EVENT";

	/** The type of the event */
	private String type = null;

	/** The realization name of the affected {@link CoordinationMechanism} */
	private String realization = null;

	/** The is {@link CoordinationMechanism} active or not */
	private Boolean active = null;

	/** The key of the affected configuration property, <code>null</code> if not affected */
	private String key = null;

	/** The value of the affected configuration property, <code>null</code> if not affected */
	private String value = null;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            the type of the coordination change event.
	 */
	public CoordinationChangeEvent(String type) {
		this.type = type;
	}

	/**
	 * @return the realization
	 */
	public String getRealization() {
		return realization;
	}

	/**
	 * @param realization
	 *            the realization to set
	 */
	public void setRealization(String realization) {
		this.realization = realization;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoordinationChangeEvent [" + (type != null ? "type=" + type + ", " : "") + (realization != null ? "realization=" + realization + ", " : "")
				+ (active != null ? "active=" + active + ", " : "") + (key != null ? "key=" + key + ", " : "") + (value != null ? "value=" + value : "") + "]";
	}
}