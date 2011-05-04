/**
 * 
 */
package haw.mmlab.production_line.logging;

import haw.mmlab.production_line.state.DeficientState;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link TimeSlot} class containing a {@link List} of {@link AgentState}s for
 * this time.
 * 
 * @author thomas
 */
public class TimeSlot implements Comparable<TimeSlot> {

	/**
	 * The time.
	 */
	private Long time = null;

	/**
	 * List of {@link AgentState}s for this time slot.
	 */
	private List<AgentState> agentStates = null;

	/**
	 * Default constructor.
	 */
	public TimeSlot() {
		super();
		this.agentStates = new ArrayList<AgentState>();
	}

	/**
	 * @param time
	 *            - the given time
	 * @param agentStates
	 *            - a list of agent states
	 */
	public TimeSlot(Long time, List<AgentState> agentStates) {
		super();
		this.time = time;
		this.agentStates = agentStates;
	}

	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * Returns the number of agents which are
	 * {@link DeficientState#DEFICIENT_BY_BREAK} in this time slot.
	 * 
	 * @return the number of deficient agents
	 */
	public int getDeficientsByBreak() {
		int deficients = 0;

		for (AgentState agentState : this.agentStates) {
			int agentDeficientState = agentState.getState().getDeficientState();

			if (agentDeficientState == DeficientState.DEFICIENT_BY_BREAK) {
				deficients++;
			}
		}

		return deficients;
	}

	/**
	 * Returns the number of agents which are
	 * {@link DeficientState#DEFICIENT_BY_CHANGE} in this time slot.
	 * 
	 * @return the number of deficient by change agents
	 */
	public int getDeficientsByChange() {
		int deficients = 0;

		for (AgentState agentState : this.agentStates) {
			int agentDeficientState = agentState.getState().getDeficientState();

			if (agentDeficientState == DeficientState.DEFICIENT_BY_CHANGE) {
				deficients++;
			}
		}

		return deficients;
	}

	/**
	 * Returns the number of agents which are
	 * {@link DeficientState#NOT_DEFICIENT} in this time slot.
	 * 
	 * @return the number of not deficient agents
	 */
	public int getNotDeficients() {
		int notDeficients = 0;

		for (AgentState agentState : this.agentStates) {
			int agentDeficientState = agentState.getState().getDeficientState();

			if (agentDeficientState == DeficientState.NOT_DEFICIENT) {
				notDeficients++;
			}
		}

		return notDeficients;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	/**
	 * @return the agentStates
	 */
	public List<AgentState> getAgentStates() {
		return agentStates;
	}

	/**
	 * @param agentStates
	 *            the agentStates to set
	 */
	public void setAgentStates(List<AgentState> agentStates) {
		this.agentStates = agentStates;
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
		result = prime * result + ((agentStates == null) ? 0 : agentStates.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		TimeSlot other = (TimeSlot) obj;
		if (agentStates == null) {
			if (other.agentStates != null)
				return false;
		} else if (!agentStates.equals(other.agentStates))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "TimeSlot [agentStates=" + agentStates + ", time=" + time + "]";
	}


	public int compareTo(TimeSlot o) {
		return this.time.compareTo(o.time);
	}
}