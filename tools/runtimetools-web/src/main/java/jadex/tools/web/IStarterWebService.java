package jadex.tools.web;

import java.util.Collection;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  
 */
@Service
public interface IStarterWebService
{
	/**
	 *  Get all startable component models.
	 *  @return The file names of the component models.
	 */
	public IFuture<Collection<String>> getComponentModels();
	
	/**
	 *  Create a component for a model.
	 */
	public IFuture<IComponentIdentifier> createComponent(ClassInfo model);
}
