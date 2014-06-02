package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 *  Component interface to be used (invoked) by platform (adapters).
 *  This interface is implemented by the different kernels.
 *  To create a kernel component instance use the kernel's component factory
 *  @link{IComponentFactory}. 
 */
public interface _IComponentInterpreter
{
	//-------- methods to be called by adapter --------

	/**
	 *  Inform the component that a message has arrived.
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message);

	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con);

	/**
	 *  Request component to cleanup itself after kill.
	 *  The component might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 *  @return When cleanup of the component is finished, the future is notified.
	 */
	public IFuture<Void> cleanupComponent();
	
	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints);
	
	/**
	 *  Start the behavior of a component.
	 *  Called from external thread that needs to be decoupled.
	 */
	public void startBehavior();
	
	/**
	 *  Called before blocking the component thread.
	 */
	public void	beforeBlock();
	
	/**
	 *  Called after unblocking the component thread.
	 */
	public void	afterBlock();
	
	/**
	 *  Get the persistable state.
	 *  Needs to be called on component thread.
	 */
	public Object	getPersistableState();
}
