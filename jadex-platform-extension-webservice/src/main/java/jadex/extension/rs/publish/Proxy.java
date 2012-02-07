package jadex.extension.rs.publish;

import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.lang.reflect.Method;

import com.sun.jersey.api.core.ResourceConfig;

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
	//-------- methods --------

	/**
	 *  Functionality blueprint for all service methods.
	 *  @param params The parameters.
	 *  @return The result.
	 */
	public Object invoke(Object[] params)
	{
		Object ret = null;
		
//		System.out.println("called invoke: "+params);
		
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
			
			try
			{
				ResourceConfig rc = (ResourceConfig)getClass().getDeclaredField("__rc").get(this);
				Object service = rc.getProperty(DefaultRestServicePublishService.JADEXSERVICE);
				
				String mname = method.getName();
				if(mname.endsWith("XML"))
					mname = mname.substring(0, mname.length()-3);
				if(mname.endsWith("JSON"))
					mname = mname.substring(0, mname.length()-4);

				System.out.println("call: "+mname+" on "+service);
				
				Method m = service.getClass().getMethod(mname, method.getParameterTypes());
				ret = m.invoke(service, params);
				if(ret instanceof IFuture)
				{
					ret = ((IFuture)ret).get(new ThreadSuspendable());
				}
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
		
		return ret;
	}

}
