package jadex.base.service.message.transport.niotcpmtp;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  The tcp transport for sending messages over
 *  tcp/ip connections. Initiates one receiving
 *  tcp/ip port under the specified settings and
 *  opens outgoing connections for all remote 
 *  platforms on demand.
 *
 *  For the receiving side a separate listener
 *  thread is necessary as it must be continuously
 *  listened for incoming transmission requests.
 */
public class NIOTCPTransport implements ITransport
{
	//-------- constants --------
	
	/** The schema name. */
	public static final String SCHEMA = "nio-mtp://";
	
	/** How long to keep connections alive (5 min). */
	protected static final int	MAX_KEEPALIVE	= 300000;

	/** The prolog size. */
	protected static final int PROLOG_SIZE = 4;
	
	/** Default port. */
	protected static final int DEFAULT_PORT	= 8765;
	
	//-------- attributes --------
	
	/** The platform. */
	protected IServiceProvider container;
	
	/** The addresses. */
	protected String[] addresses;
	
	/** The port. */
	protected int port;
	
	/** The server socket for receiving messages. */
	protected ServerSocketChannel ssc;
	
	/** The logger. */
	protected Logger logger;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The codec factory. */
	protected CodecFactory codecfac;
	
	/** The selector thread. */
	protected SelectorThread	selectorthread;
	
	/** Flag indicating that the transport was shut down. */
	protected boolean	shutdown;
	
	//-------- constructors --------
	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public NIOTCPTransport(final IServiceProvider container, int port, Logger logger)
	{
		this.logger = logger;
		this.container = container;
		this.port = port;		
	}
	
	/**
	 *  Start the transport.
	 */
	public IFuture start()
	{
		final Future ret = new Future();
		try
		{
			// Set up receiver side.
			// If port==0 -> any free port
			this.ssc = ServerSocketChannel.open();
			this.ssc.configureBlocking(false);
			ServerSocket serversocket = ssc.socket();
			serversocket.bind(new InetSocketAddress(port));
			this.port = serversocket.getLocalPort();
			
			// ANDROID: the following line causes an exception in a 2.2
			// emulator, see:
			// http://code.google.com/p/android/issues/detail?id=9431
			
			// Causes problem with maven too (only with Win firewall?)
			// http://www.thatsjava.com/java-core-apis/28232/
			final Selector selector = Selector.open();
			
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			
			String[]	addresses	= getNetworkAddresses();
			this.addresses	= new String[addresses.length];
			for(int i=0; i<addresses.length; i++)
			{
				this.addresses[i]	= getAddress(addresses[i], port);
			}
			
			// Start receiver thread.
			SServiceProvider.getService(container, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IMessageService ms = (IMessageService)result;
					codecfac = (CodecFactory)ms.getCodecFactory();
					SServiceProvider.getService(container, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							libservice = (ILibraryService)result;
							
							SServiceProvider.getService(container, IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
								.addResultListener(new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object result)
								{
									selectorthread	= new SelectorThread(selector, ms, codecfac, libservice, logger, container);
									IThreadPoolService tp = (IThreadPoolService)result;
									tp.execute(selectorthread);
									ret.setResult(null);
								}
							});
						}
					});		
				}
			});
			//platform.getLogger().info("Local address: "+getServiceSchema()+lhostname+":"+listen_port);
		}
		catch(Exception e)
		{
			ret.setException(new RuntimeException("Transport initialization error: "+e.getMessage()));
			e.printStackTrace();
//			throw new RuntimeException("Transport initialization error: "+e.getMessage());
		}
		
		return ret;
	}

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture shutdown()
	{
		try{this.ssc.close();}catch(Exception e){}
		selectorthread.setRunning(false);
		this.shutdown	= true;
		return new Future(null);
	}
	
	//-------- methods --------
	
	/**
	 *  Send a message to receivers, which are all on the same platform.
	 *  @param message The message to send.
	 *  @param msgtype The message type.
	 *  @param receivers	The intended receivers.
	 *  @param codecids	codecs to be used (if any).
	 *  
	 *  Can be called concurrently by SendManagers of message service.
	 */
