package jadex.bdi.examples.disastermanagement.commander2;

import jadex.bridge.service.IService;
import jadex.bridge.service.search.IResultSelector;
import jadex.commons.future.IFuture;

import java.util.Collection;
import java.util.Map;

/**
 *  Select a fire brigade.
 */
public class BrigadeSelector implements IResultSelector
{
	/**
	 *  Called for each searched service provider node.
	 *  @param services	The provided services (class->list of services).
	 *  @param results	The collection to which results should be added.
	 */
	public IFuture<Collection<IService>> selectServices(Map<Class<?>, Collection<IService>> services)// context
	{
		// IGoal	goal	= context.get("$goal");
		return null;
	}
	
	/**
	 *  Test if the search result is sufficient to stop the search.
	 *  @param results	The collection of selected services.
	 *  @return True, if the search should be stopped.
	 */
	public boolean	isFinished(Collection<IService> results)
	{
		return false;
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return null;
	}
}
