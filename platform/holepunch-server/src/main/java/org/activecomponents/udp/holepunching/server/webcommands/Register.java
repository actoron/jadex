package org.activecomponents.udp.holepunching.server.webcommands;

import java.util.Map;
import java.util.Random;

import org.activecomponents.udp.holepunching.server.IConnectedHost;
import org.activecomponents.udp.holepunching.server.IRegisteredHost;
import org.activecomponents.udp.holepunching.server.commands.IServerCommand;

public class Register implements IServerCommand
{
	
	
	/** Random number generator for IDs. */
	protected static final Random RANDOM = new Random();
	
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
		if (args.length != 1)
		{
			return "Usage: register [url]\n";
		}
		
		Map<String, IRegisteredHost> reghosts = connectedhost.getRegisteredHosts();
		synchronized(reghosts)
		{
			if (reghosts.containsKey(args[0]))
			{
				return "URL already registered.\n";
			}
			RegisteredWebHost reghost = new RegisteredWebHost();
			reghosts.put(args[0], reghost);
		}
		
		return "Registered.\n";
	}
}
