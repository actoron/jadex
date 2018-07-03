package org.activecomponents.udp.holepunching.server.commands;

import org.activecomponents.udp.holepunching.server.IConnectedHost;

public class ReportUdpIp implements IServerCommand
{
	/**
	 *  Tests if applicable.
	 *  @param cmd
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(String cmd)
	{
		return "reportudpip".equals(cmd);
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
		return "My UDP IP:" + connectedhost.getUdpSocket().getLocalAddress().getHostAddress() + "\n";
	}
}
