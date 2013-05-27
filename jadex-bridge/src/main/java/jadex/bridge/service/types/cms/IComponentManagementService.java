package jadex.bridge.service.types.cms;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.ParameterInfo;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.Collection;
import java.util.Map;

/**
 *  General interface for components that the container can execute.
 */
public interface IComponentManagementService //extends IService
{
	//-------- management methods --------
	
	/**
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	@ParameterInfo("modelinfo")
	public IFuture<IModelInfo> loadComponentModel(@ParameterInfo("filename") String filename, @ParameterInfo("rid") IResourceIdentifier rid);

	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @param resultlistener The result listener (if any). Will receive the results of the component execution, after the component has terminated.
	 *  @return The id of the component as future result, when the component has been created and initialized.
	 */
	public IFuture<IComponentIdentifier> createComponent(String name, String model, CreationInfo info, 
		IResultListener<Collection<Tuple2<String, Object>>> resultlistener);
		
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param componentid	The component to destroy.
	 */
	public IFuture<Map<String, Object>> destroyComponent(IComponentIdentifier componentid);

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> suspendComponent(IComponentIdentifier componentid);
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(IComponentIdentifier componentid);
	
	/**
	 *  Add a result listener. Also intermediate result listeners can be
	 *  added. In this case results are immediately fed back when set.
	 *  @param listener The result (or intermediate) result listener.
	 */
	public IFuture<Void> addComponentResultListener(IResultListener<Collection<Tuple2<String, Object>>> listener, IComponentIdentifier cid);
	
	/**
	 *  Add a previously added result listener. 
	 *  @param listener The result (or intermediate) result listener.
	 */
	public IFuture<Void> removeComponentResultListener(IResultListener<Collection<Tuple2<String, Object>>> listener, IComponentIdentifier cid);

	
	//-------- debugging methods --------
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public IFuture<Void> stepComponent(IComponentIdentifier componentid);
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier componentid, String[] breakpoints);
	
	//-------- information methods --------
	
	/**
	 *  Get the root identifier (platform).
	 *  @return The root identifier.
	 */
	public IFuture<IComponentIdentifier> getRootIdentifier();
	
	/**
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  This method should be used with caution when the agent population is large.
	 */
	public IFuture<IComponentIdentifier[]> getComponentIdentifiers();
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public IFuture<IComponentDescription> getComponentDescription(IComponentIdentifier cid);
	
	/**
	 *  Get all component descriptions.
	 *  @return The component descriptions of the platform.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions();
	
	/**
	 * Search for components matching the given description.
	 * @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con);

	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con, boolean remote);
	
	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be added.
     */
    public IFuture<Void> addComponentListener(IComponentIdentifier comp, ICMSComponentListener listener);
    
    /**
     *  Remove a listener.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be removed.
     */
    public IFuture<Void> removeComponentListener(IComponentIdentifier comp, ICMSComponentListener listener);

    //-------- external access methods --------
    
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener (recieves an IExternalAccess object).
	 */
	public IFuture<IExternalAccess> getExternalAccess(IComponentIdentifier cid);

	//-------- parent/child component accessors --------
	
	/**
	 *  Get the parent component of a component.
	 *  @param cid The component identifier.
	 *  @return The parent component identifier.
	 *  @deprecated Use cid.getParent() instead
	 */
	public IFuture<IComponentIdentifier> getParent(IComponentIdentifier cid);
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(IComponentIdentifier cid);
//	public IIntermediateFuture<IComponentIdentifier> getChildren(IComponentIdentifier cid);
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public IFuture<IComponentDescription[]> getChildrenDescriptions(IComponentIdentifier cid);
	
	//-------- create methods for cms objects --------
	
	// todo: remove all following methods 
	// (they are synchronous and not necessary, direct constructor creation)
	
//	/**
//	 *  Create component identifier (name assumed being local).
//	 *  @param name The name.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name);
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name, boolean local);
//
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses);
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name, IComponentIdentifier parent, String[] addresses);
//	
//	/**
//	 *  Create a component identifier that is allowed on the platform.
//	 *  @param name The base name.
//	 *  @return The component identifier.
//	 */
//	public IComponentIdentifier generateComponentIdentifier(String name, String platformname);
//	
//	/**
//	 * Create a component description.
//	 * @param id The component identifier.
//	 * @param state The state.
//	 * @param ownership The ownership.
//	 * @param type The component type.
//	 * @param parent The parent.
//	 * @return The component description.
//	 */
//	public IComponentDescription createComponentDescription(IComponentIdentifier id, String state, 
//		String ownership, String type, String modelname, String localtype);
//	
//	/**
//	* Create a search constraints object.
//	* @param maxresults The maximum number of results.
//	* @param maxdepth The maximal search depth.
//	* @return The search constraints.
//	*/
//	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth);
	
	/**
	 *  Get the component adapter for a component identifier.
	 *  @param aid The component identifier.
	 *  @param listener The result listener.
	 */
    // Todo: Hack!!! remove
	@Excluded
	public IFuture<IComponentAdapter> getComponentAdapter(IComponentIdentifier cid);
}
