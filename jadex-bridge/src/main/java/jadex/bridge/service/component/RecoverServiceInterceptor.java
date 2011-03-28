package jadex.bridge.service.component;

import java.lang.reflect.Proxy;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 * 
 */
public class RecoverServiceInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;
		
	/** The service info. */
	protected RequiredServiceInfo info;
	
	/** The service binding. */
	protected RequiredServiceBinding binding;
	
	/** The service fetcher. */
	protected IRequiredServiceFetcher fetcher;

	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public RecoverServiceInterceptor(IExternalAccess ea, RequiredServiceInfo info, 
		RequiredServiceBinding binding, IRequiredServiceFetcher fetcher)
	{
		this.ea = ea;
		this.info = info;
		this.binding = binding;
		this.fetcher = fetcher;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture execute(final ServiceInvocationContext sic)
	{
		final Future ret = new Future();
		sic.invoke().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				Object res = sic.getResult();
				if(res instanceof IFuture)
				{
					((IFuture)res).addResultListener(new DelegationResultListener(ret)
					{
						public void exceptionOccurred(Exception exception)
						{
							if(exception instanceof ComponentTerminatedException)
							{
								System.out.println("exception: "+((IService)sic.getObject()).getServiceIdentifier());
								rebind(sic).addResultListener(new DelegationResultListener(ret));
							}
							else
							{
								super.exceptionOccurred(exception);
							}
						}
					});
				}
				else
				{
					ret.setResult(null);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				rebind(sic).addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}

	/**
	 * 
	 */
	public IFuture rebind(final ServiceInvocationContext sic)
	{
		System.out.println("rebind1: "+Thread.currentThread());
		final Future ret = new Future();
		fetcher.getService(info, binding, ea.getServiceProvider(), false)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) 
			{
				System.out.println("rebind: "+((IService)result).getServiceIdentifier()+Thread.currentThread());
				BasicServiceInvocationHandler handler =  (BasicServiceInvocationHandler)Proxy.getInvocationHandler(result);
				Object rawservice = handler.getService();
				sic.setObject(rawservice);
				sic.invoke().addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
}
