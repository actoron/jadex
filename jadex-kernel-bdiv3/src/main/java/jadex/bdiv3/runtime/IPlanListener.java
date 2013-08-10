package jadex.bdiv3.runtime;


/**
 *  Listener for observing plans.
 */
public interface IPlanListener<T>
{
//	/**
//	 *  Invoked when a new plan has been added.
//	 *  @param ae The agent event.
//	 */
//	public void planAdded(AgentEvent ae);
	
	/**
	 *  Invoked when a plan has been finished.
	 *  @param ae The agent event.
	 */
	public void planFinished(T result); // todo: parameter?
	
}
