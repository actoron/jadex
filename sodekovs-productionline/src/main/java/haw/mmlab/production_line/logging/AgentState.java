package haw.mmlab.production_line.logging;

/**
 * Class holds an {@link Agent} and its states as {@link Integer}s.
 * 
 * @author thomas
 */
public class AgentState {

	/**
	 * The agent!
	 */
	private Agent agent = null;

	/**
	 * The agent's state.
	 */
	private State state = null;

	/**
	 * Default constructor.
	 */
	public AgentState() {
		super();
	}

	/**
	 * @return the agent
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AgentState [" + (agent != null ? "agent=" + agent + ", " : "")
				+ (state != null ? "state=" + state : "") + "]";
	}
}