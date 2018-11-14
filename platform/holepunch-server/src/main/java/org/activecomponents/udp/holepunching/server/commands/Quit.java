package org.activecomponents.udp.holepunching.server.commands;

import org.activecomponents.udp.holepunching.server.IConnectedHost;

public class Quit implements IServerCommand
{
	/**
	 *  Tests if applicable.
	 *  @param cmd
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(String cmd)
	{
		return "quit".equals(cmd);
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
//		try
//		{
//			connectedhost.writeMsg("Goodbye.\n");
//		}
//		catch(Exception e)
//		{
//		}
		throw new RuntimeException("User quitting...");
	}
}
