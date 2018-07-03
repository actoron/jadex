package org.activecomponents.udp.holepunching.server.commands;

import org.activecomponents.udp.holepunching.server.IConnectedHost;

/**
 *  Command executable on the server.
 *
 */
public interface IServerCommand
{
	/**
	 *  Tests if applicable.
	 *  @param cmd
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(String cmd);
	
	/**
	 *  Executes the command.
	 *  
	 *  @param cmd The command string.
	 *  @param args The arguments.
	 *  @param connectedhost The connection.
	 *  @return The response. 
	 */
	public String execute(String cmd, String[] args, IConnectedHost connectedhost);
}
