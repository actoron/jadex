package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Invocation interceptor for executing a call on 
 *  the underlying component thread. 
 */
public class DecouplingServiceInvocationInterceptor implements IServiceInvocationInterceptor
{
	protected static Map SUBINTERCEPTORS = getInterceptors();
	
	protected static Set DEFAULT_NA;
	
	static
	{
		try
		{
			DEFAULT_NA = new HashSet();
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
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The service. */
	protected Object service;
	
	/** The set of non-applicable methods. */
	protected Set na;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingServiceInvocationInterceptor(IExternalAccess ea, IComponentAdapter adapter)
	{
		this(ea, adapter, null);
	}
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingServiceInvocationInterceptor(IExternalAccess ea, IComponentAdapter adapter, Object service)
	{
		this(ea, adapter, service, null);
	}
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingServiceInvocationInterceptor(IExternalAccess ea, IComponentAdapter adapter, Object service, Set na)
	{
		this.ea = ea;
		this.adapter = adapter;
		this.service = service;
		this.na = na!=null? na: DEFAULT_NA;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture execute(final ServiceInvocationContext sic) 	
	{
		IServiceInvocationInterceptor subic = (IServiceInvocationInterceptor)SUBINTERCEPTORS.get(sic.getMethod());
		if(subic!=null)
		{
			return subic.execute(sic);
		}
		else
		{
			Future done = new Future();
			
			Class returntype = sic.getMethod().getReturnType();
			boolean scheduleable = returntype.equals(IFuture.class) || returntype.equals(void.class);
			boolean directcall = true;
			
			if(service!=null)
				sic.setObject(service);
	
			if(!adapter.isExternalThread() || (!scheduleable && directcall))
			{
				sic.invoke().addResultListener(new DelegationResultListener(done));
			}
			else
			{
				ea.scheduleStep(new InvokeMethodStep(sic)).addResultListener(new DelegationResultListener(done));
			}
			
			return done;
		}
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		return true;//!na.contains(context.getMethod());
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
					Object proxy = context.getObject();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					context.setResult(handler.toString());
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new AbstractApplicableInterceptor()
			{
				public IFuture execute(ServiceInvocationContext context)
				{
					Object proxy = context.getObject();
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
					Object proxy = context.getObject();
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
	
//	/**
//	 *  Static method for creating a service proxy.
//	 */
//	public static IInternalService createServiceProxy(IExternalAccess ea, IComponentAdapter adapter, IInternalService service)
//	{
//		IServiceIdentifier sid = service.getServiceIdentifier();
//		return (IInternalService)Proxy.newProxyInstance(ea.getModel().getClassLoader(), new Class[]{IInternalService.class, sid.getServiceType()}, 
//			new BasicServiceInvocationHandler(sid, getInterceptors(), new DecouplingServiceInvocationInterceptor(ea, adapter, service)));
//	}
	
	//-------- helper classes --------
	
	/**
	 *  Service invocation step.
	 */
	// Not anonymous class to avoid dependency to XML required for XMLClassname
	public static class InvokeMethodStep implements IComponentStep
	{
		protected ServiceInvocationContext sic;
//		protected Object service;

		public InvokeMethodStep(ServiceInvocationContext sic)//, Object service)
		{
			this.sic = sic;
//			this.service = service;
		}

		public Object execute(IInternalAccess ia)
		{					
			final Future fut = new Future();
			
			try
			{
//				sic.setObject(service);
				sic.invoke().addResultListener(new DelegationResultListener(fut));
				
//				Object res = sic.getMethod().invoke(service, sic.getArgumentArray());
//				
//				if(res instanceof IFuture)
//				{
//					((IFuture)res).addResultListener(new DelegationResultListener(fut)
//					{
//						public void customResultAvailable(Object result)
//						{
//							sic.setResult(result);
//							super.customResultAvailable(result);
//						}
//					});
//				}
//				else
//				{
//					// Not correct when not null but some other value.
//					fut.setResult(res);
//					sic.setResult(res);
//				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fut.setException(e);
			}
			
			return fut;
		}

		public String toString()
		{
			return "invokeMethod("+sic.getMethod()+")";
		}
	}
}
