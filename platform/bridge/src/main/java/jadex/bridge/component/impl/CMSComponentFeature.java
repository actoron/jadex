package jadex.bridge.component.impl;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.ICMSFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;

/**
 *
 */
public class CMSComponentFeature extends AbstractComponentFeature implements ICMSFeature
{
	/** Flag to enable unique id generation. */
	// Todo: move to platform data ?
	protected boolean uniqueids;
	
	/**
	 *  Create the feature.
	 */
	public CMSComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		uniqueids = true; // fetch from args/config?
	}
	
	/**
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	@SuppressWarnings("unchecked")
	public IFuture<IModelInfo> loadComponentModel(final String filename, final IResourceIdentifier rid)
	{
		return SComponentManagementService.loadComponentModel(filename, rid, getComponent());
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(String model, CreationInfo info)
	{
		return SComponentManagementService.createComponent(null, model, info, getComponent());
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(String name, final String model, CreationInfo info)
	{
		return SComponentManagementService.createComponent(name, model, info, getComponent());
	}
	
	/**
	 *  Create a new component on the platform.
	 *  This method allows for retrieving intermediate results of the component via
	 *  status events.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The status events of the components. Consists of CMSCreatedEvent, (CMSIntermediateResultEvent)*, CMSTerminatedEvent
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponent(CreationInfo info, String name, String model)
	{		
		return SComponentManagementService.createComponent(info, name, model, getComponent());
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info	The creation info, if any.
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param resultlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	public IFuture<IComponentIdentifier> createComponent(final String oname, final String modelname, CreationInfo info, 
		final IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{			
		return SComponentManagementService.createComponent(oname, modelname, info, resultlistener, getComponent());
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid)
	{
		return SComponentManagementService.destroyComponent(cid, getComponent());
	}

	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(IComponentIdentifier cid)
	{
		return SComponentManagementService.resumeComponent(cid, false, getComponent());
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(final IComponentIdentifier cid, final boolean initresume)
	{
		return SComponentManagementService.resumeComponent(cid, initresume, getComponent());
	}
	
	/**
     *  Add a component listener for all components.
     *  The listener is registered for component changes.
     */
    public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToAll()
    {
    	return SComponentManagementService.listenToAll(getComponent());
    }
    
	/**
     *  Add a component listener for a specific component.
     *  The listener is registered for component changes.
     *  @param cid	The component to be listened.
     */
    public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(final IComponentIdentifier cid)
    {
    	return SComponentManagementService.listenToComponent(cid, getComponent());
    }

	//-------- internal methods --------
	
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	public IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getExternalAccess(cid, getComponent());
	}
	
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	protected IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier cid, boolean internal)
	{
		return SComponentManagementService.getExternalAccess(cid, internal, getComponent());
	}
	
	/**
	 *  Get the parent component of a component.
	 *  @param platform The component identifier.
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParentIdentifier(CreationInfo ci)
	{
		return SComponentManagementService.getParentIdentifier(ci, getComponent());
	}
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getChildren(cid, getComponent());
	}

	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public IFuture<IComponentDescription[]> getChildrenDescriptions(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getChildrenDescriptions(cid, getComponent());
	}
	
	//--------- information methods --------
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public IFuture<IComponentDescription> getComponentDescription(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getComponentDescription(cid, getComponent());
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions()
	{
		return SComponentManagementService.getComponentDescriptions(getComponent());
	}
	
	/**
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  
	 *  This method should be used with caution when the agent population is large. <- TODO and the reason is...?
	 */
	public IFuture<IComponentIdentifier[]> getComponentIdentifiers()
	{
		return SComponentManagementService.getComponentIdentifiers(getComponent());
	}
	
	/**
	 *  Get the root identifier (platform).
	 *  @return The root identifier.
	 */
	public IFuture<IComponentIdentifier> getRootIdentifier()
	{
		return new Future<IComponentIdentifier>(getComponent().getId());
	}
	
}
