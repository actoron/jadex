package jadex.adapter.base.fipa;


/**
 *  Interface for locally listening to ams changes.
 */
public interface IAMSListener
{
	/**
	 *  Called when a new agent has been added.
	 *  @param aid The agent identifier.
	 */
	public void agentAdded(IAMSAgentDescription desc);
	
	/**
	 *  Called when a new agent has been removed.
	 *  @param aid The agent identifier.
	 */
	public void agentRemoved(IAMSAgentDescription desc);
}
