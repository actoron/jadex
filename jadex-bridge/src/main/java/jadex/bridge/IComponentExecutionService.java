package jadex.bridge;

import jadex.commons.concurrent.IResultListener;

/**
 *  General interface for components that the container can execute.
 */
public interface IComponentExecutionService
{
	/**
	 *  Create a new component on the platform.
	 *  The component will not run before the {@link startComponent()}
	 *  method is called.
	 *  @param name The component name (null for auto creation).
	 *  @param component The component instance.
	 *  @param listener The result listener (if any). Will receive the id of the component as result.
	 *  @param creator The creator (if any).
	 */
	public void	registerComponent(String name, IComponentInstance component, IResultListener listener, Object creator);
	
	/**
	 *  Start a previously created component on the platform.
	 *  @param componentid The id of the previously created component.
	 */
	public void	startComponent(Object componentid, IResultListener listener);
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param componentid	The component to destroy.
	 */
	public void destroyComponent(Object componentid, IResultListener listener);

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void suspendComponent(Object componentid, IResultListener listener);
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void resumeComponent(Object componentid, IResultListener listener);
	
	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param listener  The listener to be added.
     */
    public void addComponentListener(IComponentListener listener);
    
    /**
     *  Remove a listener.
     *  @param listener  The listener to be removed.
     */
    public void removeComponentListener(IComponentListener listener);

}
