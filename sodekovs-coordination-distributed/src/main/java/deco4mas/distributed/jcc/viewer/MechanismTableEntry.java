/**
 * 
 */
package deco4mas.distributed.jcc.viewer;

import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * @author thomas
 * 
 */
public class MechanismTableEntry {

	private CoordinationMechanism mechanism;

	private Boolean active;

	/**
	 * @param mechanism
	 * @param active
	 */
	public MechanismTableEntry(CoordinationMechanism mechanism, Boolean active) {
		super();
		this.mechanism = mechanism;
		this.active = active;
	}

	/**
	 * @return the mechanism
	 */
	public CoordinationMechanism getMechanism() {
		return mechanism;
	}

	/**
	 * @param mechanism
	 *            the mechanism to set
	 */
	public void setMechanism(CoordinationMechanism mechanism) {
		this.mechanism = mechanism;
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((mechanism == null) ? 0 : mechanism.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MechanismTableEntry other = (MechanismTableEntry) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (mechanism == null) {
			if (other.mechanism != null)
				return false;
		} else if (!mechanism.equals(other.mechanism))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MechanismTableEntry [" + (mechanism != null ? "mechanism=" + mechanism + ", " : "") + (active != null ? "active=" + active : "") + "]";
	}
}