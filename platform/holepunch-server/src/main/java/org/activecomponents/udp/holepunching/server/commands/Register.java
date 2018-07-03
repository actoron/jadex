package org.activecomponents.udp.holepunching.server.commands;

import org.activecomponents.udp.holepunching.server.IConnectedHost;
import org.activecomponents.udp.holepunching.server.ServerConnection;

public class Register implements IServerCommand
{
	/**
	 *  Tests if applicable.
	 *  @param cmd
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(String cmd)
	{
		return "register".equals(cmd);
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
		if (((ServerConnection) connectedhost).getRegistered() != null)
		{
			return "Already registered.\n";
		}
		String usage = "Usage: register [url]\n";
		if (args.length != 1)
		{
			return usage;
		}
		
		synchronized(connectedhost.getRegisteredHosts())
		{
			if (connectedhost.getRegisteredHosts().containsKey(args[0]))
			{
				return "URL already registered.\n";
			}
			((ServerConnection) connectedhost).setRegistered(args[0]);
			
			connectedhost.getRegisteredHosts().put(args[0], ((ServerConnection) connectedhost));
		}
		return "Registered.\n";
	}
}
