package jadex.service;

import java.util.Collection;
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
	public void	selectServices(Map services, Collection results);
	
	/**
	 *  Get the result.
	 *  Called once after search is finished.
	 *  @param results	The collection of selected services.
	 *  @return A single service or a list of services.
	 */
	public Object	getResult(Collection results);
}
