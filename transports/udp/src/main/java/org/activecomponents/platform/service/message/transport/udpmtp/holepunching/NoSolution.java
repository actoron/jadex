package org.activecomponents.platform.service.message.transport.udpmtp.holepunching;

import org.activecomponents.udp.IThreadExecutor;

/**
 *  Solution used if none was found or necessary.
 *
 */
public class NoSolution extends AbstractSolution
{
	/**
	 *  Creates the solution
	 *  @param schema The schema used.
	 *  @param extip External IP in case of NAT.
	 *  @param port The port used.
	 */
	public NoSolution(String schema, String extip, int port)
	{
		super(schema, extip, port);
	}
	
	/**
	 *  Activates the solution.
	 */
	public void start(IThreadExecutor texec)
	{
	}
	
	/**
	 *  Stops the solution.
	 */
	public void stop()
	{
	}
}
