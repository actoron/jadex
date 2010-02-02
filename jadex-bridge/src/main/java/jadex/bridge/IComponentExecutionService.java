package jadex.bridge;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;

import java.util.Map;

/**
 *  General interface for components that the container can execute.
 */
public interface IComponentExecutionService	extends IService
{
	//-------- management methods --------
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param config The configuration to use for initializing the component (null for default).
	 *  @param args The arguments for the component (if any).
	 *  @param suspend Create the component in suspended mode (i.e. do not run until resum() is called).
	 *  @param listener The result listener (if any). Will receive the id of the component as result.
	 *  @param parent The parent component (if any).
	 */
	public void	createComponent(String name, String model, String config, Map args, boolean suspend, 
		IResultListener listener, IComponentIdentifier parent, IResultListener killlistener);
		
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
	
	//-------- debugging methods --------
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public void stepComponent(IComponentIdentifier componentid, IResultListener listener);
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public void setComponentBreakpoints(IComponentIdentifier componentid, String[] breakpoints);
	
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
	
	/**
	 * Search for agents matching the given description.
	 * @return An array of matching agent descriptions.
	 */
	public void searchComponents(IComponentDescription adesc, ISearchConstraints con, IResultListener listener);

	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be added.
     */
    public void addComponentListener(IComponentIdentifier comp, IComponentListener listener);
    
    /**
     *  Remove a listener.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be removed.
     */
    public void removeComponentListener(IComponentIdentifier comp, IComponentListener listener);

    //-------- internal methods --------
    
	/**
	 *  Get the component adapter for a component identifier.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 * /
    // Todo: Hack!!! remove?
	public void getComponentAdapter(IComponentIdentifier cid, IResultListener listener);*/
		
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener (recieves an IExternalAccess object).
	 */
	public void getExternalAccess(IComponentIdentifier cid, IResultListener listener);

	//-------- create methods for cms objects --------
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses);
	
	/**
	 * Create a component description.
	 * @param id The component identifier.
	 * @param state The state.
	 * @param ownership The ownership.
	 * @param type The component type.
	 * @param parent The parent.
	 * @return The component description.
	 */
	public IComponentDescription createComponentDescription(IComponentIdentifier id, String state, String ownership, String type, IComponentIdentifier parent);
	
	/**
	* Create a search constraints object.
	* @param maxresults The maximum number of results.
	* @param maxdepth The maximal search depth.
	* @return The search constraints.
	*/
	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth);
	
}
