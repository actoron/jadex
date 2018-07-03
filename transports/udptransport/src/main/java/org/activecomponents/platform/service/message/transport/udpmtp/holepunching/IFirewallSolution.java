package org.activecomponents.platform.service.message.transport.udpmtp.holepunching;

import org.activecomponents.udp.IThreadExecutor;

/**
 *  Solution for getting through a firewall if one was found in the initial analysis.
 *
 */
public interface IFirewallSolution
{
	/** Gets the addresses for the solution. */
	public String[] getAddresses();
	
	/**
	 *  Activates the solution.
	 */
	public void start(IThreadExecutor texec);
	
	/**
	 *  Stops the solution.
	 */
	public void stop();
}
