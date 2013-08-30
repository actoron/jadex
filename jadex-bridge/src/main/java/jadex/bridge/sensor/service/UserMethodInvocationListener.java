package jadex.bridge.sensor.service;

import jadex.bridge.service.component.interceptors.ResolveInterceptor;

import java.lang.reflect.Method;

/**
 *  Listener that only observes user methods. It ignores calls to
 *  IService, INFPropertyProvider and INMethodPropertyProvider interfaces.
 */
public class UserMethodInvocationListener implements IMethodInvocationListener
{
	/** The protected listener. */
	protected IMethodInvocationListener listener;
	
	/**
	 *  Create a new UserMethodInvocationListener. 
	 */
	public UserMethodInvocationListener(IMethodInvocationListener listener)
	{
		this.listener = listener;
	}

	/**
	 *  Called when a method call started.
	 */
	public void methodCallStarted(Object proxy, Method method, final Object[] args, long callid)
	{
		if(ResolveInterceptor.SERVICEMETHODS.contains(method))
		{
			return;
		}
		else
		{
			listener.methodCallStarted(proxy, method, args, callid);
		}
	}
	
	/**
	 *  Called when the method call is finished.
	 */
	public void methodCallFinished(Object proxy, Method method, final Object[] args, long callid)
	{
		if(ResolveInterceptor.SERVICEMETHODS.contains(method))
		{
			return;
		}
		else
		{
			listener.methodCallFinished(proxy, method, args, callid);
		}
	}
}
