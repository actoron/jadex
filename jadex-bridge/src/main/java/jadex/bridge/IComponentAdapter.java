package jadex.bridge;

import jadex.service.IServiceContainer;

import java.util.logging.Logger;


/**
 *  The adapter for a specific platform component (e.g. a JADE agent).
 *  These are the methods a kernel components needs to call on its host component.
 */
public interface IComponentAdapter
{
	/**
	 *  Called by the component when it probably awoke from an idle state.
	 *  The platform has to make sure that the component will be executed
	 *  again from now on.
	 *  Note, this method can be called also from external threads
	 *  (e.g. property changes). Therefore, on the calling thread
	 *  no component related actions must be executed (use some kind
	 *  of wake-up mechanism).
	 *  Also proper synchronization has to be made sure, as this method
	 *  can be called concurrently from different threads.
	 */
	public void	wakeup() throws ComponentTerminatedException;
	
	/**
	 *  Execute an action on the component thread.
	 *  May be safely called from any (internal or external) thread.
	 *  The contract of this method is as follows:
	 *  The component adapter ensures the execution of the external action, otherwise
	 *  the method will throw a terminated exception.
	 *  @param action The action to be executed on the component thread.
	 */
	public void invokeLater(Runnable action);
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread();

	/**
	 *  Cause termination of the component.
	 *  IKernelComponent.killComponent(IResultListener) will be
	 *  called in turn.
	 * /
	public void killComponent()	throws ComponentTerminatedException;*/

	/**
	 *  Get the component platform.
	 *  @return The component platform.
	 */
	public IServiceContainer getServiceContainer()	throws ComponentTerminatedException;

	/**
	 *  Return the native component-identifier that allows to send
	 *  messages to this component.
	 */
	public IComponentIdentifier getComponentIdentifier() throws ComponentTerminatedException;
	
	/**
	 *  Get the component logger.
	 *  @return The logger.
	 */
	public Logger getLogger();
	
//	/**
//	 *  Get the subcomponents of this component.
//	 *  @return The current subcomponents of this component.
//	 */
//	public IComponentIdentifier[]	getSubcomponents(); 
}

