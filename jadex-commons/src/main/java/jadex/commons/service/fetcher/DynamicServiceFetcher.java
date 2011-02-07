package jadex.commons.service.fetcher;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.service.IRequiredServiceFetcher;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;

/**
 *  The dynamic fetcher always performs a fresh search.
 */
public class DynamicServiceFetcher implements IRequiredServiceFetcher
{
	//-------- constants --------
	
	/** The static fetcher instance. */
	public static DynamicServiceFetcher INSTANCE = new DynamicServiceFetcher();
	
	//-------- methods --------
	
	/**
	 *  Get a required service.
	 */
	public IFuture getService(RequiredServiceInfo info, IServiceProvider provider, boolean rebind)
	{
		return SServiceProvider.getService(provider, info.getType(), info.getScope());
	}
	
	/**
	 *  Get a required multi service.
	 */
	public IIntermediateFuture getServices(RequiredServiceInfo info, IServiceProvider provider, boolean rebind)
	{
		return SServiceProvider.getServices(provider, info.getType(), info.getScope());
	}
}
