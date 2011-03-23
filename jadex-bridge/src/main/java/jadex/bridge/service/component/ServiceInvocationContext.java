package jadex.bridge.service.component;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;

/**
 *  Context for service invocations.
 *  Contains all method call information. 
 */
public class ServiceInvocationContext
{
	//-------- attributes --------
	
	/** The proxy object. */
	protected Object proxy;
	
	/** The method to be called. */
	protected Method method;
	
	/** The invocation arguments. */
	protected Object[] args;
	
	/** The call result. */
	protected Object result;

	/** The service interceptors. */
	protected IServiceInvocationInterceptor[] interceptors;

	/** The interceptor number to call. */
	protected int cnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ServiceInvocationContext(Object proxy, Method method, Object[] args, IServiceInvocationInterceptor[] interceptors)
	{
		this.proxy = proxy;
		this.method = method;
		this.args = args;
		this.interceptors = interceptors;
	}

	//-------- methods --------
	
	/**
	 *  Get the proxy.
	 *  @return the proxy.
	 */
	public Object getProxy()
	{
		return proxy;
	}
	
	/**
	 *  Get the method.
	 *  @return the method.
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public Object[] getArguments()
	{
		return args;
	}

	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}
	
	/**
	 *  Invoke the next interceptor.
	 */
	public IFuture invoke()
	{
		final Future ret = new Future();
		if(cnt<interceptors.length)
		{
			interceptors[cnt++].execute(this);
		}
		else
		{
			
		}
		return ret;
	}
}
