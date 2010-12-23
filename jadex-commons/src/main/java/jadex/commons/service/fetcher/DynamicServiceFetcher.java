package jadex.commons.service.fetcher;

import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IntermediateFuture;
import jadex.commons.concurrent.DelegationResultListener;
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
	public IFuture getService(RequiredServiceInfo info, IServiceProvider provider)
	{
		return info.isDeclared()?
			SServiceProvider.getDeclaredService(provider, info.getType()):
			SServiceProvider.getService(provider, info.getType(), info.isRemote(), info.isForced());
	}
	
	/**
	 *  Get a required multi service.
	 */
	public IIntermediateFuture getServices(RequiredServiceInfo info, IServiceProvider provider)
	{
		IIntermediateFuture	ret;
		if(info.isDeclared())
		{
			IntermediateFuture	fut	= new IntermediateFuture();
			SServiceProvider.getDeclaredServices(provider, info.getType())
				.addResultListener(new DelegationResultListener(fut));
			ret	= fut;
		}
		else
		{
			ret	= SServiceProvider.getServices(provider, info.getType(), info.isRemote(), info.isForced());
		}
		
		return ret;
	}
}
