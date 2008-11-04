package jadex.tools.comanalyzer;

/**
 * Interface for tooltabs to be informed about agentlist changes.
 */
public interface IAgentListListener
{

	/**
	 * @param agents The agents to remove.
	 */
	void agentsRemoved(Agent[] agents);

	/**
	 * @param agents The agents to add.
	 */
	void agentsAdded(Agent[] agents);

	/**
	 * @param agents The agents that have changed.
	 */
	void agentsChanged(Agent[] agents);

}
