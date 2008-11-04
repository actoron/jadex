package jadex.bdi.runtime;

/**
 *  Definition of aspects to wait for.
 */
public interface IWaitAbstraction
{
	/**
	 *  The timeout.
	 *  @param timeout The timeout.
	 */
//	public void setTimeout(long timeout);
	
	//-------- adder methods --------
	
	/**
	 *  Add a message event.
	 *  @param type The type.
	 */
	public IWaitAbstraction addMessageEvent(String type);

	/**
	 * Add a message event reply.
	 * @param me The message event.
	 */
	public IWaitAbstraction addReply(IMessageEvent me);

	/**
	 *  Add an internal event.
	 *  @param type The type.
	 */
	public IWaitAbstraction addInternalEvent(String type);

	/**
	 *  Add a goal.
	 *  @param type The type.
	 */
	public IWaitAbstraction addGoal(String type);

	/**
	 *  Add a goal.
	 *  @param goal The goal.
	 */
	public IWaitAbstraction addGoal(IGoal goal);

	/**
	 *  Add a fact changed.
	 *  @param belief The belief or beliefset.
	 */
	public IWaitAbstraction addFactChanged(String belief);

	/**
	 *  Add a fact added.
	 *  @param beliefset The beliefset.
	 */
	public IWaitAbstraction addFactAdded(String beliefset);


	/**
	 *  Add a fact removed.
	 *  @param beliefset The beliefset.
	 */
	public IWaitAbstraction addFactRemoved(String beliefset);
	
	/**
	 *  Add a condition.
	 *  @param condition the condition name.
	 */
	public IWaitAbstraction addCondition(String condition);

	/**
	 *  Add an external condition.
	 *  @param condition the condition.
	 */
	public IWaitAbstraction addExternalCondition(IExternalCondition condition);

	//-------- remover methods --------

	/**
	 *  Remove a message event.
	 *  @param type The type.
	 */
	public void removeMessageEvent(String type);

	/**
	 *  Remove a message event reply.
	 *  @param me The message event.
	 */
	public void removeReply(IMessageEvent me);

	/**
	 *  Remove an internal event.
	 *  @param type The type.
	 */
	public void removeInternalEvent(String type);

	/**
	 *  Remove a goal.
	 *  @param type The type.
	 */
	public void removeGoal(String type);

	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(IGoal goal);
	
	/**
	 *  Remove a fact changed.
	 *  @param belief The belief or beliefset.
	 */
	public void removeFactChanged(String belief);

	/**
	 *  Remove a fact added.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactAdded(String beliefset);


	/**
	 *  Remove a fact removed.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactRemoved(String beliefset);	

	/**
	 *  Remove a condition.
	 *  @param condition the condition name.
	 */
	public void removeCondition(String condition);
	
	/**
	 *  Remove an external condition.
	 *  @param condition the condition.
	 */
	public void	removeExternalCondition(IExternalCondition condition);
}
