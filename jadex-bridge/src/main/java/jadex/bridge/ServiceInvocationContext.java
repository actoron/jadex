package jadex.bridge;

import java.lang.reflect.Method;

/**
 * 
 */
public class ServiceInvocationContext
{
	public Object proxy;
	public Method method;
	public Object[] args;
	public Object result;

	public ServiceInvocationContext(Object proxy, Method method, Object[] args)
	{
		this.proxy = proxy;
		this.method = method;
		this.args = args;
	}

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
