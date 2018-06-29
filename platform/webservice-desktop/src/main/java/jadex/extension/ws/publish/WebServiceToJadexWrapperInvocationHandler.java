package jadex.extension.ws.publish;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 *  Proxy invocation handler that maps a web service call to
 *  a Jadex service and waits for the result, which is returned
 *  to the web service caller.
 */
public class WebServiceToJadexWrapperInvocationHandler implements InvocationHandler
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
	public WebServiceToJadexWrapperInvocationHandler(IService service)
	{
		if(service==null)
			throw new IllegalArgumentException("Service must not null.");
		this.service = service;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when web service is called.
	 *  Redirects to Jadex service method and
	 *  synchronously waits for reply.
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
				ret = ((IFuture)ret).get();
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}

}
