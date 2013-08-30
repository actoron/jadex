package jadex.bridge.sensor.service;

import java.lang.reflect.Method;


/**
 *  Interface for listeners that are notified when a service method is invoked.
 */
public interface IMethodInvocationListener
{
	/**
	 *  Called when a method call started.
	 */
	public void methodCallStarted(Object proxy, Method method, final Object[] args, long callid);
	
	/**
	 *  Called when the method call is finished.
	 */
	public void methodCallFinished(Object proxy, Method method, final Object[] args, long callid);
}
