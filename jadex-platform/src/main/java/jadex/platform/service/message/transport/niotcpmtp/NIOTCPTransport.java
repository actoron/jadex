package jadex.platform.service.message.transport.niotcpmtp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.platform.service.message.ISendTask;
import jadex.platform.service.message.transport.ITransport;

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
	public static final String[] SCHEMAS = new String[]{"tcp-mtp://"};
	
	/** How long to keep connections alive (5 min). */
	protected static final int	MAX_KEEPALIVE	= 300000;

	/** The time span for which a failed connection is not retried. */
	public static final long DEADSPAN = 60000;
	
	/** Default port. */
	protected static final int DEFAULT_PORT	= 8765;
	
	//-------- attributes --------
	
	/** The platform. */
	protected IInternalAccess component;
	
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
	public NIOTCPTransport(final IInternalAccess component, int port, Logger logger)
	{
		this.logger = logger;
		this.component = component;
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
//			if (SReflect.isAndroid() && SReflect.getAndroidVersion() <= 8) ...

			// Causes problem with maven too (only with Win firewall?)
			// http://www.thatsjava.com/java-core-apis/28232/
			java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
			java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
			
			final Selector selector = Selector.open();
			
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			
			String[]	addresses	= SUtil.getNetworkAddresses();
			this.addresses	= new String[addresses.length];
			for(int i=0; i<addresses.length; i++)
			{
				for(int j=0; j<getServiceSchemas().length; j++)
				{
					this.addresses[i]	= getAddress(getServiceSchemas()[j], addresses[i], port);
				}
			}
			
			// Start receiver thread.
			SServiceProvider.getService(component, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
			{
				public void customResultAvailable(final IMessageService ms)
				{
					SServiceProvider.getService(component, IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new ExceptionDelegationResultListener<IDaemonThreadPoolService, Void>(ret)
					{
						public void customResultAvailable(IDaemonThreadPoolService tp)
						{
							selectorthread	= new SelectorThread(selector, ms, logger);
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
			if(ssc!=null)
				try{ssc.close();}catch(IOException e2){}
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
//		System.out.println("shutdown: "+this);
		try
		{
			this.ssc.close();
		}
		catch(Exception e)
		{
			logger.warning("Exception during shutdown: "+e);
		}
		selectorthread.shutdown();
		this.shutdown	= true;
		return IFuture.DONE;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a transport is applicable for the target address.
	 *  
	 *  @return True, if the transport is applicable for the address.
	 */
	public boolean	isApplicable(String address)	
	{
		boolean	ret	= false;
		for(int i=0; !ret && i<getServiceSchemas().length; i++)
		{
			ret	= address.startsWith(getServiceSchemas()[i]);
		}
		return ret;
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
		return sec==null || !sec.booleanValue();
	}
	
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
		IResultListener<NIOTCPOutputConnection>	lis	= new IResultListener<NIOTCPOutputConnection>()
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
		};
		IFuture<NIOTCPOutputConnection>	con	= selectorthread.getConnection(parseAddress(address));
		
		// Inform listener immediately if future is done (avoids that niotcp drops behind other transports due to stack unwinding).
		if(con.isDone())
		{
			NIOTCPOutputConnection	res	= null;
			Exception	ex	= null;
			try
			{
				res	= con.get();
			}
			catch(Exception e)
			{
				ex	= e;
			}
			if(ex!=null)
			{
				lis.exceptionOccurred(ex);
			}
			else
			{
				lis.resultAvailable(res);
			}
		}
		else
		{
			con.addResultListener(lis);
		}
	}

	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String[] getServiceSchemas()
	{
		return SCHEMAS;
	}
	
	/**
	 *  Get the addresses of this transport.
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
	protected String getAddress(String schema, String hostname, int port)
	{
		return schema+hostname+":"+port;
	}
	
	/**
	 *  Parse an address.
	 *  @param address The address string.
	 *  @return The parsed address.
	 */
	protected InetSocketAddress	parseAddress(String address)
	{
		InetSocketAddress	ret	= null;
		address = address.toLowerCase();
		
		for(int i=0; i<getServiceSchemas().length; i++)
		{
			if(address.startsWith(getServiceSchemas()[i]))
			{		
				try
				{
					int schemalen = getServiceSchemas()[i].length();
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
					ret	= new InetSocketAddress(getAddress(hostname), port);
				}
				catch(Exception e)
				{ 
				}
			}
		}
		
		return ret;
	}
	
	/** Cache for internet addresses to avoid slow lookup. */
	protected static Map<String, InetAddress>	addresscache	= new LRU<String, InetAddress>();
	
	/**
	 *  
	 */
	protected static InetAddress	getAddress(String hostname)
	{
		boolean	found	= false;
		InetAddress	ret	= null;
		synchronized(addresscache)
		{
			found	= addresscache.containsKey(hostname);
			if(found)
			{
				ret	= addresscache.get(hostname);
			}
		}
		
		if(!found)
		{
			try
			{
				ret	= InetAddress.getByName(hostname);
			}
			catch(UnknownHostException e)
			{
			}
			
			synchronized(addresscache)
			{
				addresscache.put(hostname, ret);
			}
		}
		
		return ret;
	}
}
