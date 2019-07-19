package jadex.tools.web.starter;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
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
	public IFuture<Collection<String[]>> getComponentModels(IComponentIdentifier cid);
	
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
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions(IComponentIdentifier cid);
	
	/**
	 * Get a default icon for a file type.
	 */
	public IFuture<byte[]> loadComponentIcon(String type, IComponentIdentifier cid);
	
	/**
	 *  Subscribe to component events
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> subscribeToComponentChanges(IComponentIdentifier cid);
}
