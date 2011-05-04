package haw.mmlab.production_line.configuration;

import javax.xml.bind.annotation.XmlElement;

/**
 * Role definition. A Role has a precondition and postcondition (
 * {@link Condition} )and one {@link Capability}.
 * 
 * @author thomas
 */
public class Role {

	private Condition precondition = null;

	private Condition postcondition = null;

	private Capability capability = null;

	private Integer processingTime = null;

	/**
	 * @return the processingTime
	 */
	@XmlElement(name = "processingtime")
	public Integer getProcessingTime() {
		return processingTime;
	}

	/**
	 * @param processingTime
	 *            the processingTime to set
	 */
	public void setProcessingTime(Integer processingTime) {
		this.processingTime = processingTime;
	}

	/**
	 * @return the precondition
	 */
	@XmlElement(name = "precondition")
	public Condition getPrecondition() {
		return precondition;
	}

	/**
	 * @param precondition
	 *            the precondition to set
	 */
	public void setPrecondition(Condition precondition) {
		this.precondition = precondition;
	}

	/**
	 * @return the postcondition
	 */
	@XmlElement(name = "postcondition")
	public Condition getPostcondition() {
		return postcondition;
	}

	/**
	 * @param postcondition
	 *            the postcondition to set
	 */
	public void setPostcondition(Condition postcondition) {
		this.postcondition = postcondition;
	}

	/**
	 * @return the capability
	 */
	@XmlElement(name = "capability")
	public Capability getCapability() {
		return capability;
	}

	public String getCapabilityAsString() {
		if (capability != null) {
			return capability.getId();
		}

		return null;
	}

	/**
	 * @param capability
	 *            the capability to set
	 */
	public void setCapability(Capability capability) {
		this.capability = capability;
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
		result = prime * result + ((capability == null) ? 0 : capability.hashCode());
		result = prime * result + ((postcondition == null) ? 0 : postcondition.hashCode());
		result = prime * result + ((precondition == null) ? 0 : precondition.hashCode());
		result = prime * result + ((processingTime == null) ? 0 : processingTime.hashCode());
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
		Role other = (Role) obj;
		if (capability == null) {
			if (other.capability != null)
				return false;
		} else if (!capability.equals(other.capability))
			return false;
		if (postcondition == null) {
			if (other.postcondition != null)
				return false;
		} else if (!postcondition.equals(other.postcondition))
			return false;
		if (precondition == null) {
			if (other.precondition != null)
				return false;
		} else if (!precondition.equals(other.precondition))
			return false;
		if (processingTime == null) {
			if (other.processingTime != null)
				return false;
		} else if (!processingTime.equals(other.processingTime))
			return false;
		return true;
	}

	public void setTaskId(String taskId) {
		this.postcondition.setTaskId(taskId);
		this.precondition.setTaskId(taskId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Role [" + (capability != null ? "capability=" + capability + ", " : "")
				+ (postcondition != null ? "postcondition=" + postcondition + ", " : "")
				+ (precondition != null ? "precondition=" + precondition + ", " : "")
				+ (processingTime != null ? "processingTime=" + processingTime : "") + "]";
	}
}