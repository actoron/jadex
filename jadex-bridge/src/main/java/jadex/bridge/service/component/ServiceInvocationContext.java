package jadex.bridge.service.component;

import java.lang.reflect.Method;

/**
 *  Context for service invocations.
 *  Contains all method call information. 
 */
public class ServiceInvocationContext
{
	//-------- attributes --------
	
	/** The proxy object. */
	public Object proxy;
	
	/** The method to be called. */
	public Method method;
	
	/** The invocation arguments. */
	public Object[] args;
	
	/** The call result. */
	public Object result;

	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ServiceInvocationContext(Object proxy, Method method, Object[] args)
	{
		this.proxy = proxy;
		this.method = method;
		this.args = args;
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
}
