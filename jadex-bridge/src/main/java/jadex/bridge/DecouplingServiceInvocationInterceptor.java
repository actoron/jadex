package jadex.bridge;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IInternalService;
import jadex.commons.service.IServiceIdentifier;

import java.lang.reflect.Proxy;

/**
 *  Invocvation interceptor for executing a call on 
 *  the underlying component thread. 
 */
public class DecouplingServiceInvocationInterceptor implements IServiceInvocationInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The service. */
	protected Object service;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingServiceInvocationInterceptor(IExternalAccess ea, IComponentAdapter adapter, Object service)
	{
		this.ea = ea;
		this.adapter = adapter;
		this.service = service;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public void execute(final ServiceInvocationContext sic) 	
	{
		Object ret;
		Class returntype = sic.getMethod().getReturnType();
		boolean scheduleable = returntype.equals(IFuture.class) || returntype.equals(void.class);
		boolean directcall = true;
		
		if(!adapter.isExternalThread() || (!scheduleable && directcall))
		{
			try
			{
				ret = sic.getMethod().invoke(service, sic.getArguments());
			}
			catch(Exception e)
			{
				if(returntype.equals(IFuture.class))
				{
					Future fut = new Future();
					fut.setException(e);
					ret = fut;
				}
				else
				{
					throw new RuntimeException(e);
				}
			}
		}
		else
		{
			final Future future = new Future();
			
			IFuture resfut = ea.scheduleResultStep(new IResultCommand()
			{
				public Object execute(Object args)
				{
					final Future fut = new Future();
					
					try
					{
						Object res = sic.getMethod().invoke(service, sic.getArguments());
						if(res instanceof IFuture)
						{
							((IFuture)res).addResultListener(new DelegationResultListener(fut));
						}
						else
						{
							// Not correct when not null but some other value.
							fut.setResult(res);
						}
					}
					catch(Exception e)
					{
						fut.setException(e);
					}
					
					return fut;
				}
			});
			
			if(scheduleable)
			{
				ret = future;
				resfut.addResultListener(new DelegationResultListener(future));
			}
			else
			{
				System.out.println("Warning, blocking call: "+sic.getMethod());
				ret = resfut.get(new ThreadSuspendable());
//				ret = new Future(null);
			}
		}
		
		sic.setResult(ret);
	}
	
	/**
	 *  Static method for creating a service proxy.
	 */
	public static IInternalService createServiceProxy(IExternalAccess ea, IComponentAdapter adapter, IInternalService service)
	{
		IServiceIdentifier sid = service.getServiceIdentifier();
		return (IInternalService)Proxy.newProxyInstance(ea.getModel().getClassLoader(), new Class[]{IInternalService.class, sid.getServiceType()}, 
			new BasicServiceInvocationHandler(sid, null, new DecouplingServiceInvocationInterceptor(ea, adapter, service)));
	}
}
