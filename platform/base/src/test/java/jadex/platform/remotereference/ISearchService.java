package jadex.platform.remotereference;

import jadex.commons.future.IFuture;

/**
 *  A service to search for a service.
 */
public interface ISearchService
{
	/**
	 *  Search for a service.
	 */
	IFuture<ILocalService>	searchService(String dummy);
}
