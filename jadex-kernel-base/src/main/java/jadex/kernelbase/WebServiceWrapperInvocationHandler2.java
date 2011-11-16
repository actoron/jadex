package jadex.kernelbase;

import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 
 */
@Service
class WebServiceWrapperInvocationHandler2 implements InvocationHandler
{
	//-------- attributes --------
	
	/** The service. */
	protected IService service;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 *  @mapping The mapping info about the web service to Java.
	 */
	public WebServiceWrapperInvocationHandler2(IService service)
	{
		if(service==null)
			throw new IllegalArgumentException("Service must not null.");
		this.service = service;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a wrapper method is invoked.
	 *  Uses the cms to create a new invocation agent and lets this
	 *  agent call the web service. The result is transferred back
	 *  into the result future of the caller.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		Object ret = null;
		
		try
		{
			Method m = service.getClass().getMethod(method.getName(), method.getParameterTypes());
			ret = m.invoke(service, args);
			if(ret instanceof IFuture)
			{
				ret = ((IFuture)ret).get(new ThreadSuspendable());
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
}
