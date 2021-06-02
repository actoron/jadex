package org.activecomponents.platform.service.message.transport.udpmtp.holepunching;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.activecomponents.platform.service.message.transport.udpmtp.HolepunchServerConf;
import org.activecomponents.platform.service.message.transport.udpmtp.SHolepunchServerClient;
import org.activecomponents.platform.service.message.transport.udpmtp.UdpTransport;
import org.activecomponents.udp.IThreadExecutor;

/**
 *  Address-specific firewall solution.
 *
 */
public class AddressSolution extends AbstractSolution
{
	/** Socket used for holepunching. */
	protected DatagramSocket dgsocket;
	
	/** Configuration for holepunching assist server.. */
	protected HolepunchServerConf conf;
	
	/** Listen handle holepunching assist server.. */
	protected Object listenhandle;
	
	/** URL used for registration. */
	protected String url;
	
	/** Flag if running. */
	protected volatile boolean running;
	
	/**
	 *  Creates the solution
	 *  @param schema The schema used.
	 *  @param port The port used.
	 */
	public AddressSolution(String schema, String extip, int port, DatagramSocket dgsocket, HolepunchServerConf serverconf)
	{
		super(schema, extip, port);
		this.schema = schema;
		this.dgsocket = dgsocket;
		this.conf = serverconf;
		this.url = getAddresses()[0];
	}
	
	/**
	 *  Activates the solution.
	 */
	public void start(final IThreadExecutor texec)
	{
		if (!running)
		{
			running = true;
			texec.run(new Runnable()
			{
				public void run()
				{
					DatagramPacket dgp;
					dgp = new DatagramPacket(new byte[0], 0);
					
					while(running)
					{
						while (listenhandle == null)
						{
							try
							{
								listenhandle = SHolepunchServerClient.register(conf, url);
								if (listenhandle == null)
								{
									Thread.sleep(10000);
								}
							}
							catch (Exception e)
							{
							}
						}
						
						if (listenhandle != null)
						{
							try
							{
								String reply = SHolepunchServerClient.listen(conf, listenhandle);
//								System.out.println("BaseHpReqRead: " +reply);
								if (reply != null && reply.startsWith("HpReq"))
								{
									UdpTransport.LOGGER.log(Level.FINE, "HpReqRead: " +reply);
									String target = reply.substring(reply.indexOf('|') + 1);
									String host = null;
									int port = 0;
									if (target.startsWith("["))
									{
										int ind = target.indexOf(']');
										host = target.substring(1, ind);
										port = Integer.parseInt(target.substring(ind + 2));
									}
									else
									{
										int ind = target.indexOf(":");
										host = target.substring(0, ind);
										port = Integer.parseInt(target.substring(ind + 1));
									}
									
									UdpTransport.LOGGER.log(Level.FINE, "HpReq received from " + host + ":" + port);
									
									dgp.setSocketAddress(new InetSocketAddress(host, port));
									
									final DatagramPacket fdgp = dgp;
									
									//TODO: Add restrictions against spamming
									texec.run(new Runnable()
									{
										
										@Override
										public void run()
										{
											for (int i = 0; i < 60; ++i)
											{
												try
												{
													dgsocket.send(fdgp);
													Thread.sleep(100);
												}
												catch (Exception e)
												{
												}
												
											}
										}
									});
								}
							}
							catch (Exception e)
							{
								if (listenhandle != null)
								{
									SHolepunchServerClient.close(listenhandle);
								}
								listenhandle = null;
							}
						}
					}
					
					if (listenhandle != null)
					{
						SHolepunchServerClient.close(listenhandle);
					}
					listenhandle = null;
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
