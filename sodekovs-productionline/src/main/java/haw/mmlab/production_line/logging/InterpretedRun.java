/**
 * 
 */
package haw.mmlab.production_line.logging;

import java.util.Set;
import java.util.TreeSet;

/**
 * An interpreted run containing a {@link Set} ({@link TreeSet}) with all the
 * {@link TimeSlot}s for this run.
 * 
 * @author thomas
 */
public class InterpretedRun {

	private Set<TimeSlot> timeSlots = null;

	public InterpretedRun() {
		this.timeSlots = new TreeSet<TimeSlot>();
	}

	/**
	 * @return the timeSlots
	 */
	public Set<TimeSlot> getTimeSlots() {
		return timeSlots;
	}

	/**
	 * @param timeSlots
	 *            the timeSlots to set
	 */
	public void setTimeSlots(Set<TimeSlot> timeSlots) {
		this.timeSlots = timeSlots;
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
		result = prime * result + ((timeSlots == null) ? 0 : timeSlots.hashCode());
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
		InterpretedRun other = (InterpretedRun) obj;
		if (timeSlots == null) {
			if (other.timeSlots != null)
				return false;
		} else if (!timeSlots.equals(other.timeSlots))
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
		return "InterpretedRun [" + (timeSlots != null ? "timeSlots=" + timeSlots : "") + "]";
	}

	/**
	 * Adds the given {@link TimeSlot} to the list of time slots.
	 * 
	 * @param slot
	 *            - the given time slot
	 * @return true if the set of time slots did not already contain the
	 *         specified element
	 */
	public boolean addTimeSlot(TimeSlot slot) {
		return this.timeSlots.add(slot);
	}

	/**
	 * Returns the number of agents in this run.
	 * 
	 * @return the number of agents
	 */
	public int getAgentCount() {
		int agentCount = 0;

		if (timeSlots != null) {
			for (TimeSlot timeSlot : timeSlots) {
				return timeSlot.getAgentStates().size();
			}
		}

		return agentCount;
	}

	/**
	 * Returns the size of this {@link InterpretedRun} by returning
	 * {@link InterpretedRun#timeSlots}<code>.size()</code>.
	 * 
	 * @return the size of this run
	 */
	public int getSize() {
		int size = this.timeSlots != null ? this.timeSlots.size() : 0;
		return size;
	}
}