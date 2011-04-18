package jadex.bridge;

import jadex.commons.ChangeEvent;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

/**
 *  Interface for entities that want to be 
 *  notified on certain component lifecycle events.
 */
public interface IComponentListener extends IRemotable
{
	/**
	 *  Called when the component is closing down
	 *  (i.e. moving to the end state).
	 *  In this state the component should perform cleanup operations
	 *  and is still able to execute
	 *  goals/plans as well as send/receive messages.
	 *  @param ae The component event.
	 */
	public IFuture componentTerminating(ChangeEvent ce);
	
	/**
	 *  Invoked when the component was finally terminated.
	 *  No more component related functionality (e.g. goals plans)
	 *  can be executed.
	 *  @param ae The component event.
	 */
	public IFuture componentTerminated(ChangeEvent ce);
	
	/**
	 *  Invoked when a change occurs with the component.
	 *  The changes depend on the underlying component type.
	 */
	public IFuture eventOccured(IComponentChangeEvent cce);
}
