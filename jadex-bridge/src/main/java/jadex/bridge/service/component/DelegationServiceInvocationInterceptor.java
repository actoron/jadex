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

import java.util.HashSet;
import java.util.Set;

/**
 *  Delegates a service call to another service provider.
 */
public class DelegationServiceInvocationInterceptor implements IServiceInvocationInterceptor
{
	protected static Set DEFAULT_NA;
	
	static
	{
		try
		{
			DEFAULT_NA = new HashSet();
			DEFAULT_NA.add(IInternalService.class.getMethod("getServiceIdentifier", new Class[0]));
			DEFAULT_NA.add(IInternalService.class.getMethod("getPropertyMap", new Class[0]));
			DEFAULT_NA.add(IInternalService.class.getMethod("signalStarted", new Class[0]));
			DEFAULT_NA.add(IInternalService.class.getMethod("startService", new Class[0]));
			DEFAULT_NA.add(IInternalService.class.getMethod("shutdownService", new Class[0]));
			DEFAULT_NA.add(IInternalService.class.getMethod("isValid", new Class[0]));
			DEFAULT_NA.add(Object.class.getMethod("toString", new Class[0]));
			DEFAULT_NA.add(Object.class.getMethod("equals", new Class[]{Object.class}));
			DEFAULT_NA.add(Object.class.getMethod("hashCode", new Class[0]));
		}
		catch(Exception e)
		{
			// cannot happen
		}
	}
	
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;
		
	/** The service info. */
	protected RequiredServiceInfo info;
	
	/** The service binding. */
	protected RequiredServiceBinding binding;
	
	/** The service fetcher. */
	protected IRequiredServiceFetcher fetcher;

	/** The set of non-applicable methods. */
	protected Set na;
	
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
	public IFuture execute(final ServiceInvocationContext sic) 	
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
						// todo? allow remote execution of the interceptor chain?
//						sic.invoke();
						
						try
						{
							Object res = sic.getMethod().invoke(result, sic.getArgumentArray());
							sic.setResult(res);
							if(res instanceof IFuture)
							{
								((IFuture)res).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
							}
							else
							{
								ret.setResult(res);
							}
						}
						catch(Exception e)
						{
							ret.setException(e);
						}
					}
				}));
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
//		sic.setResult(ret);
	}

	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		return !na.contains(context.getMethod());
	}
	
	/**
	 *  Get the ea.
	 *  @return the ea.
	 */
	public IExternalAccess getExternalAccess()
	{
		return ea;
	}

//	/**
//	 *  Get the standard interceptors for composite service proxies.
//	 */
//	public static MultiCollection getInterceptors()
//	{
//		MultiCollection ret = new MultiCollection();
//		try
//		{
//			ret.put(IInternalService.class.getMethod("getServiceIdentifier", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					Object proxy = context.getObject();
//					BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(proxy);
//					context.setResult(handler.getServiceIdentifier());
//					return IFuture.DONE;
//				}
//			});
//			
//			// todo: implement methods?!
//			ret.put(IInternalService.class.getMethod("getPropertyMap", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					return IFuture.DONE;
//				}
//			});
//			ret.put(IInternalService.class.getMethod("signalStarted", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					context.setResult(IFuture.DONE);
//					return IFuture.DONE;
//				}
//			});
//			ret.put(IInternalService.class.getMethod("startService", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					context.setResult(IFuture.DONE);
//					return IFuture.DONE;
//				}
//			});
//			ret.put(IInternalService.class.getMethod("shutdownService", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					context.setResult(IFuture.DONE);
//					return IFuture.DONE;
//				}
//			});
//			ret.put(IInternalService.class.getMethod("isValid", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					context.setResult(true);
//					return IFuture.DONE;
//				}
//			});
//			
//			ret.put(Object.class.getMethod("toString", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					Object proxy = context.getObject();
//					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
//					context.setResult(handler.toString());
//					return IFuture.DONE;
//				}
//			});
//			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					Object proxy = context.getObject();
//					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
//					Object[] args = (Object[])context.getArguments().toArray();
//					context.setResult(new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
//						&& handler.equals(Proxy.getInvocationHandler(args[0]))));
//					return IFuture.DONE;
//				}
//			});
//			ret.put(Object.class.getMethod("hashCode", new Class[0]), new IServiceInvocationInterceptor()
//			{
//				public IFuture execute(ServiceInvocationContext context)
//				{
//					Object proxy = context.getObject();
//					InvocationHandler handler = Proxy.getInvocationHandler(proxy);
//					context.setResult(new Integer(handler.hashCode()));
//					return IFuture.DONE;
//				}
//			});
//			// todo: other object methods?!
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Create a new composite (application) service proxy.
//	 */
//	public static IInternalService createServiceProxy(IExternalAccess ea, RequiredServiceInfo info, RequiredServiceBinding binding, ClassLoader classloader)
//	{
//		return (IInternalService)Proxy.newProxyInstance(classloader, new Class[]{IInternalService.class, info.getType()}, 
//			new BasicServiceInvocationHandler(BasicService.createServiceIdentifier(ea.getServiceProvider().getId(), info.getType(), BasicServiceInvocationHandler.class), 
//			DelegationServiceInvocationInterceptor.getInterceptors(), 
//			new DelegationServiceInvocationInterceptor(ea, info, binding)));
//	}
}
