package jadex.base.service.message.transport.niotcpmtp;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
			
			String[]	addresses	= SUtil.getNetworkAddresses();
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
}
