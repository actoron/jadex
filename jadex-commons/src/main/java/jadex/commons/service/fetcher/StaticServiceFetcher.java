package jadex.commons.service.fetcher;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IntermediateDelegationResultListener;
import jadex.commons.IntermediateFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IRequiredServiceFetcher;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;

/**
 *  This fetcher stores the search result and returns this result. 
 */
public class StaticServiceFetcher implements IRequiredServiceFetcher
{
	//-------- attributes --------
	
	/** The result. */
	protected Object result;
	
	//-------- methods --------
	
	/**
	 *  Get a required service.
	 */
	public IFuture getService(RequiredServiceInfo info, IServiceProvider provider, boolean rebind)
	{
		final Future ret = new Future();
		if(result==null || rebind)
		{
			SServiceProvider.getService(provider, info.getType(), info.getScope())
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					StaticServiceFetcher.this.result = result;
					super.customResultAvailable(result);
				}
			});
		}
		else
		{
			ret.setResult(result);
		}
		return ret;
	}
	
	/**
	 *  Get a required multi service.
	 */
	public IIntermediateFuture getServices(RequiredServiceInfo info, IServiceProvider provider, boolean rebind)
	{
		final IntermediateFuture ret = new IntermediateFuture();
		if(result==null || rebind)
		{
			SServiceProvider.getServices(provider, info.getType(), info.getScope())
				.addResultListener(new IntermediateDelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					StaticServiceFetcher.this.result = result;
					super.customResultAvailable(result);
				}
			});
		}
		else
		{
			ret.setResult(result);
		}
		return ret;
	}
}
