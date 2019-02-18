package jadex.tools.web;

import java.util.Collection;

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
	 */
	public IFuture<Collection<String>> getComponentModels();
}
