package org.activecomponents.udp.holepunching.server.commands;

import java.net.DatagramPacket;

import org.activecomponents.udp.holepunching.server.IConnectedHost;

public class SendMeUdp implements IServerCommand
{
	/**
	 *  Tests if applicable.
	 *  @param cmd
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(String cmd)
	{
		return "sendmeudp".equals(cmd);
	}
	
	/**
	 *  Executes the command.
	 *  
	 *  @param cmd The command string.
	 *  @param args The arguments.
	 *  @param connectedhost The connection.
	 *  @return The response. 
	 */
	public String execute(String cmd, String[] args, final IConnectedHost connectedhost)
	{
		String usage = "Usage: sendmeudp [port] [count] [delay]\n";
		if (args.length != 3)
		{
			return usage;
		}
		int count = 0;
		int delay = 0;
		int port = 0;
		try
		{
			port = Integer.parseInt(args[0]);
			count = Integer.parseInt(args[1]);
			delay = Integer.parseInt(args[2]);
		}
		catch (Exception e)
		{
			return usage;
		}
		if (count > 10 || delay > 1000)
		{
			return usage;
		}
		
		final int fcount = count;
		final int fdelay = delay;
		final int fport = port;
		
		DatagramPacket dgp = new DatagramPacket(new byte[0], 0, connectedhost.getRemoteAddress(), fport);
		for (int i = 0; i < fcount; ++i)
		{
			try
			{
//				System.out.println("Sending to " + connectedhost.getRemoteAddress().getHostAddress());
				connectedhost.getUdpSocket().send(dgp);
				Thread.sleep(fdelay);
			}
			catch (Exception e)
			{
			}
		}
//		Thread sendthread = new Thread(new Runnable()
//		{
//			public void run()
//			{
//				DatagramPacket dgp = new DatagramPacket(new byte[0], 0, connectedhost.getRemoteAddress(), fport);
//				for (int i = 0; i < fcount; ++i)
//				{
//					try
//					{
//						System.out.println("Sending to " + connectedhost.getRemoteAddress().getHostAddress());
//						connectedhost.getUdpSocket().send(dgp);
//						Thread.sleep(fdelay);
//					}
//					catch (Exception e)
//					{
//					}
//				}
//			}
//		});
//		sendthread.setDaemon(true);
//		sendthread.start();
		
		return "Done.\n";
	}
}
