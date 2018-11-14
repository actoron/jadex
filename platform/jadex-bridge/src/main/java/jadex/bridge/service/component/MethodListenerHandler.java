package jadex.bridge.service.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.sensor.service.IMethodInvocationListener;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.MethodInfo;

/**
 * 
 */
public class MethodListenerHandler
{
	/** The registered non-functional property hooks. */
	protected Map<MethodInfo, List<IMethodInvocationListener>> methodlisteners;
	
	/**
	 *  Add a method listener.
	 */
	public void addMethodListener(MethodInfo m, IMethodInvocationListener listener)
	{
		if(methodlisteners==null)
			methodlisteners = new HashMap<MethodInfo, List<IMethodInvocationListener>>();
		List<IMethodInvocationListener> lis = methodlisteners.get(m);
		if(lis==null)
		{
			lis = new ArrayList<IMethodInvocationListener>();
			methodlisteners.put(m, lis);
		}
		lis.add(listener);
	}
	
	/**
	 *  Add a method listener.
	 */
	public void removeMethodListener(MethodInfo m, IMethodInvocationListener listener)
	{
		if(methodlisteners!=null)
		{
			List<IMethodInvocationListener> lis = methodlisteners.get(m);
			if(lis!=null)
			{
				lis.remove(listener);
			}
		}
	}
	
	/**
	 *  Notify registered listeners in case a method is called.
	 */
	public void notifyMethodListeners(boolean start, Object proxy, final Method method, final Object[] args, Object callid, ServiceInvocationContext context)
	{
		if(methodlisteners!=null)
		{
			doNotifyListeners(start, proxy, method, args, callid, context, methodlisteners.get(null));
			doNotifyListeners(start, proxy, method, args, callid, context, methodlisteners.get((new MethodInfo(method))));
		}
	}
	
	/**
	 *  Test if service and method has listeners.
	 */
	public boolean hasMethodListeners(IServiceIdentifier sid, MethodInfo mi)
	{
		boolean ret = false;
		if(methodlisteners!=null)
		{
			List<IMethodInvocationListener> lis = methodlisteners.get(mi);
			ret = lis!=null && lis.size()>0;
			if(!ret)
			{
				lis = methodlisteners.get(null);
				ret = lis!=null && lis.size()>0;
			}
		}
		return ret;
	}
	
	/**
	 *  Do notify the listeners.
	 */
	protected void doNotifyListeners(boolean start, Object proxy, final Method method, final Object[] args, Object callid, ServiceInvocationContext context, List<IMethodInvocationListener> lis)
	{
		if(lis!=null)
		{
			for(IMethodInvocationListener ml: lis)
			{
				if(start)
				{
					ml.methodCallStarted(proxy, method, args, callid, context);
				}
				else
				{
					ml.methodCallFinished(proxy, method, args, callid, context);
				}
			}
		}
	}
}
