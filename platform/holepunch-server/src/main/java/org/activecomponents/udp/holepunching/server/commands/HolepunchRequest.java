package org.activecomponents.udp.holepunching.server.commands;

import org.activecomponents.udp.holepunching.server.IConnectedHost;
import org.activecomponents.udp.holepunching.server.IRegisteredHost;

/**
 *  Request for a holepunch.
 *
 */
public class HolepunchRequest implements IServerCommand
{
	/**
	 *  Tests if applicable.
	 *  @param cmd
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(String cmd)
	{
		return "hpreq".equals(cmd);
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
		String usage = "Usage: hpreq [localport] [url]\n";
		if (args.length != 2)
		{
			return usage;
		}
		
		int lport = 0;
		String url = args[1];
		try
		{
			lport = Integer.parseInt(args[0]);
		}
		catch (Exception e)
		{
			return usage;
		}
		
		IRegisteredHost rcon = connectedhost.getRegisteredHosts().get(url);
		if (rcon != null)
		{
			try
			{
				String addr = connectedhost.getRemoteAddress().getHostAddress();
				if (addr.contains(":"))
				{
					addr = "[" + addr + "]";
				}
				rcon.writeMsg("HpReq|" + addr + ":" + lport + "\n");
			}
			catch (Exception e)
			{
			}
		}
		else
		{
			return "Host not registered.\n";
		}
		return "HPReq done.\n";
	}
}
