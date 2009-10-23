package jadex.bridge;

import jadex.commons.concurrent.IResultListener;

/**
 *  General interface for components that the container can execute.
 */
public interface IComponentExecutionService
{
	//-------- management methods --------
	
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
	public void	startComponent(IComponentIdentifier componentid, IResultListener listener);
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param componentid	The component to destroy.
	 */
	public void destroyComponent(IComponentIdentifier componentid, IResultListener listener);

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void suspendComponent(IComponentIdentifier componentid, IResultListener listener);
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void resumeComponent(IComponentIdentifier componentid, IResultListener listener);
	
	//-------- information methods --------
	
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public void getComponentIdentifiers(IResultListener listener);
	
	/**
	 *  Get the agent description of a single agent.
	 *  @param cid The agent identifier.
	 *  @return The agent description of this agent.
	 */
	public void getComponentDescription(IComponentIdentifier cid, IResultListener listener);
	
	/**
	 *  Get all agent descriptions.
	 *  @return The agent descriptions of this agent.
	 */
	public void getComponentDescriptions(IResultListener listener);
	
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

    //-------- internal methods --------
    
	/**
	 *  Get the component adapter for a component identifier.
	 *  @param aid The component identifier.
	 *  @param listener The result listener.
	 */
    // Todo: Hack!!! remove?
	public void getComponentAdapter(IComponentIdentifier cid, IResultListener listener);
	
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	public void getExternalAccess(IComponentIdentifier cid, IResultListener listener);

	//-------- create methods for cms objects --------
	
	/**
	 *  Create a component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @return The new agent identifier.
	 */
	public IComponentIdentifier createAgentIdentifier(String name, boolean local);
}
