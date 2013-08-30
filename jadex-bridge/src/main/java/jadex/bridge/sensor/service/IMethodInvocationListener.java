package jadex.bridge.sensor.service;

import jadex.commons.MethodInfo;


/**
 *  Interface 
 */
public interface IMethodInvocationListener
{
	/**
	 *  Called when a method call started.
	 */
	public void methodCallStarted(Object proxy, MethodInfo mi, final Object[] args, long callid);
	
	/**
	 *  Called when the method call is finished.
	 */
	public void methodCallFinished(Object proxy, MethodInfo mi, final Object[] args, long callid);
}
