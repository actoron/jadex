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

	/** The realization name of the affected {@link CoordinationMechanism} */
	private String realization = null;

	/** The is {@link CoordinationMechanism} active or not */
	private Boolean active = null;

	/**
	 * @param realization
	 * @param active
	 */
	public CoordinationChangeEvent(String realization, Boolean active) {
		super();
		this.realization = realization;
		this.active = active;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoordinationChangeEvent [" + (realization != null ? "realization=" + realization + ", " : "") + (active != null ? "active=" + active : "") + "]";
	}
}