package jadex.bridge;

import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

/**
 *  General interface for components that the container can execute.
 */
public interface IComponentManagementService
{
	//-------- management methods --------
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param killlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	public IFuture createComponent(String name, String model, CreationInfo info, IResultListener killlistener);
		
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param componentid	The component to destroy.
	 */
	public IFuture destroyComponent(IComponentIdentifier componentid);

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture suspendComponent(IComponentIdentifier componentid);
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture resumeComponent(IComponentIdentifier componentid);
	
	//-------- debugging methods --------
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public IFuture stepComponent(IComponentIdentifier componentid);
	
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
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  
	 *  This method should be used with caution when the agent population is large.
	 */
	public IFuture getComponentIdentifiers();
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public IFuture getComponentDescription(IComponentIdentifier cid);
	
	/**
	 *  Get all component descriptions.
	 *  @return The component descriptions of the platform.
	 */
	public IFuture getComponentDescriptions();
	
	/**
	 * Search for components matching the given description.
	 * @return An array of matching component descriptions.
	 */
	public IFuture searchComponents(IComponentDescription adesc, ISearchConstraints con);

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

    //-------- external access methods --------
    
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener (recieves an IExternalAccess object).
	 */
	public IFuture getExternalAccess(IComponentIdentifier cid);

	//-------- parent/child component accessors --------
	
	/**
	 *  Get the parent component of a component.
	 *  @param cid The component identifier.
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParent(IComponentIdentifier cid);
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IComponentIdentifier[] getChildren(IComponentIdentifier cid);
	
	//-------- create methods for cms objects --------
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name);
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local);

	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses);
	
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier generateComponentIdentifier(String name);
	
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
	
	//-------- methods for component services --------
	
	/**
	 *  Get a component service of a specific type.
	 *  @param type The type.
	 *  @return The service object. 
	 * /
	public IFuture getComponentService(Class type);*/
}
