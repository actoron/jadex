package jadex.base.service.message.transport.niotcpmtp;

import jadex.base.service.message.ISendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedHashSet;
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
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		try
		{
			// Set up receiver side.
			// If port==0 -> any free port
			this.ssc = ServerSocketChannel.open();
			this.ssc.configureBlocking(false);
			ServerSocket serversocket = ssc.socket();
			serversocket.bind(new InetSocketAddress(port));
			this.port = serversocket.getLocalPort();
			
			// ANDROID: Selector.open() causes an exception in a 2.2
			// emulator due to IPv6 addresses, see:
			// http://code.google.com/p/android/issues/detail?id=9431
			/* $if android && androidVersion < 9 $
			java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
			java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
			$endif $ */
			
			// Causes problem with maven too (only with Win firewall?)
			// http://www.thatsjava.com/java-core-apis/28232/
			final Selector selector = Selector.open();
			
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			
			String[]	addresses	= SUtil.getNetworkAddresses();
			this.addresses	= new String[addresses.length];
			for(int i=0; i<addresses.length; i++)
			{
				this.addresses[i]	= getAddress(addresses[i], port);
			}
			
			// Start receiver thread.
			SServiceProvider.getService(container, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
			{
				public void customResultAvailable(final IMessageService ms)
				{
					SServiceProvider.getService(container, IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<IThreadPoolService, Void>(ret)
					{
						public void customResultAvailable(IThreadPoolService tp)
						{
							selectorthread	= new SelectorThread(selector, ms, logger, container);
							tp.execute(selectorthread);
							ret.setResult(null);
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
	public IFuture<Void> shutdown()
	{
		try{this.ssc.close();}catch(Exception e){}
		selectorthread.setRunning(false);
		this.shutdown	= true;
		return IFuture.DONE;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a transport is applicable for the message.
	 *  
	 *  @return True, if the transport is applicable for the message.
	 */
	public boolean	isApplicable(ISendTask task)
	{
		boolean	ret	= false;
		for(int i=0; !ret && i<task.getReceivers().length; i++)
		{
			String[]	raddrs	= task.getReceivers()[i].getAddresses();
			for(int j=0; !ret && j<raddrs.length; j++)
			{
				ret	= raddrs[j].toLowerCase().startsWith(getServiceSchema());
			}			
		}
		return ret;
	}
	
	/**
	 *  Send a message to receivers on the same platform.
	 *  This method is called concurrently for all transports.
	 *  Each transport should immediately announce its interest and try to connect to the target platform
	 *  (or reuse an existing connection) and afterwards acquire the token for the task.
	 *  
	 *  The first transport that acquires the token (i.e. the first connected transport) tries to send the message.
	 *  If sending fails, it may release the token to trigger the other transports.
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param task The message to send.
	 *  @return True, if the transport is applicable for the message.
	 */
	public void	sendMessage(final ISendTask task)
	{
		InetSocketAddress[] addresses = fetchAddresses(task.getReceivers());
		selectorthread.getConnection(addresses).addResultListener(new IResultListener<NIOTCPOutputConnection>()
		{
			public void resultAvailable(final NIOTCPOutputConnection con)
			{
				IResultCommand<IFuture<Void>, Void>	send	= new IResultCommand<IFuture<Void>, Void>()
				{
					public IFuture<Void> execute(Void args)
					{
						return selectorthread.sendMessage(con, task.getProlog(), task.getData());
					}
				};
				task.ready(send);
			}
			public void exceptionOccurred(final Exception exception)
			{
				IResultCommand<IFuture<Void>, Void>	send	= new IResultCommand<IFuture<Void>, Void>()
				{
					public IFuture<Void> execute(Void args)
					{
						return new Future<Void>(exception);
					}
				};
				task.ready(send);
			}
		});
	}

	/**
	 *  Fetch addresses correponding to receivers of a send task.
	 */
	protected InetSocketAddress[] fetchAddresses(IComponentIdentifier[] receivers)
	{
		// Fetch all addresses
		Set<InetSocketAddress>	addrs	= new LinkedHashSet<InetSocketAddress>();
		for(int i=0; i<receivers.length; i++)
		{
			String[]	raddrs	= receivers[i].getAddresses();
			if(raddrs==null || raddrs.length==0)
				throw new RuntimeException("Adresses must not null: "+receivers[i]);
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
		return addresses;
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
}
