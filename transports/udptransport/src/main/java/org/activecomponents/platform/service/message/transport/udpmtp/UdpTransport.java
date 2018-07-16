package org.activecomponents.platform.service.message.transport.udpmtp;

import java.lang.reflect.Method;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.activecomponents.platform.service.message.transport.udpmtp.holepunching.AddressSolution;
import org.activecomponents.platform.service.message.transport.udpmtp.holepunching.IFirewallSolution;
import org.activecomponents.platform.service.message.transport.udpmtp.holepunching.NoSolution;
import org.activecomponents.platform.service.message.transport.udpmtp.holepunching.PortSolution;
import org.activecomponents.udp.Connection;
import org.activecomponents.udp.IIncomingListener;
import org.activecomponents.udp.IUdpCallback;
import org.activecomponents.udp.STunables;
import org.activecomponents.udp.SUdpUtil;
import org.activecomponents.udp.UdpConnectionHandler;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransport;

public class UdpTransport implements ITransport	
{
	/** Transport logger. */
	public static Logger LOGGER = Logger.getLogger("UdpTransport");
	
	/** Setting on how verbose the transport should be. */
	public static Level LOG_LEVEL = Level.INFO;
	
	/** Host of the holepunching server. */
	public static final String HP_SERVER_HOST = "hpserver.actoron.com";
	
	/** Port of the holepunching server. */
	public static final int HP_SERVER_PORT = 80;
	
	/** Initial reconnect wait time. */
	protected static final int INITIAL_RECONNECT_WAIT = 1000;
	
	/** Maximum reconnect wait time. */
	protected static final int MAX_RECONNECT_WAIT = 60*60*1000;
	
	/** Address resolve cache size. */
	protected static final int RESOLVE_CACHE_SIZE = 100;
	
	/** The platform. */
	protected IInternalAccess component;
	
	/** The platform, external. */
	protected IExternalAccess extcomponent;
	
	/** Server for assisting holepunching. */
	protected HolepunchServerConf hpserverconf;
	
	/** Initial port for insecure traffic. */
	protected Integer insecureport;
	
	/** Initial port for secure traffic. */
	protected Integer secureport;
	
	/** Secure schema name. */
	public String secureschema;
	
	/** Insecure schema name. */
	public String insecureschema = "udp-mtp://";
	
	/** Solution for circumventing potential firewalls for insecure connections. */
	protected IFirewallSolution insecsolution;
	
	/** Handler for insecure connections. */
	protected UdpConnectionHandler insecurehandler;
	
	/** Solution for circumventing potential firewalls for secure connections. */
	protected IFirewallSolution secsolution;
	
	/** Handler for secure connections. */
	protected UdpConnectionHandler securehandler;
	
	/** Available schemas. */
	protected String[] schemas;
	
	/** Local addresses cache. */
	protected String[] addresses;
	
	/** Connect blacklist. */
	protected Map<String, Tuple2<Long, Long>> connectblacklist;
	
	/** Address resolve cache, URL --> handler/(host/port)/HPRequired */
	protected LRU<String, RemoteHost> resolvecache;
	
	/** Message service for message delivery. */
	protected IMessageService msgser;
	
	/** Task on message fail. */
	protected IResultCommand<IFuture<Void>, Void> failtask;
	
	/**
	 *  Starts the transport with randomized ports.
	 */
	public UdpTransport(IInternalAccess component)
	{
//		this(component, 5000, 5100);
		this(component, (int)(Math.random() * 30000) + 5000, (int)(Math.random() * 30000) + 5000);
	}
	
	/**
	 *  Starts the transport.
	 * 
	 *  @param component The platform.
	 *  @param enableinsecure Enable insecure connections.
	 *  @param enablesecure Enable secure connections.
	 */
	public UdpTransport(IInternalAccess component, boolean enableinsecure, boolean enablesecure)
	{
		this(component, enableinsecure? (int)(Math.random() * 500) + 17000 : null, enablesecure? (int)(Math.random() * 500) + 17000 : null);
	}
	
	/**
	 *  Starts the transport.
	 * 
	 *  @param component The platform.
	 *  @param port The insecure port.
	 *  @param secureport The secure port.
	 */
	public UdpTransport(IInternalAccess component, Integer port, Integer secureport)
	{
		SUdpUtil.RANDOM = SUtil.getSecureRandom();
		
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(new Handler()
		{
			public void publish(LogRecord record)
			{
				System.out.println(record.getMessage());
			}
			public void flush()
			{
			}
			public void close() throws SecurityException
			{
			}
		});
		LOGGER.setLevel(LOG_LEVEL);
		failtask = new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				return new Future<Void>(new RuntimeException("Send failed, no connection."));
			}
		};
