package jadex.tools.web.starter;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.tools.web.jcc.IJCCPluginService;

/**
 *  Interface for the starter plugin service.
 *  
 *  Note: cid needs to be always last parameter. It is used to remote 
 *  control another platform using a webjcc plugin on the gateway.
 */
@Service(system=true)
public interface IJCCStarterService extends IJCCPluginService
{
	/**
	 *  Get all startable component models.
	 *  @return The filenames and classnames of the component models.
	 */
	//public IFuture<Collection<String[]>> getComponentModels();
	//public IFuture<Collection<String[]>> getComponentModels(IComponentIdentifier cid);
	
	//public ISubscriptionIntermediateFuture<String[]> getComponentModelsAsStream(IComponentIdentifier cid);
	public ISubscriptionIntermediateFuture<Collection<String[]>> getComponentModelsAsStream(IComponentIdentifier cid);
	
	/**
	 *  Load a component model.
	 *  @param filename The filename.
	 *  @return The component model.
	 */
	public IFuture<IModelInfo> loadComponentModel(String filename, IComponentIdentifier cid);
	
	/**
	 *  Create a component for a filename.
	 *  @param ci The creation info.
	 *  @return The component id.
	 */
	public IFuture<IComponentIdentifier> createComponent(CreationInfo ci, IComponentIdentifier cid);
	
	/**
	 *  Kill a component.
	 *  @param id The component id.
	 *  @return The component id.
	 */
	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier id, IComponentIdentifier cid);
		
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions(IComponentIdentifier cid);
	
	/**
	 *  Get the child component descriptions.
	 *  @param parent The component id of the parent.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getChildComponentDescriptions(IComponentIdentifier cid, IComponentIdentifier parent);
	
	/**
	 *  Get the component description.
	 *  @return The component description.
	 */
	public IFuture<IComponentDescription> getComponentDescription(IComponentIdentifier cid);
	
	/**
	 * Get a default icon for a file type.
	 */
	public IFuture<byte[]> loadComponentIcon(String type, IComponentIdentifier cid);
	
	/**
	 *  Subscribe to component events
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> subscribeToComponentChanges(IComponentIdentifier cid);
	
	/**
	 *  Get infos about services (provided, required).
	 *  @param cid The component id
	 */
	public IFuture<Object[]> getServiceInfos(IComponentIdentifier cid);
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param cid The component id.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos(IComponentIdentifier cid, IServiceIdentifier sid, MethodInfo mi, Boolean req);
	
	/**
	 *  Returns the values about a non-functional property of this service.
	 *  @param cid The component id.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, Object>> getNFPropertyValues(IComponentIdentifier cid, IServiceIdentifier sid, MethodInfo mi, Boolean req, String name);

}
