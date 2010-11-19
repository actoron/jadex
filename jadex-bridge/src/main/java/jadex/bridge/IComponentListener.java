package jadex.bridge;

import jadex.commons.ChangeEvent;

/**
 *  Interface for entities that want to be 
 *  notified on certain component lifecycle events.
 */
public interface IComponentListener
{
	/**
	 *  Called when the component is closing down
	 *  (i.e. moving to the end state).
	 *  In this state the component should perform cleanup operations
	 *  and is still able to execute
	 *  goals/plans as well as send/receive messages.
	 *  @param ae The component event.
	 */
	public void componentTerminating(ChangeEvent ce);
	
	/**
	 *  Invoked when the component was finally terminated.
	 *  No more component related functionality (e.g. goals plans)
	 *  can be executed.
	 *  @param ae The component event.
	 */
	public void componentTerminated(ChangeEvent ce);
}