//		port = null;
//		this.hpserverconf = new HolepunchServerConf("www3.activecomponents.org", 10000, HolepunchServerConf.BASIC);
		this.hpserverconf = new HolepunchServerConf(HP_SERVER_HOST, HP_SERVER_PORT, HolepunchServerConf.HTTP);
		this.component = component;
		this.extcomponent = component.getExternalAccess();
		connectblacklist = new HashMap<String, Tuple2<Long, Long>>();
		
		this.insecureport = port;
		this.secureport = secureport;
		if (port == null && secureport == null)
		{
			throw new IllegalArgumentException("Disabled both secure and insecure UDP.");
		}
		
		resolvecache = new LRU<String, RemoteHost>(RESOLVE_CACHE_SIZE);
	}
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		IDaemonThreadPoolService dtps = getLocalServiceCompat(component, IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		DaemonThreadPoolServiceExecutor dtpse = new DaemonThreadPoolServiceExecutor(dtps);
		IIncomingListener inclistener = new IIncomingListener()
		{
			public void receivePacket(SocketAddress remoteaddress, byte[] data)
			{
				receiveMessage(remoteaddress, data);
			}
			
			public void receiveMessage(final SocketAddress remoteaddress, final byte[] data)
			{
				extcomponent.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
//								System.out.println("DEL " + data);
						if (msgser == null)
						{
							msgser = getLocalServiceCompat(ia, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
						}
						msgser.deliverMessage(data);
						return null;
					}
				});
			}
		};
		IIncomingListener[]inclisteners = new IIncomingListener[] { inclistener };
		
		DatagramSocket ids = null;
		if (insecureport != null)
		{
			while (ids == null)
			{
				InetSocketAddress isa = new InetSocketAddress(insecureport);
				try
				{
					ids = new DatagramSocket(isa);
				}
				catch (BindException e)
				{
					++insecureport;
				}
				catch(SocketException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		DatagramSocket sds = null;
		if (secureport != null)
		{
			while (sds == null)
			{
				InetSocketAddress isa = new InetSocketAddress(secureport);
				try
				{
					sds = new DatagramSocket(isa);
				}
				catch (BindException e)
				{
					++secureport;
				}
				catch(SocketException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		if (ids != null)
		{
			//getFirewallHandler(ids, "siod.net", 10000, 2000);
			insecureschema = "udp-v" + STunables.PROTOCOL_VERSION + "c" + UdpConnectionHandler.NULL_CIPHER + "-mtp://";
			insecsolution = getFirewallSolution(insecureschema, ids, hpserverconf.getHost(), hpserverconf.getPort(), 1000);
			insecurehandler = new UdpConnectionHandler(ids, UdpConnectionHandler.NULL_CIPHER, true, inclisteners);
			insecurehandler.start(dtpse);
			insecsolution.start(dtpse);
		}
		if (sds != null)
		{
			secureschema = "udp-v" + STunables.PROTOCOL_VERSION + "c" + UdpConnectionHandler.DEFAULT_CIPHER + "-mtp://";
			secsolution = getFirewallSolution(secureschema, sds, hpserverconf.getHost(), hpserverconf.getPort(), 1000);
			securehandler = new UdpConnectionHandler(sds, UdpConnectionHandler.DEFAULT_CIPHER, true, inclisteners);
			securehandler.start(dtpse);
			secsolution.start(dtpse);
		}
		
		return IFuture.DONE;
	}

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture<Void> shutdown()
	{
		if (insecurehandler != null)
		{
			insecurehandler.stop();
			insecsolution.stop();
		}
		if (securehandler != null)
		{
			securehandler.stop();
			secsolution.stop();
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Test if a transport is applicable for the target address.
	 *  @return True, if the transport is applicable for the address.
	 */
	public boolean	isApplicable(String address)
	{
		return (address.startsWith(insecureschema) && insecurehandler != null) || 
			   (address.startsWith(secureschema) && securehandler != null);
	}
	
	/**
	 *  Test if a transport satisfies the non-functional requirements.
	 *  @param nonfunc	The non-functional requirements (name, value).
	 *  @param address	The transport address.
	 *  @return True, if the transport satisfies the non-functional requirements.
	 */
	public boolean	isNonFunctionalSatisfied(Map<String, Object> nonfunc, String address)
	{
		Boolean sec = nonfunc!=null? (Boolean)nonfunc.get(SecureTransmission.SECURE_TRANSMISSION): null;
		if (Boolean.TRUE.equals(sec) && !address.startsWith(secureschema))
		{
			return false;
		}
		return true;
	}
//	protected Map<String, LinkedBlockingQueue<ISendTask>> msgtaskqueues = new HashMap<String, LinkedBlockingQueue<ISendTask>>(); 
	/**
	 *  Send a message to the given address.
	 *  This method is called multiple times for the same message, i.e. once for each applicable transport / address pair.
	 *  The transport should asynchronously try to connect to the target address
	 *  (or reuse an existing connection) and afterwards call-back the ready() method on the send task.
	 *  
	 *  The send manager calls the obtained send commands of the transports and makes sure that the message
	 *  gets sent only once (i.e. call send commands sequentially and stop, when a send command finished successfully).
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param address The address to send to.
	 *  @param task A task representing the message to send.
	 */
	public void	sendMessage(String address, final ISendTask task)
	{
		RemoteHost remotehost = resolvecache.get(address);
		if (remotehost == null)
		{			
			remotehost = new RemoteHost(address);
			
			UdpConnectionHandler handler = null;
			if (insecureschema.equals(remotehost.getSchema()))
			{
				handler = insecurehandler;
			}
			else if (secureschema.equals(remotehost.getSchema()))
			{
				handler = securehandler;
			}
			
			remotehost.setHandler(handler);
			
			resolvecache.put(address, remotehost);
		}
		
		if (remotehost.getHandler() == null)
			return;
		
		Connection con = remotehost.getHandler().getConnection(remotehost.getHost(), remotehost.getPort());
		if (con == null)
		{
			Tuple2<Long, Long> timeout = connectblacklist.get(address);
			if (timeout == null || timeout.getFirstEntity() < System.currentTimeMillis())
			{
				LOGGER.log(Level.FINE, "Attempting to connect to remote host: " + address);
				
				holepunch(remotehost.getHandler().getPort(), remotehost.getUrl());
				
				con = remotehost.getHandler().connect(remotehost.getHost(), remotehost.getPort());
				if (con == null)
				{
					long wait = timeout != null? timeout.getSecondEntity() << 1 : INITIAL_RECONNECT_WAIT;
					wait = Math.min(MAX_RECONNECT_WAIT, wait);
					timeout = new Tuple2<Long, Long>(System.currentTimeMillis() + (long)(Math.random() * wait), wait);
					connectblacklist.put(address, timeout);
					LOGGER.log(Level.FINE, "Connection failed, giving up for some time, current time: " + System.currentTimeMillis() + " next try " + timeout.getFirstEntity() + ", current wait delay " + timeout.getSecondEntity());
				}
				else
				{
					LOGGER.log(Level.FINE, "New Connection to:" + con.getRemoteAddress());
				}
			}
			else
			{
				task.ready(failtask);
			}
		}
		
		if (con != null && con.isConnected())
		{
			connectblacklist.remove(address);
			final Connection fcon = con;
			task.ready(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					byte[] prolog = task.getProlog();
					byte[] data = task.getData();
					byte[] msg = new byte[prolog.length + data.length];
					System.arraycopy(prolog, 0, msg, 0, prolog.length);
					System.arraycopy(data, 0, msg, prolog.length, data.length);
					final Future<Void> ret = new Future<Void>();
					fcon.sendMessage(msg, new IUdpCallback<Boolean>()
					{
						public void resultAvailable(Boolean result)
						{
							if (Boolean.TRUE.equals(result))
							{
								ret.setResult(null);
							}
							else
							{
								ret.setException(new RuntimeException("Send failed, disconnected."));
							}
						}
					});
					return ret;
				}
			});
		}
		else
		{
			task.ready(failtask);
		}
	}
	
	/**
	 *  Returns the prefixes of this transport
	 *  @return Transport prefixes.
	 */
	public String[] getServiceSchemas()
	{
		if (schemas == null)
		{
			List<String> schemalist = new ArrayList<String>();
			if (insecurehandler != null)
				schemalist.add(insecureschema);
			if (securehandler != null)
				schemalist.add(secureschema);
			schemas = schemalist.toArray(new String[schemalist.size()]);
		}
		return schemas;
	}
	
	/**
	 *  Get the addresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		if (addresses == null)
		{
			String[] insecaddrs = insecsolution != null ? insecsolution.getAddresses() : null;
			String[] secaddrs = secsolution != null ? secsolution.getAddresses() : null;
			int count = (insecaddrs != null ? insecaddrs.length : 0) + (secaddrs != null? secaddrs.length : 0);
			String[] addr = new String[count];
			count = 0;
			if (insecaddrs != null)
			{
				System.arraycopy(insecaddrs, 0, addr, 0, insecaddrs.length);
				count = insecaddrs.length;
			}
			if (secaddrs != null)
			{
				System.arraycopy(secaddrs, 0, addr, count, secaddrs.length);
			}
			addresses = addr;
			LOGGER.log(Level.INFO, "UDP addresses: " + Arrays.toString(addresses));
		}
		return addresses;
	}
	
	protected IFirewallSolution getFirewallSolution(String plannedschema, DatagramSocket dgsocket, String hphost, int hpport, int maxprobedelay)
	{
		String myextip = null;
		int remoteudpport = 0;
		
		boolean nat = true;
		boolean fw = true;
		Boolean portbased = null;
		Boolean addrspec = null;
		int cmdtimeout = 2000;
		
		try
		{
//			PortMapping desiredMapping =
//			        new PortMapping(
//			                8123,
//			                "192.168.0.123",
//			                PortMapping.Protocol.UDP,
//			                "My Port Mapping"
//			        );
//
//			UpnpService upnpService =
//			        new UpnpServiceImpl(
//			                new PortMappingListener(desiredMapping)
//			        );
//
//			upnpService.getControlPoint().search();
			
			int sendcount = 5;
			int rcvcount = sendcount * 2;
//			String udpsendcfg = " " + sendcount + " " + ((maxprobedelay >> 1) / sendcount) + "\n";
			String senddelay = String.valueOf((maxprobedelay >> 1) / sendcount);
			dgsocket.setSoTimeout(maxprobedelay / rcvcount);
			
			LOGGER.log(Level.INFO, "Begin probing for firewall for schema: " + plannedschema);
			InetAddress hpaddr = InetAddress.getByName(hphost);
//			hphelper = new Socket(hpaddr, hpport);
//			hphelper.setSoTimeout(8000);
			
			String ret = SHolepunchServerClient.sendCommand(hpserverconf, "showip", null, cmdtimeout);
			
			myextip = ret.substring(8).trim().replaceAll("\\n", "").replaceAll("\\r", "");
			LOGGER.log(Level.INFO, "My external IP is: " + myextip);
			LOGGER.log(Level.INFO, "My local UDP port is: " + dgsocket.getLocalPort());
			
			String[] localaddrs = SUtil.getNetworkAddresses();
			InetAddress myextipaddr = null;
			try
			{
				myextipaddr = InetAddress.getByName(myextip);
			}
			catch(Exception e)
			{
			}
			for (int i = 0; i < localaddrs.length; ++i)
			{
				try
				{
					InetAddress localaddr = InetAddress.getByName(localaddrs[i]);
					if (localaddr.equals(myextipaddr))
					{
						nat = false;
					}
				}
				catch(Exception e)
				{
				}
			}
			
			LOGGER.log(Level.INFO, "NAT " + (nat? "detected." : "not detected."));
			
			byte[] packet = new byte[65536];
			DatagramPacket rdgp = new DatagramPacket(packet, packet.length);
			
			SHolepunchServerClient.sendCommand(hpserverconf, "sendmeudp", new String[] {String.valueOf(dgsocket.getLocalPort()), String.valueOf(sendcount), senddelay}, -1);
			
			int count = rcvcount;
			while (count > 0)
			{
				try
				{
					dgsocket.receive(rdgp);
					if (rdgp.getAddress().equals(hpaddr))
					{
						count = 0;
						fw = false;
					}
				}
				catch (Exception e)
				{
				}
				
				--count;
			}
			
			if (fw)
			{
				LOGGER.log(Level.INFO, "Firewall detected.");
				LOGGER.log(Level.INFO, "Attempting port-based holepunching...");
				
				try
				{
					DatagramPacket sdgp = new DatagramPacket(new byte[0], 0, new InetSocketAddress("8.8.8.8", 10000));
					for (int i = 0; i < 10; ++i)
					{
						dgsocket.send(sdgp);
					}
					
					SHolepunchServerClient.sendCommand(hpserverconf, "sendmeudp", new String[] {String.valueOf(dgsocket.getLocalPort()), String.valueOf(sendcount), senddelay}, -1);
					portbased = Boolean.FALSE;
					count = rcvcount;
					while (count > 0)
					{
						try
						{
							dgsocket.receive(rdgp);
							
							if (rdgp.getAddress().equals(hpaddr))
							{
								count = 0;
								portbased = Boolean.TRUE;
							}
						}
						catch (Exception e)
						{
						}
						
						--count;
					}
					
					if (Boolean.FALSE.equals(portbased))
					{
						LOGGER.log(Level.INFO, "Firewall does not allow port-based holepunching.");
						LOGGER.log(Level.INFO, "Attempting address-specific holepunching....");
						
						ret = SHolepunchServerClient.sendCommand(hpserverconf, "reportudpport", null, cmdtimeout);
						remoteudpport = Integer.parseInt(ret.substring(12).trim().replaceAll("\\n", "").replaceAll("\\r", ""));
//						System.out.println("REMOTE UDP PORT: " + remoteudpport + " " + hpaddr.getHostAddress());
						
						sdgp = new DatagramPacket(new byte[0], 0, new InetSocketAddress(hpaddr.getHostAddress(), remoteudpport));
//						dgp = new DatagramPacket(new byte[1], 1, new InetSocketAddress("127.0.0.1", 9997));
//						dgp.getData()[0] = 'y';
						for (int i = 0; i < 5; ++i)
						{
							dgsocket.send(sdgp);
						}
						
						SHolepunchServerClient.sendCommand(hpserverconf, "sendmeudp", new String[] {String.valueOf(dgsocket.getLocalPort()), String.valueOf(sendcount), senddelay}, -1);
						
						addrspec = Boolean.FALSE;
						count = rcvcount;
						while (count > 0)
						{
							try
							{
								dgsocket.receive(rdgp);
								
								if (rdgp.getAddress().equals(hpaddr))
								{
									count = 0;
									addrspec = Boolean.TRUE;
								}
							}
							catch (Exception e)
							{
//								e.printStackTrace();
							}
							
							--count;
						}
						
						if (Boolean.TRUE.equals(addrspec))
						{
							LOGGER.log(Level.INFO, "Firewall allows address-specific holepunching.");
						}
						else
						{
							LOGGER.log(Level.INFO, "Firewall does not allow address-specific holepunching, all options exhausted.");
						}
					}
					else
					{
						LOGGER.log(Level.INFO, "Firewall allows port-based holepunching.");
					}
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				LOGGER.log(Level.INFO, "No firewall detected or port forwards enabled.");
			}
			
			LOGGER.log(Level.INFO, "Firewall probe finished.");
			dgsocket.setSoTimeout(0);
			String summary = "Fire analysis summary: Firewall ";
			summary += fw? "detected, " : "not detected, ";
			summary += "port-based holepunching " + ((portbased == null) ? "not tested, " : portbased? "available, " : "not available, ");
			summary +=  "NAT " + (nat? "detected, " : "not detected, ");
			summary +=  "address-specific holepunching " + ((addrspec == null) ? "not tested." : addrspec? "available." : "not available.");
			LOGGER.log(Level.INFO, summary);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String solution = "Generating firewall solution: Using ";
		IFirewallSolution ret = null;
		if (!fw)
		{
			ret = new NoSolution(plannedschema, nat? myextip : null, dgsocket.getLocalPort());
		}
		else if (Boolean.TRUE.equals(portbased))
		{
			ret = new PortSolution(plannedschema, nat? myextip : null, dgsocket.getLocalPort(), dgsocket, new InetSocketAddress(hphost, remoteudpport));
		}
		else if (Boolean.TRUE.equals(addrspec))
		{
			ret = new AddressSolution(plannedschema, myextip, dgsocket.getLocalPort(), dgsocket, hpserverconf);
		}
		else
		{
			// Out of options...
			
			ret = new NoSolution(plannedschema, nat? myextip : null, dgsocket.getLocalPort());
		}
		solution += ret.getClass().getSimpleName() + ".";
		LOGGER.log(Level.INFO, solution);
		
		return ret;
	}
	
	protected void holepunch(int localport, String url)
	{
//		System.out.println("Holepunching: " + url);
		try
		{
			SHolepunchServerClient.sendCommand(hpserverconf, "hpreq", new String[] { String.valueOf(localport), url}, -1);
//			System.out.println("Holepunched: " + url);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getLocalServiceCompat(final IInternalAccess component, final Class<T> type, final String scope)
	{
		T ret = null;
		try
		{
			Method m = SServiceProvider.class.getMethod("getLocalService", IInternalAccess.class, Class.class, String.class);
			ret = (T) m.invoke(null, component, type, scope);
		}
		catch (Exception e)
		{
		}
		return ret;
	}
}
