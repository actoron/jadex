package jadex.bridge;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  Component interface to be used (invoked) by platform (adapters).
 *  This interface is implemented by the different kernels.
 *  To create a kernel component instance use the kernel's component factory
 *  @link{IComponentFactory}. 
 */
public interface IComponentInstance
{
	//-------- methods to be called by adapter --------

	/**
	 *  Can be called on the component thread only.
	 * 
	 *  Main method to perform component execution.
	 *  Whenever this method is called, the component performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for components
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeStep();

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the component that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message);

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the component that a stream has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con);

	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param desc	The newly created component.
	 *  @param model	The model of the newly created component.
	 *  @return A future to indicate when the event has been processed.
	 */
	// todo: Use parent->child creation config information instead of model (e.g. role in AGR!?)
	public IFuture<Void>	componentCreated(IComponentDescription desc, IModelInfo model);

	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param desc	The destroyed component.
	 *  @return A future to indicate when the event has been processed.
	 */
	public IFuture<Void>	componentDestroyed(IComponentDescription desc);

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *   
	 *  Request component to cleanup itself after kill.
	 *  The component might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 *  Can be called concurrently (also during executeAction()).
	 *  @return When cleanup of the component is finished, the future is notified.
	 */
	public IFuture<Void> cleanupComponent();
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this component.
	 *  External access objects must implement the IExternalAccess interface. 
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @return External access is delivered via future.
	 */
	public IExternalAccess getExternalAccess();

	/**
	 *  Get the class loader of the component.
	 *  The component class loader is required to avoid incompatible class issues,
	 *  when changing the platform class loader while components are running. 
	 *  This may occur e.g. when decoding messages and instantiating parameter values.
	 *  @return	The component class loader. 
	 */
	public ClassLoader getClassLoader();
	
	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchronous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	public Map<String, Object> getResults();
	
	/**
	 *  Get a property of the component.
	 *  May only be called after the component's init has finished.
	 *  @param name	The property name.
	 *  @return	The property value
	 */
	public Object	getProperty(String name);
	
	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints);
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer();
	
	/**
	 *  Start the behavior of a component.
	 *  Called from external thread that needs to be decoupled.
	 */
	public void startBehavior();
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread();
	
	/**
	 *  Called before blocking the component thread.
	 */
	public void	beforeBlock();
	
	/**
	 *  Called after unblocking the component thread.
	 */
	public void	afterBlock();
	
	public IFuture<IPersistInfo> getPersistableState();

}
