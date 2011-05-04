package haw.mmlab.production_line.logging;

import java.util.ArrayList;
import java.util.List;

/**
 * Class holds a {@link List} of {@link LogEntry}s. Mapping a run from
 * the database.
 * 
 * @author thomas
 */
public class Run {

	/**
	 * The run id.
	 */
	private int id = 0;

	/**
	 * All the entries for this run.
	 */
	private List<LogEntry> entries = null;

	/**
	 * Default constructor.
	 */
	public Run() {
		super();

		this.entries = new ArrayList<LogEntry>();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the entries
	 */
	public List<LogEntry> getEntries() {
		return entries;
	}

	/**
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(List<LogEntry> entries) {
		this.entries = entries;
	}

	/**
	 * Adds an {@link LogEntry} to the {@link List} of entries.
	 * 
	 * @param entry
	 *            - the new entry
	 */
	public void addEntry(LogEntry entry) {
		this.entries.add(entry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Run [" + (entries != null ? "entries=" + entries + ", " : "") + "id=" + id + "]";
	}

	/**
	 * @return - a {@link List} of all the agentIds (as {@link String}) in this
	 *         {@link Run}.
	 */
	public List<Agent> getAgents() {
		List<Agent> agents = new ArrayList<Agent>();

		for (LogEntry entry : this.entries) {
			Agent agent = entry.getAgent();

			if (!agents.contains(agent)) {
				agents.add(agent);
			}
		}

		return agents;
	}

	/**
	 * @return - the highest logical time for this run.
	 */
	public int getHighestIntervalTime() {
		int time = 0;

		for (LogEntry entry : this.entries) {
			int logicalTime = entry.getIntervalTime();

			if (logicalTime > time) {
				time = logicalTime;
			}
		}

		return time;
	}

	/**
	 * Returns an {@link LogEntry} with the given parameters.
	 * 
	 * @param agentId
	 *            - the agent's id.
	 * @param logicalTime
	 *            - the logical time.
	 * @return - an {@link LogEntry} which matches the given parameters
	 *         or <code>null</code> if none matches the parameters.
	 */
	public LogEntry getEntry(String agentId, int logicalTime) {
		for (LogEntry entry : this.entries) {
			Agent agent = entry.getAgent();

			if (agentId.equals(agent.getAgentId()) && logicalTime == entry.getIntervalTime()) {
				return entry;
			}
		}

		return null;
	}
}