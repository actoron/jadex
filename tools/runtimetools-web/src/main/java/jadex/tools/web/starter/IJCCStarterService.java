package jadex.tools.web.starter;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;
import jadex.tools.web.jcc.IJCCPluginService;

/**
 *  Interface for the starter plugin service.
 */
@Service
public interface IJCCStarterService extends IJCCPluginService
{
	/**
	 *  Get all startable component models.
	 *  @return The file names of the component models.
	 */
	public IFuture<Collection<String>> getComponentModels();
	
	/**
	 *  Load a component model.
	 *  @param filename The filename.
	 *  @return The component model.
	 */
	public IFuture<IModelInfo> loadComponentModel(String filename);
	
	/**
	 *  Create a component for a filename.
	 *  @param filename The filename.
	 *  @return The component id.
	 * /
	public IFuture<IComponentIdentifier> createComponent(String filename);*/
	
	/**
	 *  Create a component for a filename.
	 *  @param ci The creation info.
	 *  @return The component id.
	 */
	public IFuture<IComponentIdentifier> createComponent(CreationInfo ci);
}
