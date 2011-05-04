package haw.mmlab.production_line.domain;

import haw.mmlab.production_line.configuration.Role;

import java.util.Arrays;
import java.util.List;

/**
 * HelpRequest class containing the current roles, which the agent can not apply
 * anymore, all the capabilities the agent can apply and the agent's id.
 * 
 * @author thomas
 */
public class HelpRequest implements MediumMessage {

	/**
	 * The minimum level of escalation
	 */
	public static int MIN_ESCALATION_LEVEL = 0;

	/**
	 * Id for Serialization.
	 */
	private static final long serialVersionUID = 2126663145177641554L;

	private String agentId = null;

	private List<Role> deficientRoles = null;

	private String[] capabilities = null;

	private int hopCount = 0;

	private int escalationLevel = MIN_ESCALATION_LEVEL;

	/**
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId
	 *            the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * @return the deficientRoles
	 */
	public List<Role> getDeficientRoles() {
		return deficientRoles;
	}

	/**
	 * @param deficientRoles
	 *            the deficientRoles to set
	 */
	public void setDeficientRoles(List<Role> deficientRoles) {
		this.deficientRoles = deficientRoles;
	}

	/**
	 * @return the capabilities
	 */
	public String[] getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities
	 *            the capabilities to set
	 */
	public void setCapabilities(String[] capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return the hopCount
	 */
	public int getHopCount() {
		return hopCount;
	}

	/**
	 * @param hopCount
	 *            the hopCount to set
	 */
	public void setHopCount(int hopCount) {
		this.hopCount = hopCount;
	}

	/**
	 * @return the escalationLevel
	 */
	public int getEscalationLevel() {
		return escalationLevel;
	}

	/**
	 * @param escalationLevel
	 *            the escalationLevel to set
	 */
	public void setEscalationLevel(int escalationLevel) {
		this.escalationLevel = escalationLevel;
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
		result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
		result = prime * result + Arrays.hashCode(capabilities);
		result = prime * result + ((deficientRoles == null) ? 0 : deficientRoles.hashCode());
		result = prime * result + escalationLevel;
		result = prime * result + hopCount;
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
		HelpRequest other = (HelpRequest) obj;
		if (agentId == null) {
			if (other.agentId != null)
				return false;
		} else if (!agentId.equals(other.agentId))
			return false;
		if (!Arrays.equals(capabilities, other.capabilities))
			return false;
		if (deficientRoles == null) {
			if (other.deficientRoles != null)
				return false;
		} else if (!deficientRoles.equals(other.deficientRoles))
			return false;
		if (escalationLevel != other.escalationLevel)
			return false;
		if (hopCount != other.hopCount)
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
		final int maxLen = 10;
		return "HelpRequest ["
				+ (agentId != null ? "agentId=" + agentId + ", " : "")
				+ (capabilities != null ? "capabilities="
						+ Arrays.asList(capabilities).subList(0, Math.min(capabilities.length, maxLen)) + ", " : "")
				+ (deficientRoles != null ? "deficientRoles="
						+ deficientRoles.subList(0, Math.min(deficientRoles.size(), maxLen)) + ", " : "")
				+ "escalationLevel=" + escalationLevel + ", hopCount=" + hopCount + "]";
	}

	/**
	 * Increments the hop count by one.
	 * 
	 * @return the new hop count value
	 */
	public int incrementHopCount() {
		hopCount++;
		return hopCount;
	}

	/**
	 * Increments the escalation level by one.
	 * 
	 * @return the new escalation level value
	 */
	public int incrementEscalationLevel() {
		return escalationLevel++;
	}
}