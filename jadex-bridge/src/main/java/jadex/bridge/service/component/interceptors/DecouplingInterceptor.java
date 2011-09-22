package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.JadexCloner;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Invocation interceptor for executing a call on 
 *  the underlying component thread. 
 *  
 *  It checks whether the call can be decoupled (has void or IFuture return type)
 *  and the invoking thread is not already the component thread.
 *  
 *  todo: what about synchronous calls that change the object state.
 *  These calls could damage the service state.
 */
public class DecouplingInterceptor extends AbstractMultiInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	protected static Map SUBINTERCEPTORS = getInterceptors();

	/** The static set of no decoupling methods. */
	protected static Set NO_DECOUPLING;
	
	static
	{
		try
		{
			NO_DECOUPLING = new HashSet();
			NO_DECOUPLING.add(IInternalService.class.getMethod("shutdownService", new Class[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The argument copy allowed flag. */
	protected boolean copy;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingInterceptor(IExternalAccess ea, IComponentAdapter adapter, boolean copy)
	{
		this.ea = ea;
		this.adapter = adapter;
		this.copy = copy;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture doExecute(ServiceInvocationContext sic)
	{
		Future ret = new Future();
		
		// Perform argument copy
		
		// In case of remote call parameters are copied as part of marshalling.
		if(copy && !SServiceProvider.isRemoteObject(sic.getObject()))
		{
			Method method = sic.getMethod();
			boolean[] refs = SServiceProvider.getLocalReferenceInfo(method, !copy);
			
			Object[] args = sic.getArgumentArray();
			List copyargs = new ArrayList(); 
			if(args.length>0)
			{
				for(int i=0; i<args.length; i++)
				{
					boolean ref = refs[i] || SServiceProvider.isLocalReference(args[i]);
					
//					if(!ref && args[i]!=null)
//						System.out.println("copy arg: "+args[i]);
					copyargs.add(ref? args[i]: JadexCloner.deepCloneObject(args[i]));
				}
//				System.out.println("call: "+method.getName()+" "+notcopied+" "+SUtil.arrayToString(method.getParameterTypes()));//+" "+SUtil.arrayToString(args));
				sic.setArguments(copyargs);
			}
		}
		
		// Perform pojo service replacement (for local and remote calls).
		
		List args = sic.getArguments();
		if(args!=null)
		{
			for(int i=0; i<args.size(); i++)
			{
				// Test if it is pojo service impl.
				// Has to be mapped to new proxy then
				Object arg = args.get(i);
				if(arg!=null && !(arg instanceof BasicService) && arg.getClass().isAnnotationPresent(Service.class))
				{
					// Check if the argument type refers to the pojo service
					Service ser = arg.getClass().getAnnotation(Service.class);
					if(SReflect.isSupertype(ser.value(), sic.getMethod().getParameterTypes()[i]))
					{
						Object proxy = BasicServiceInvocationHandler.getPojoServiceProxy(arg);
//						System.out.println("proxy: "+proxy);
						args.set(i, proxy);
					}
				}
			}
		}
		
		// Perform decoupling
		
		boolean scheduleable = sic.getMethod().getReturnType().equals(IFuture.class) 
			|| sic.getMethod().getReturnType().equals(void.class);
		
		if(!adapter.isExternalThread() || !scheduleable || NO_DECOUPLING.contains(sic.getMethod()))
		{
//			if(sic.getMethod().getName().equals("add"))
//				System.out.println("direct: "+Thread.currentThread());
			sic.invoke().addResultListener(new DelegationResultListener(ret));
		}
		else
		{
//			if(sic.getMethod().getName().equals("add"))
//				System.out.println("decouple: "+Thread.currentThread());
			ea.scheduleStep(new InvokeMethodStep(sic)).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
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
	 *  Get the sub interceptors for special cases.
	 */
	public static Map getInterceptors()
	{
		Map ret = new HashMap();
		try
		{
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
	
	//-------- helper classes --------
	
	/**
	 *  Service invocation step.
	 */
	// Not anonymous class to avoid dependency to XML required for XMLClassname
	public static class InvokeMethodStep implements IComponentStep<Void>
	{
		protected ServiceInvocationContext sic;

		public InvokeMethodStep(ServiceInvocationContext sic)
		{
			this.sic = sic;
		}

		public IFuture<Void> execute(IInternalAccess ia)
		{					
			IFuture<Void> ret;
			
			try
			{
//				sic.setObject(service);
				ret	= sic.invoke();
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				ret	= new Future<Void>(e);
			}
			
			return ret;
		}

		public String toString()
		{
			return "invokeMethod("+sic.getMethod()+")";
		}
	}
}
