package jadex.bridge;

import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  General interface for components that the container can execute.
 */
public interface IComponentExecutionService
{
	/**
	 *  Test if the execution service can handle the component (or model).
	 *  @param component The component (or its filename).
	 */
	public boolean isResponsible(Object component);
	
	/**
	 *  Create a new component on the platform.
	 *  The component will not run before the {@link startElement()}
	 *  method is called.
	 *  Ensures (in non error case) that the aid of
	 *  the new component is added to the AMS when call returns.
	 *  @param name The component name (null for auto creation)
	 *  @param model The model name.
	 *  @param config The configuration.
	 *  @param args The arguments map (name->value).
	 *  @param listener The result listener (if any).
	 *  @param creator The creator (if any).
	 */
	public void	createComponent(String name, String model, String config, Map args, IResultListener listener, Object creator);
	
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
     *  Remove an ams listener.
     *  @param listener  The listener to be removed.
     */
    public void removeComponentListener(IComponentListener listener);

}
