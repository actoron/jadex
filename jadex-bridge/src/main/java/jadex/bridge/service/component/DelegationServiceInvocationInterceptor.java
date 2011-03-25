package jadex.bridge.service.component;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *  Delegates a service call to another service provider.
 *  
 *  Used e.g. for provided services with binding i.e. delegation. 
 */
public class DelegationServiceInvocationInterceptor extends AbstractMultiInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	protected static Map SUBINTERCEPTORS = getInterceptors();
	
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
	public DelegationServiceInvocationInterceptor(IExternalAccess ea, RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		this.ea = ea;
		this.info = info;
		this.binding = binding;
		this.fetcher = new DefaultServiceFetcher();
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture doExecute(final ServiceInvocationContext sic) 	
	{
		Future ret = new Future(); 
//		System.out.println("Invoked: "+method.getName());
		
		// This works because context has not to be transferred remotely.
		// Ea is the local component and the service is fetched maybe a proxy.
		// The reult listener is executed locally to ea.
		ea.scheduleStep(new IComponentStep()
		{
//			@XMLClassname("invoc")
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				fetcher.getService(info, binding, ia.getServiceProvider(), false)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result) 
					{
						// Note: this will not be executed remotely but on the component 
						sic.setObject(result);
						sic.invoke().addResultListener(new DelegationResultListener(ret)
						{
							public void exceptionOccurred(Exception exception)
							{
//								if(binding.isRecover())
								{
									System.out.println("recover");
									super.exceptionOccurred(exception);
								}
							}
						});
					}
				}));
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}

	/**
	 *  Get the ea.
	 *  @return the ea.
	 */
	public IExternalAccess getExternalAccess()
	{
		return ea;
	}
	
	/**
	 *  Get a sub interceptor for special cases.
	 *  @param sic The context.
	 *  @return The interceptor (if any).
	 */
	public IServiceInvocationInterceptor getInterceptor(ServiceInvocationContext sic)
	{
		return (IServiceInvocationInterceptor)SUBINTERCEPTORS.get(sic.getMethod());
	}

	/**
	 *  Get the standard interceptors for composite service proxies.
	 */
	public static Map getInterceptors()
	{
		Map ret = new HashMap();
		try
		{
			ret.put(IInternalService.class.getMethod("getServiceIdentifier", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(proxy);
					context.setResult(handler.getServiceIdentifier());
					return IFuture.DONE;
				}
			});
			
			// todo: implement methods?!
			ret.put(IInternalService.class.getMethod("getPropertyMap", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					return IFuture.DONE;
				}
			});
			ret.put(IInternalService.class.getMethod("signalStarted", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					context.setResult(IFuture.DONE);
					return IFuture.DONE;
				}
			});
			ret.put(IInternalService.class.getMethod("startService", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					context.setResult(IFuture.DONE);
					return IFuture.DONE;
				}
			});
			ret.put(IInternalService.class.getMethod("shutdownService", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					context.setResult(IFuture.DONE);
					return IFuture.DONE;
				}
			});
			ret.put(IInternalService.class.getMethod("isValid", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					context.setResult(true);
					return IFuture.DONE;
				}
			});
			
			ret.put(Object.class.getMethod("toString", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					context.setResult(handler.toString());
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					Object[] args = (Object[])context.getArguments().toArray();
					context.setResult(new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
						&& handler.equals(Proxy.getInvocationHandler(args[0]))));
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("hashCode", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = Proxy.getInvocationHandler(proxy);
					context.setResult(new Integer(handler.hashCode()));
					return IFuture.DONE;
				}
			});
			// todo: other object methods?!
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
}
