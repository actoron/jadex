package org.activecomponents.platform.service.message.transport.udpmtp.holepunching;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.activecomponents.udp.IThreadExecutor;

/**
 *  Port-based firewall holepunch solution.
 *
 */
public class PortSolution extends AbstractSolution
{
	/** Socket used for holepunching. */
	protected DatagramSocket dgsocket;
	
	/** Target for holepunching packets. */
	protected SocketAddress target;
	
	/** Flag if running. */
	protected volatile boolean running;
	
	/**
	 *  Creates the solution
	 *  @param schema The schema used.
	 *  @param port The port used.
	 */
	public PortSolution(String schema, String extip, int port, DatagramSocket dgsocket, SocketAddress target)
	{
		super(schema, extip, port);
		this.dgsocket = dgsocket;
		this.target = target;
	}
	
	/**
	 *  Activates the solution.
	 */
	public void start(IThreadExecutor texec)
	{
		if (!running)
		{
			running = true;
			texec.run(new Runnable()
			{
				public void run()
				{
					DatagramPacket dgp;
					try
					{
						dgp = new DatagramPacket(new byte[0], 0, target);
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
					while(running)
					{
						try
						{
							dgsocket.send(dgp);
							Thread.sleep(30000);
						}
						catch (Exception e)
						{
						}
					}
				}
			});
		}
	}
	
	/**
	 *  Stops the solution.
	 */
	public void stop()
	{
		running = false;
	}
}
