package jadex.bridge;

import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.ICommandFuture;
import jadex.commons.future.IFuture;

/**
 *  Helper class for future aspects.
 */
public class SFuture
{
	/**
	 *  Automatically update the timer of a long running service call future.
	 *  Ensures that the caller does not timeout even if no result
	 *  value is set in that timespan.
	 *  The call periodically sends alive calls to the caller. 
	 *  @param ret The future that is returned by the service call.
	 *  @param ia The component handling the service call (on that component the periodic updates are scheduled).
	 */
	public static void avoidCallTimeouts(final Future<?> ret, IInternalAccess ia)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		long to = sc!=null? sc.getTimeout(): BasicService.DEFAULT_LOCAL; // Hack!!! find out in which cases service call can null
	//	boolean local = sc.getCaller().getPlatformName().equals(agent.getComponentIdentifier().getPlatformName());
	//	long to = sc.getTimeout()>0? sc.getTimeout(): (local? BasicService.DEFAULT_LOCAL: BasicService.DEFAULT_REMOTE);
	//	to = 5000;
		avoidCallTimeouts(ret, ia, to);
	}
	
	/**
	 *  Automatically update the timer of a long running service call future.
	 *  Ensures that the caller does not timeout even if no result
	 *  value is set in that timespan.
	 *  The call periodically sends alive calls to the caller. 
	 *  @param ret The future that is returned by the service call.
	 *  @param ia The component handling the service call (on that component the periodic updates are scheduled).
	 *  @param factor (default 0.8) Used to update the timer when factor*to has elapsed.
	 */
	public static void avoidCallTimeouts(final Future<?> ret, IInternalAccess ia, double factor)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		long to = sc.getTimeout();
		avoidCallTimeouts(ret, ia, to, factor);
	}
	
	/**
	 *  Automatically update the timer of a long running service call future.
	 *  Ensures that the caller does not timeout even if no result
	 *  value is set in that timespan.
	 *  The call periodically sends alive calls to the caller. 
	 *  @param ret The future that is returned by the service call.
	 *  @param ia The component handling the service call (on that component the periodic updates are scheduled).
	 *  @param to The timeout.
	 */
	public static void avoidCallTimeouts(final Future<?> ret, IInternalAccess ia, long to)
	{
		avoidCallTimeouts(ret, ia, to, 0.8);
	}
	
	/**
	 *  Automatically update the timer of a long running service call future.
	 *  Ensures that the caller does not timeout even if no result
	 *  value is set in that timespan.
	 *  The call periodically sends alive calls to the caller. 
	 *  @param ret The future that is returned by the service call.
	 *  @param ia The component handling the service call (on that component the periodic updates are scheduled).
	 *  @param to The timeout.
	 *  @param factor (default 0.8) Used to update the timer when factor*to has elapsed.
	 */
	public static void avoidCallTimeouts(final Future<?> ret, IInternalAccess ia, long to, double factor)
	{
		if(to>0)
		{
			final long w = (long)(to*factor);
			IComponentStep<Void> step = new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if(!ret.isDone())
					{
						ret.sendCommand(ICommandFuture.Type.UPDATETIMER);
						ia.waitForDelay(w, this);
					}
					return IFuture.DONE;
				}
			};
			ia.waitForDelay(w, step);
		}
	}
	
	/**
	 *  Convenience method for creating a future (possibly with timeout avoidance).
	 *  @param timeouts (default is true) False, if no timeouts should be generated when service call timeout has elapsed.
	 *  @param ea The external access.
	 */
	public static <T> Future<?> getNoTimeoutFuture(IInternalAccess ia)
	{
		return getFuture((Class<?>)Future.class, false, ia);
	}
	
	/**
	 *  Convenience method for creating a future (possibly with timeout avoidance).
	 *  @param timeouts (default is true) False, if no timeouts should be generated when service call timeout has elapsed.
	 *  @param ea The external access.
	 */
	public static <T> Future<?> getNoTimeoutFuture(Class<T> type, IInternalAccess ia)
	{
		return getFuture(type, false, ia);
	}
	
	/**
	 *  Convenience method for creating a future (possibly with timeout avoidance).
	 *  @param timeouts (default is true) False, if no timeouts should be generated when service call timeout has elapsed.
	 *  @param ia The external access.
	 */
	public static <T> Future<?> getFuture(boolean timeouts, IInternalAccess ia)
	{
		return getFuture((Class<T>)Future.class, timeouts, ia);
	}
	
	/**
	 *  Convenience method for creating a future (possibly with timeout avoidance).
	 *  @param type The future implementation type.
	 *  @param timeouts (default is true) False, if no timeouts should be generated when service call timeout has elapsed.
	 *  @param ia The external access.
	 */
	public static <T> Future<?> getFuture(Class<T> type, boolean timeouts, IInternalAccess ia)
	{
		try
		{
			Future<?> ret = (Future<?>)type.newInstance();
			
			if(!timeouts)
				avoidCallTimeouts(ret, ia);
			
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
