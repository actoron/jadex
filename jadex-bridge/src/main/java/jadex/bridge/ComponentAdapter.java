package jadex.bridge;

import com.sun.corba.se.impl.orbutil.closure.Future;

import jadex.commons.ChangeEvent;
import jadex.commons.future.IFuture;

/**
 *  Adapter for the IComponentListener interface.
 *
 */
public abstract class ComponentAdapter implements IComponentListener
{
	/**
	 *  Called when the component is closing down
	 *  (i.e. moving to the end state).
	 *  In this state the component should perform cleanup operations
	 *  and is still able to execute
	 *  goals/plans as well as send/receive messages.
	 *  @param ae The component event.
	 */
	public IFuture componentTerminating(ChangeEvent ce)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Invoked when the component was finally terminated.
	 *  No more component related functionality (e.g. goals plans)
	 *  can be executed.
	 *  @param ae The component event.
	 */
	public IFuture componentTerminated(ChangeEvent ce)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Invoked when a change occurs with the component.
	 *  The changes depend on the underlying component type.
	 */
	public IFuture eventOccured(IComponentChangeEvent cce)
	{
		return IFuture.DONE;
	}
}
