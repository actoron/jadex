package jadex.micro.examples.watchdog;

import jadex.commons.future.IFuture;

/**
 *  Watchdogs observe each other and take actions
 *  when a watchdog becomes unavailable.
 */
public interface IWatchdogService
{
	/**
	 *  Get the information about this watchdog.
	 *  @return The information.
	 */
	public String	getInfo();
	
	/**
	 *  Test if this watchdog is alive.
	 */
	public IFuture ping();
}
