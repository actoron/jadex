package jadex.extension.ws.publish;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import jadex.commons.SReflect;

/**
 *  Base class for generated web service proxies.
 *  
 *  Generated proxies implement a domain dependent web service interface
 *  by delegation methods that all call the invocation handler.
 *  In this way the proxy does the same as a Java dynamic proxy.
 *  
 *  The invoke method in this class is copied as body for all
 *  service methods, i.e. the invoke method is not called itself at any time.
 */
public class Proxy
{
	//-------- attributes --------
	
	/** The invocation handler. */
	protected InvocationHandler handler;
	
	//-------- methods --------

	/**
	 *  Functionality blueprint for all service methods.
	 *  @param params The parameters.
	 *  @return The result.
	 */
	public Object invoke(Object[] params)
	{
		Object ret = null;
		
		try
		{
			StackTraceElement[] s = Thread.currentThread().getStackTrace();
			String name = s[2].getMethodName();
//			for(int i=0;i<s.length; i++)
//			{
//				System.out.println(s[i].getMethodName());
//			}
//			String name = SReflect.getMethodName();
			Method[] methods = SReflect.getMethods(getClass(), name);
		    Method method = null;
			if(methods.length>1)
			{
			    for(int i=0; i<methods.length && method==null; i++)
			    {
			    	Class<?>[] types = methods[i].getParameterTypes();
			    	if(types.length==params.length)
			    	{
			    		// check param types
			    		method = methods[i];
			    	}
			    }
			}
			else if(methods.length==1)
			{
				method = methods[0];
			}
//			System.out.println("call: "+this+" "+method+" "+args+" "+name);
			ret = handler.invoke(this, method, params);
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
		
		return ret;
	}

	/**
	 *  Get the handler.
	 *  @return The handler.
	 */
	public InvocationHandler getHandler()
	{
		return handler;
	}

	/**
	 *  Set the handler.
	 *  @param handler The handler to set.
	 */
	public void setHandler(InvocationHandler handler)
	{
		this.handler = handler;
	}
	
}
