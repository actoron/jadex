package haw.mmlab.production_line.logging;

/**
 * Class encapsulating all the properties of an Archiv_Log table entry.
 * 
 * @author thomas
 */
public class LogEntry {

	/**
	 * The agent!
	 */
	private Agent agent = null;

	/**
	 * The interval time.
	 */
	private int intervalTime = 0;

	/**
	 * The agent's state.
	 */
	private State state = null;

	/**
	 * Default constructor
	 */
	public LogEntry() {
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
	 * @return the intervalTime
	 */
	public int getIntervalTime() {
		return intervalTime;
	}

	/**
	 * @param intervalTime
	 *            the intervalTime to set
	 */
	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
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
		return "LogEntry [" + (agent != null ? "agent=" + agent + ", " : "") + "intervalTime=" + intervalTime + ", "
				+ (state != null ? "state=" + state : "") + "]";
	}
}