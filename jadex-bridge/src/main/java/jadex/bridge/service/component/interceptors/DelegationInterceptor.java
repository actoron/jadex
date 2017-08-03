package jadex.bridge.service.component.interceptors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.DefaultServiceFetcher;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Delegates a service call to another service provider.
 *  
 *  Used e.g. for provided services with binding i.e. delegation. 
 */
public class DelegationInterceptor extends AbstractMultiInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	protected static final Map SUBINTERCEPTORS = getInterceptors();
	
	/** The static set of no delegation methods. */
	protected static final Set<Method> NO_DELEGATION;

	
	static
	{
		NO_DELEGATION = new HashSet<Method>();
		try
		{
			NO_DELEGATION.add(INFPropertyProvider.class.getMethod("shutdownNFPropertyProvider", new Class[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
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

	/** The service id of myself (must be excluded to avoid looped invocation of itself). */
	protected IServiceIdentifier sid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DelegationInterceptor(IInternalAccess ia, RequiredServiceInfo info, 
		RequiredServiceBinding binding, IRequiredServiceFetcher fetcher, IServiceIdentifier sid, boolean realtime)
	{
		this.ea = ia.getExternalAccess();
		this.info = info;
		this.binding = binding;
		this.fetcher = fetcher!=null? fetcher: new DefaultServiceFetcher(ia, realtime);
		this.sid = sid;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> doExecute(final ServiceInvocationContext sic) 	
	{
//		System.out.println("Invoked: "+method.getName());
		
		// This works because context has not to be transferred remotely.
		// Ea is the local component and the service is fetched maybe a proxy.
		// The reult listener is executed locally to ea.
		
		if(NO_DELEGATION.contains(sic.getMethod()))
		{
			sic.setResult(IFuture.DONE);
			return IFuture.DONE;
		}
		else
		{
			return ea.scheduleStep(new IComponentStep<Void>()
			{
	//			@XMLClassname("invoc")
				public IFuture<Void> execute(final IInternalAccess ia)
				{
					final Future<Void> ret = new Future<Void>();
					IFuture<IService> fut = fetcher.getService(info, binding, false, new IAsyncFilter<IService>()
					{
						public IFuture<Boolean> filter(IService ser)
						{
							return new Future<Boolean>(!sid.equals(ser.getServiceIdentifier()));
						}
					});
					
					fut.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IService, Void>(ret)
					{
						public void customResultAvailable(IService result) 
						{
							// Note: this will not be executed remotely but on the component 
							sic.setObject(result);
							sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
						}
					}));
					
	//				IIntermediateFuture<IService> fut = fetcher.getServices(info, binding, false);
	//				fut.addResultListener(ia.createResultListener(new IIntermediateResultListener<IService>()
	//				{
	//					boolean fin = false;
	//					public void resultAvailable(Collection<IService> result)
	//					{
	//						for(IService ser: result)
	//						{
	//							intermediateResultAvailable(ser);
	//						}
	//						finished();
	//					}
	//					
	//					public void intermediateResultAvailable(IService result) 
	//					{
	//						// Only set result if not found itself
	//						if(!fin && !sid.equals(result.getServiceIdentifier()))
	//						{
	//							fin = true;
	//							sic.setObject(result);
	//							sic.invoke().addResultListener(new DelegationResultListener(ret));
	//						}
	//					}	
	//					
	//					public void finished()
	//					{
	//						if(!fin)
	//						{
	//							ret.setException(new ServiceNotFoundException());
	//						}
	//					}
	//					
	//					public void exceptionOccurred(Exception exception)
	//					{
	//						ret.setException(exception);
	//					}
	//				}));
	//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
	//				{
	//					public void customResultAvailable(Object result) 
	//					{
	//						// Note: this will not be executed remotely but on the component 
	//						sic.setObject(result);
	//						sic.invoke().addResultListener(new DelegationResultListener(ret));
	//					}
	//				}));
					return ret;
				}
			});
		}
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
					BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(proxy);
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
			ret.put(IInternalService.class.getMethod("setComponentAccess", new Class[]{IInternalAccess.class}), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					context.setResult(IFuture.DONE);
					return IFuture.DONE;
				}
			});
			
			ret.put(Object.class.getMethod("toString", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)ProxyFactory.getInvocationHandler(proxy);
					context.setResult(handler.toString());
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)ProxyFactory.getInvocationHandler(proxy);
					Object[] args = (Object[])context.getArguments().toArray();
					context.setResult(Boolean.valueOf(args[0]!=null && ProxyFactory.isProxyClass(args[0].getClass())
						&& handler.equals(ProxyFactory.getInvocationHandler(args[0]))));
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("hashCode", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = ProxyFactory.getInvocationHandler(proxy);
					context.setResult(Integer.valueOf(handler.hashCode()));
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
