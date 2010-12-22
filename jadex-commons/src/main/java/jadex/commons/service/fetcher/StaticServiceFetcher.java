package jadex.commons.service.fetcher;

import jadex.commons.Future;
import jadex.commons.IFuture;
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
	public IFuture getService(RequiredServiceInfo info, IServiceProvider provider)
	{
		final Future ret = new Future();
		if(result==null)
		{
			if(info.isDeclared())
			{
				SServiceProvider.getDeclaredService(provider, info.getType())
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						StaticServiceFetcher.this.result = result;
						super.customResultAvailable(source, result);
					}
				});
			}
			else
			{
				SServiceProvider.getService(provider, info.getType(), info.isRemote(), info.isForced())
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						StaticServiceFetcher.this.result = result;
						super.customResultAvailable(source, result);
					}
				});
			}
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
	public IFuture getServices(RequiredServiceInfo info, IServiceProvider provider)
	{
		final Future ret = new Future();
		if(result==null)
		{
//			System.out.println("static: "+info.getType()+" "+info.isForced());
			if(info.isDeclared())
			{
				SServiceProvider.getDeclaredServices(provider, info.getType())
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						StaticServiceFetcher.this.result = result;
						super.customResultAvailable(source, result);
					}
				});
			}
			else
			{
				SServiceProvider.getServices(provider, info.getType(), info.isRemote(), info.isForced())
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						StaticServiceFetcher.this.result = result;
						super.customResultAvailable(source, result);
					}
				});
			}
		}
		else
		{
			ret.setResult(result);
		}
		return ret;
	}
}
