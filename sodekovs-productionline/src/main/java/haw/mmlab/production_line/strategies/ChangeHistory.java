/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.configuration.Role;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Stores the local change history of an agent to realize if an agent has already taken a capability from an other agent. If so the agent should not give away a {@link Role} to this agent, which
 * requires the already taken capability. This is needed to avoid infinite role swapping.
 * 
 * @author Peter
 * 
 */
public class ChangeHistory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changeHistory == null) ? 0 : changeHistory.hashCode());
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
		ChangeHistory other = (ChangeHistory) obj;
		if (changeHistory == null) {
			if (other.changeHistory != null)
				return false;
		} else if (!changeHistory.equals(other.changeHistory))
			return false;
		return true;
	}

	/** The change history */
	private Map<String, HistoryContainer> changeHistory;

	/**
	 * Default Constructor
	 */
	public ChangeHistory() {
		changeHistory = new HashMap<String, HistoryContainer>();
	}

	/**
	 * Checks if the change history already contains the given capability for the given agent id.
	 * 
	 * @param otherAgentId
	 *            the given capability
	 * @param capability
	 *            the given agent id
	 * @return <code>true</code> if the change history already contains the given capability for the given agent id, else <code>false</code>
	 */
	public boolean contains(String otherAgentId, String capability) {
		if (changeHistory.containsKey(otherAgentId)) {
			HistoryContainer hc = changeHistory.get(otherAgentId);
			if (hc.contains(capability)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds the {@link Set} of taken capabilities to the change history for the given deficient agent id.
	 * 
	 * @param otherAgentId
	 *            the agent id of the deficient agent
	 * @param takenCaps
	 *            the taken capabilities
	 */
	public void add(String otherAgentId, Set<String> takenCaps) {
		if (changeHistory.containsKey(otherAgentId)) {
			changeHistory.get(otherAgentId).addAll(takenCaps);
		} else {
			HistoryContainer hc = new HistoryContainer(takenCaps);
			changeHistory.put(otherAgentId, hc);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "ChangeHistory [" + (changeHistory != null ? "changeHistory=" + toString(changeHistory.entrySet(), maxLen) : "") + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * private container to encapsulate the history parameters.
	 * 
	 * @author Peter
	 * 
	 */
	private class HistoryContainer {
		private Set<String> takenCapabilities;

		/**
		 * @param takenCapabilities
		 */
		HistoryContainer(Set<String> takenCapabilities) {
			super();
			this.takenCapabilities = takenCapabilities;
		}

		/**
		 * Adds all the given capabilities to the {@link Set} of already taken capabilities.
		 * 
		 * @param capabilities
		 *            the given capabilities
		 */
		private void addAll(Set<String> capabilities) {
			this.takenCapabilities.addAll(capabilities);
		}

		/**
		 * Checks wheather the given capabilility is already contained in the {@link Set} of already taken capabilities.
		 * 
		 * @param capability
		 *            the given capabilility
		 * @return true if the {@link Set} of of already taken capabilities contains the given capability, else <code>false</code>
		 */
		private boolean contains(String capability) {
			return this.takenCapabilities.contains(capability);
		}

		private String toString(Collection<?> collection, int maxLen) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			int i = 0;
			for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
				if (i > 0)
					builder.append(", ");
				builder.append(iterator.next());
			}
			builder.append("]");
			return builder.toString();
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
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((takenCapabilities == null) ? 0 : takenCapabilities.hashCode());
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
			HistoryContainer other = (HistoryContainer) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (takenCapabilities == null) {
				if (other.takenCapabilities != null)
					return false;
			} else if (!takenCapabilities.equals(other.takenCapabilities))
				return false;
			return true;
		}

		private ChangeHistory getOuterType() {
			return ChangeHistory.this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final int maxLen = 10;
			return "HistoryContainer [" + (takenCapabilities != null ? "takenCapabilities=" + toString(takenCapabilities, maxLen) : "") + "]";
		}
	}
}
