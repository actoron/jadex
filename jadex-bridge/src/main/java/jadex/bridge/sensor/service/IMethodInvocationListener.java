package jadex.bridge.sensor.service;


/**
 * 
 */
public interface IMethodInvocationListener
{
	/**
	 *  Called when a method call started.
	 */
	public void methodCallStarted(Object proxy, final Object[] args, long callid);
	
	/**
	 *  Called when the method call is finished.
	 */
	public void methodCallFinished(Object proxy, final Object[] args, long callid);
}
