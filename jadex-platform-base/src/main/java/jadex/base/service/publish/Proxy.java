package jadex.base.service.publish;

import jadex.commons.SReflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Proxy
{
	protected InvocationHandler handler;
	
	public Object invoke(Object[] args)
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
			    	Class[] types = methods[i].getParameterTypes();
			    	if(types.length==args.length)
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
			ret = handler.invoke(this, method, args);
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
