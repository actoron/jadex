package jadex.bridge.service.component.interceptors;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceInvalidException;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Proxy;

/**
 *  The recovery interceptor is used in required service proxies 
 *  for automatically reassigning a service if it fails due to
 *  one of specific exceptions.
 *  
 *  todo: - fix that the failed service is not found again by search
 *        - which exception should be supported (ComponentTerminatedException, ServiceInvalidException, ServiceNotFoundException?)
 *        - what about multi services, if this case there is no abstraction for
 *          all services so that only single rebinds can be done (not a new set of services)
 */ 
public class RecoveryInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
//	protected IExternalAccess ea;
		
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
	public RecoveryInterceptor(RequiredServiceInfo info, 
		RequiredServiceBinding binding, IRequiredServiceFetcher fetcher)
	{
//		this.ea = ea;
		this.info = info;
		this.binding = binding;
		this.fetcher = fetcher;
	}
	
	//-------- methods --------

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
							if(exception instanceof ComponentTerminatedException 
								|| exception instanceof ServiceInvalidException)
							{
//								System.out.println("exception: "+((IService)sic.getObject()).getServiceIdentifier());
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
	 *  Rebind a service call by using the fetcher to find/create
	 *  another service.
	 */
	public IFuture rebind(final ServiceInvocationContext sic)
	{
//		System.out.println("rebind1: "+Thread.currentThread());
		final Future ret = new Future();
		
		// todo: problem, search delivers failed service as result again
		
		fetcher.getService(info, binding, false)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) 
			{
//				System.out.println("rebind: "+((IService)result).getServiceIdentifier()+Thread.currentThread());
				BasicServiceInvocationHandler handler =  (BasicServiceInvocationHandler)Proxy.getInvocationHandler(result);
				Object rawservice = handler.getService();
				sic.setObject(rawservice);
				sic.invoke().addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
}
