package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 *  Basic service invocation interceptor.
 *  It has a multi collection of interceptors per method.
 *  Executes the list of interceptors one by one.
 *  In case no handler can be found a fallback handler is used.
 */
public class BasicServiceInvocationHandler implements InvocationHandler
{
	//-------- attributes --------

	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The service. */
	protected IService service;

	
//	/** The map of own methods. */
//	protected MultiCollection interceptors;
	
	/** The list of interceptors. */
	protected List interceptors;
	
//	/** The fallback invocation interceptor. */
//	protected IServiceInvocationInterceptor fallback;

	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public BasicServiceInvocationHandler(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
	
	/**
	 *  Create a new invocation handler.
	 */
	public BasicServiceInvocationHandler(IService service)
	{
		this.service = service;
		this.sid = service.getServiceIdentifier();
	}
	
//	/**
//	 *  Create a new invocation handler.
//	 */
//	public BasicServiceInvocationHandler(IServiceIdentifier sid, List interceptors)//, IServiceInvocationInterceptor fallback)
//	{
//		this.sid = sid;
//		this.interceptors = interceptors;
////		this.fallback = fallback;
//	}
	
	//-------- methods --------
	
	/**
	 *  A proxy method has been invoked.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		Object ret = null;

		final ServiceInvocationContext sic = new ServiceInvocationContext(getInterceptors());
		
		List myargs = args!=null? SUtil.arrayToList(args): null;
		
		if(SReflect.isSupertype(IFuture.class, method.getReturnType()))
		{
			final Future fut = new Future();
			ret = fut;
			sic.invoke(service, method, myargs)
				.addResultListener(new DelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					if(sic.getResult() instanceof IFuture)
					{
						((IFuture)sic.getResult()).addResultListener(new DelegationResultListener(fut));
					}
					else
					{
						fut.setResult(sic.getResult());
					}
				}
			});
		}
		else if(method.getReturnType().equals(void.class))
		{
			sic.invoke(service, method, myargs);
		}
		else
		{
			IFuture fut = sic.invoke(service, method, myargs);
			if(fut.isDone())
			{
				ret = sic.getResult();
			}
			else
			{
				System.out.println("Warning, blocking call: "+method.getName()+" "+sid);
				ret = fut.get(new ThreadSuspendable());
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the sid.
	 *  @return the sid.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}
	
	/**
	 *  Add an interceptor.
	 *  
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized void addFirstServiceInterceptor(IServiceInvocationInterceptor interceptor)
	{
		if(interceptors==null)
			interceptors = new ArrayList();
		interceptors.add(0, interceptor);
	}
	
	/**
	 *  Add an interceptor.
	 *  
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized void addServiceInterceptor(IServiceInvocationInterceptor interceptor, int pos)
	{
		if(interceptors==null)
			interceptors = new ArrayList();
		interceptors.add(pos, interceptor);
	}
	
	/**
	 *  Remove an interceptor.
	 *  
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized void addServiceInterceptor(int pos)
	{
		if(interceptors!=null)
			interceptors.remove(pos);
	}
	
	/**
	 *  Get interceptors.
	 *  
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized IServiceInvocationInterceptor[] getInterceptors()
	{
		return interceptors==null || interceptors.size()==0? null://new IServiceInvocationInterceptor[]{fallback}: 
			(IServiceInvocationInterceptor[])interceptors.toArray(new IServiceInvocationInterceptor[interceptors.size()]);
	}
	
	/**
	 *  Static method for creating a service proxy.
	 */
	public static IInternalService createServiceProxy(IExternalAccess ea, IComponentAdapter adapter, IInternalService service)
	{
//		IServiceIdentifier sid = service.getServiceIdentifier();
		BasicServiceInvocationHandler handler = new BasicServiceInvocationHandler(service);
		handler.addFirstServiceInterceptor(new MethodInvocationInterceptor());
//		System.out.println("create: "+service.getServiceIdentifier().getServiceType());
		return (IInternalService)Proxy.newProxyInstance(ea.getModel().getClassLoader(), new Class[]{IInternalService.class, service.getServiceIdentifier().getServiceType()}, handler); 
	}
	
	/**
	 *  Static method for creating a service proxy.
	 */
	public static IInternalService createServiceProxy(IExternalAccess ea, IComponentAdapter adapter, IServiceIdentifier sid)
	{
		BasicServiceInvocationHandler handler = new BasicServiceInvocationHandler(sid);
		return (IInternalService)Proxy.newProxyInstance(ea.getModel().getClassLoader(), new Class[]{IInternalService.class, sid.getServiceType()}, handler); 
	}

}
