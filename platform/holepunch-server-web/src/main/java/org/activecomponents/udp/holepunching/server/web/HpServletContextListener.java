package org.activecomponents.udp.holepunching.server.web;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.activecomponents.udp.holepunching.server.IRegisteredHost;
import org.activecomponents.udp.holepunching.server.webcommands.Listen;
import org.activecomponents.udp.holepunching.server.webcommands.RegisteredWebHost;

public class HpServletContextListener implements ServletContextListener
{
	/** Context ID for registered hosts. */
	public static final String REGISTERED_HOSTS = "registeredhosts";
	
	/** Context ID for datagram socket. */
	public static final String DGSOCKET = "dgsocket";
	
	protected static final long CLEANER_INTERVAL = 29356;
	
	/** The context cleaner */
	private ScheduledExecutorService cleaner;
	
	/**
	 *  Initializes the servlet context.
	 */
	public void contextInitialized(ServletContextEvent sce)
	{
		System.out.println("Initializing");
		final Map<String, IRegisteredHost> reghosts = Collections.synchronizedMap(new HashMap<String, IRegisteredHost>());
		sce.getServletContext().setAttribute(REGISTERED_HOSTS, reghosts);
		
		try
		{
			DatagramSocket dgsocket = new DatagramSocket(15000);
			System.out.println("DGSocket: " + dgsocket.getLocalPort() + " " + dgsocket.getLocalAddress().getHostAddress());
			sce.getServletContext().setAttribute(DGSOCKET, dgsocket);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		cleaner = Executors.newSingleThreadScheduledExecutor();
		cleaner.scheduleAtFixedRate(new Runnable()
		{
			public void run()
			{
				synchronized (reghosts)
				{
					long current = System.currentTimeMillis();
					for (Iterator<Map.Entry<String, IRegisteredHost>> it = reghosts.entrySet().iterator(); it.hasNext(); )
					{
						Map.Entry<String, IRegisteredHost> entry = it.next();
						long lastactivity = ((RegisteredWebHost) entry.getValue()).getLastActivity();
						if (lastactivity + Listen.LEASE_TIME < current)
						{
							//System.out.println("Nuking " + entry.getKey());
							it.remove();
						}
					}
				}
			}
		},
		CLEANER_INTERVAL, CLEANER_INTERVAL, TimeUnit.MILLISECONDS);
	}

	/**
	 *  Cleans the servlet context.
	 */
	public void contextDestroyed(ServletContextEvent sce)
	{
		cleaner.shutdownNow();
		
		DatagramSocket dgsocket = (DatagramSocket) sce.getServletContext().getAttribute(DGSOCKET);
		if (dgsocket != null)
		{
			try
			{
				dgsocket.close();
			}
			catch(Exception e)
			{
			}
		}
		sce.getServletContext().removeAttribute(DGSOCKET);
		sce.getServletContext().removeAttribute(REGISTERED_HOSTS);
	}
	
}
