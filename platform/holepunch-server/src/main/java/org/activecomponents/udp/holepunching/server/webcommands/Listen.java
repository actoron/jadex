package org.activecomponents.udp.holepunching.server.webcommands;

import org.activecomponents.udp.holepunching.server.IConnectedHost;
import org.activecomponents.udp.holepunching.server.commands.IServerCommand;

public class Listen implements IServerCommand
{
	/** Lease Time */
	public static final long LEASE_TIME = 60000;
	
	/**
	 *  Tests if applicable.
	 *  @param cmd
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(String cmd)
	{
		return "listen".equals(cmd);
	}
	
	/**
	 *  Executes the command.
	 *  
	 *  @param cmd The command string.
	 *  @param args The arguments.
	 *  @param connectedhost The connection.
	 *  @return The response. 
	 */
	public String execute(String cmd, String[] args, IConnectedHost connectedhost)
	{
		if (args.length != 1)
		{
			throw new IllegalArgumentException();
		}
		
		RegisteredWebHost reghost = (RegisteredWebHost) connectedhost.getRegisteredHosts().get(args[0]);
		if (reghost == null)
		{
			return "ID not found: " + args[0];
		}
		
		reghost.setLastActivity(System.currentTimeMillis());
		
		return reghost.readMsg(LEASE_TIME >> 2);
	}
}
