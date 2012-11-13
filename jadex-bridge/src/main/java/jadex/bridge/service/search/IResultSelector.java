package jadex.bridge.service.search;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *  Select services to be returned as result of service search.
 */
public interface IResultSelector
{
	/**
	 *  Called for each searched service provider node.
	 *  @param services	The provided services (class->list of services).
	 *  @param results	The collection to which results should be added.
	 */
	public IFuture<List<IService>> selectServices(Map<Class<?>, Collection<IService>> services);
	
//	/**
//	 *  Get the result.
//	 *  Called once after search is finished.
//	 *  @param results	The collection of selected services.
//	 *  @return A single service or a list of services.
//	 */
//	public Collection getResult(Collection results);
	
	/**
	 *  Test if the search result is sufficient to stop the search.
	 *  @param results	The collection of selected services.
	 *  @return True, if the search should be stopped.
	 */
	public boolean	isFinished(Collection<IService> results);
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey();
}
