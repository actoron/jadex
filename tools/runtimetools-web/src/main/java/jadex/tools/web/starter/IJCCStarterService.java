package jadex.tools.web.starter;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
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
	 *  Create a component for a model.
	 */
	public IFuture<IComponentIdentifier> createComponent(String model);
}
