package haw.mmlab.production_line.logging;

/**
 * Class holds the id and type of an agent.
 * 
 * @author thomas
 * 
 */
public class Agent {

	/**
	 * The agent's id.
	 */
	private String agentId = null;

	/**
	 * The agent's type.
	 */
	private String agentType = null;

	/**
	 * Default Constructor.
	 */
	public Agent() {
		super();
	}

	/**
	 * Constructor using fields.
	 * 
	 * @param agentId
	 *            - the agents's id
	 * @param agentType
	 *            - the agent's type
	 */
	public Agent(String agentId, String agentType) {
		this.agentId = agentId;
		this.agentType = agentType;
	}

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
	 * @return the agentType
	 */
	public String getAgentType() {
		return agentType;
	}

	/**
	 * @param agentType
	 *            the agentType to set
	 */
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Agent [" + (agentId != null ? "agentId=" + agentId + ", " : "")
				+ (agentType != null ? "agentType=" + agentType : "") + "]";
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
		result = prime * result + ((agentType == null) ? 0 : agentType.hashCode());
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
		Agent other = (Agent) obj;
		if (agentId == null) {
			if (other.agentId != null)
				return false;
		} else if (!agentId.equals(other.agentId))
			return false;
		if (agentType == null) {
			if (other.agentType != null)
				return false;
		} else if (!agentType.equals(other.agentType))
			return false;
		return true;
	}
}