//	public synchronized IFuture sendMessage(final Map message, final String msgtype, final IComponentIdentifier[] receivers, final byte[] codecids)
	public synchronized IFuture sendMessage2(final ManagerSendTask task)
	{
		final Future	ret	= new Future();
		
		// Fetch all addresses
		Set	addrs	= new LinkedHashSet();
		for(int i=0; i<task.getReceivers().length; i++)
		{
			String[]	raddrs	= task.getReceivers()[i].getAddresses();
			for(int j=0; j<raddrs.length; j++)
			{
				InetSocketAddress	address	= parseAddress(raddrs[j]);
				if(address!=null)
				{
					addrs.add(address);
				}
			}
			
		}
		InetSocketAddress[]	addresses	= (InetSocketAddress[])addrs.toArray(new InetSocketAddress[addrs.size()]);
		
		selectorthread.getConnection(addresses).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				NIOTCPOutputConnection	con	= (NIOTCPOutputConnection)result;
//				selectorthread.sendMessage(con, new MessageEnvelope(message, Arrays.asList(receivers), msgtype), codecids)
				selectorthread.sendMessage(con, task.getProlog(), task.getData())
					.addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Send a message.
	 *  @param message The message to send.
	 */
	public IFuture<Void>	sendMessage(final ManagerSendTask task)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Fetch all addresses
		Set	addrs	= new LinkedHashSet();
		for(int i=0; i<task.getReceivers().length; i++)
		{
			String[]	raddrs	= task.getReceivers()[i].getAddresses();
			for(int j=0; j<raddrs.length; j++)
			{
				InetSocketAddress	address	= parseAddress(raddrs[j]);
				if(address!=null)
				{
					addrs.add(address);
				}
			}
			
		}
		InetSocketAddress[]	addresses	= (InetSocketAddress[])addrs.toArray(new InetSocketAddress[addrs.size()]);
		
		selectorthread.getConnection(addresses).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				NIOTCPOutputConnection	con	= (NIOTCPOutputConnection)result;
//				selectorthread.sendMessage(con, new MessageEnvelope(message, Arrays.asList(receivers), msgtype), codecids)
				selectorthread.sendMessage(con, task.getProlog(), task.getData())
					.addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String getServiceSchema()
	{
		return SCHEMA;
	}
	
	/**
	 *  Get the adresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		return addresses;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the address of this transport.
	 *  @param hostname The hostname.
	 *  @param port The port.
	 *  @return <scheme>:<hostname>:<port>
	 */
	protected String getAddress(String hostname, int port)
	{
		return getServiceSchema()+hostname+":"+port;
	}
	
	/**
	 *  Parse an address.
	 *  @param address The address string.
	 *  @return The parsed address.
	 */
	protected static InetSocketAddress	parseAddress(String address)
	{
		InetSocketAddress	ret	= null;
		address = address.toLowerCase();
		
		if(address.startsWith(SCHEMA))
		{		
			try
			{
				int schemalen = SCHEMA.length();
				int div = address.lastIndexOf(':');
				String hostname;
				int port;
				if(div>schemalen)
				{
					hostname = address.substring(schemalen, div);
					port = Integer.parseInt(address.substring(div+1));
				}
				else
				{
					hostname = address.substring(schemalen);
					port = DEFAULT_PORT;
				}
				ret	= new InetSocketAddress(hostname, port);
			}
			catch(Exception e)
			{ 
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the addresses to be used for the transport.
	 */
	protected static String[]	getNetworkAddresses() throws SocketException
	{
		// Determine useful transport addresses.
		Set<String>	addresses	= new HashSet<String>();	// global network addresses (uses all)
		Set<InetAddress>	sitelocal	= new HashSet<InetAddress>();	// local network addresses e.g. 192.168.x.x (use one v4 and one v6 if no global)
		Set<InetAddress>	linklocal	= new HashSet<InetAddress>();	// link-local fallback addresses e.g. 169.254.x.x (use one v4 and one v6 if no global or local)
		Set<InetAddress>	loopback	= new HashSet<InetAddress>();	// loopback addresses e.g. 127.0.0.1 (use one v4 and one v6 if no global or local or link-local)
		
		boolean	v4	= false;	// true when one v4 address was added.
		boolean	v6	= false;	// true when one v6 address was added.
		
		for(Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); )
		{
			NetworkInterface ni = nis.nextElement();
			for(Enumeration<InetAddress> iadrs = ni.getInetAddresses(); iadrs.hasMoreElements(); )
			{
				InetAddress addr = iadrs.nextElement();
				System.out.println("addr: "+addr+" "+addr.isAnyLocalAddress()+" "+addr.isLinkLocalAddress()+" "+addr.isLoopbackAddress()+" "+addr.isSiteLocalAddress()+", "+ni.getDisplayName());
				if(addr.isLoopbackAddress())
				{
					loopback.add(addr);
				}
				else if(addr.isLinkLocalAddress())
				{
					linklocal.add(addr);
				}
				else if(addr.isSiteLocalAddress())
				{
					sitelocal.add(addr);
				}
				else
				{
					v4	= v4 || addr instanceof Inet4Address;
					v6	= v6 || addr instanceof Inet6Address;
					addresses.add(addr.getHostAddress());
				}
			}
		}

		boolean	tmpv4	= v4;
		boolean	tmpv6	= v6;
		for(Iterator<InetAddress> it=sitelocal.iterator(); it.hasNext(); )
		{
			InetAddress	addr	= it.next();
			if(!tmpv4 && addr instanceof Inet4Address || !tmpv6 && addr instanceof Inet6Address)
			{
				v4	= v4 || addr instanceof Inet4Address;
				v6	= v6 || addr instanceof Inet6Address;
				addresses.add(addr.getHostAddress());
			}
		}
		
		tmpv4	= v4;
		tmpv6	= v6;
		for(Iterator<InetAddress> it=linklocal.iterator(); it.hasNext(); )
		{
			InetAddress	addr	= it.next();
			if(!tmpv4 && addr instanceof Inet4Address || !tmpv6 && addr instanceof Inet6Address)
			{
				v4	= v4 || addr instanceof Inet4Address;
				v6	= v6 || addr instanceof Inet6Address;
				addresses.add(addr.getHostAddress());
			}
		}
		
		tmpv4	= v4;
		tmpv6	= v6;
		for(Iterator<InetAddress> it=loopback.iterator(); it.hasNext(); )
		{
			InetAddress	addr	= it.next();
			if(!tmpv4 && addr instanceof Inet4Address || !tmpv6 && addr instanceof Inet6Address)
			{
				v4	= v4 || addr instanceof Inet4Address;
				v6	= v6 || addr instanceof Inet6Address;
				addresses.add(addr.getHostAddress());
			}
		}
		
//		InetAddress iaddr = InetAddress.getLocalHost();
//		String lhostname = iaddr.getCanonicalHostName();
//		InetAddress[] laddrs = InetAddress.getAllByName(lhostname);
//
//		addrs.add(getAddress(iaddr.getHostAddress(), this.port));
//		// Get the ip addresses
//		for(int i=0; i<laddrs.length; i++)
//		{
//			String hostname = laddrs[i].getHostName().toLowerCase();
//			String ip_addr = laddrs[i].getHostAddress();
//			addrs.add(getAddress(ip_addr, this.port));
//			if(!ip_addr.equals(hostname))
//			{
//				// We have a fully qualified domain name.
//				addrs.add(getAddress(hostname, this.port));
//			}
//		}
		
		System.out.println("addresses: "+addresses);
		
		return (String[])addresses.toArray(new String[addresses.size()]);		
	}
}
