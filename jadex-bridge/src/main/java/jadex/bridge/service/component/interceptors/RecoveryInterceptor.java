package jadex.bridge.service.component.interceptors;

import java.lang.reflect.Proxy;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ProxyFactory;
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
	public RecoveryInterceptor(IExternalAccess ea, RequiredServiceInfo info, 
		RequiredServiceBinding binding, IRequiredServiceFetcher fetcher)
	{
		this.ea = ea;
		this.info = info;
		this.binding = binding;
		this.fetcher = fetcher;
	}
	
	//-------- methods --------

	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
//		System.out.println("invoke: "+sic.getMethod());
		
		sic.invoke().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				System.out.println("invoked: "+sic.getMethod().getName());
				
				Object res = sic.getResult();
				if(!ResolveInterceptor.SERVICEMETHODS.contains(sic.getMethod()) && res instanceof IFuture)
				{
//					((IFuture)res).addResultListener(new TimeoutResultListener(10000, ea, new DelegationResultListener(ret)
					((IFuture)res).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							System.out.println("ex: "+exception);
							if(exception instanceof ComponentTerminatedException 
								|| exception instanceof ServiceInvalidException)
							{
//								System.out.println("exception: "+((IService)sic.getObject()).getServiceIdentifier());
								rebind(sic).addResultListener(new DelegationResultListener(ret));
							}
							else
							{
								ret.setResult(null);
//								super.exceptionOccurred(exception);
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
				if(!ResolveInterceptor.SERVICEMETHODS.contains(sic.getMethod()))
				{
					rebind(sic).addResultListener(new DelegationResultListener(ret));
				}
				else
				{
					ret.setException(exception);
				}
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
		
		// for debugging
//		ret.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				System.out.println("rebind res: "+result);
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("rebind ex: "+exception);
//				exception.printStackTrace();
//			}
//		});
		
		// todo: problem, search delivers failed service as result again
		// -> remember sids and use filter for that purpose!
		
		fetcher.getService(info, binding, false, null)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result) 
			{
//				System.out.println("rebind: "+((IService)result).getServiceIdentifier()+Thread.currentThread());
				BasicServiceInvocationHandler handler =  (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(result);
				Object rawservice = handler.getService();
				sic.setObject(rawservice);
				sic.invoke().addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
}
