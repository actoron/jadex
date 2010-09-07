package jadex.standalone.transport.niotcpmtp;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.clock.ITimedObject;
import jadex.commons.service.clock.ITimer;
import jadex.commons.service.library.ILibraryService;
import jadex.commons.service.threadpool.IThreadPoolService;
import jadex.standalone.transport.ITransport;
import jadex.standalone.transport.MessageEnvelope;
import jadex.standalone.transport.codecs.CodecFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	protected static final int PROLOG_SIZE = 5;
	
	/** Maximum number of outgoing connections */
	protected static final int MAX_CONNECTIONS	= 20;
	
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
	
	/** The selector for fetching new incoming requests. */
	protected Selector selector;
	
	/** The opened connections for addresses. (aid address -> connection). */
	protected Map connections;
	
	/** The codec factory. */
	protected CodecFactory codecfac;
	
	/** The logger. */
	protected Logger logger;
	
	/** The library service. */
	protected ILibraryService libservice;

	//-------- constructors --------
	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public NIOTCPTransport(final IServiceProvider container, int port)
	{
		this.logger = Logger.getLogger("NIOTCPTransport" + this);
		this.codecfac = new CodecFactory();
		this.container = container;
		this.port = port;
		
		// Set up sending side.
		this.connections = SCollection.createLRU(MAX_CONNECTIONS);
		((LRU)this.connections).setCleaner(new ILRUEntryCleaner()
		{
			public void cleanupEldestEntry(Entry eldest)
			{
				Object con = eldest.getValue();
				if(con instanceof NIOTCPOutputConnection)
				{
					((NIOTCPOutputConnection)con).close();
				}
			}
		});
	}
	
	/**
	 *  Start the transport.
	 */
	public void start()
	{
		try
		{
			// Set up receiver side.
			// If port==0 -> any free port
			this.ssc = ServerSocketChannel.open();
			this.ssc.configureBlocking(false);
			ServerSocket serversocket = ssc.socket();
			serversocket.bind(new InetSocketAddress(port));
			this.port = serversocket.getLocalPort();
			this.selector = Selector.open();
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			
			// Determine all transport addresses.
			InetAddress iaddr = InetAddress.getLocalHost();
			String lhostname = iaddr.getHostName().toLowerCase();
			InetAddress[] laddrs = InetAddress.getAllByName(lhostname);
	
			Set addrs = new HashSet();
			addrs.add(getAddress(iaddr.getHostAddress(), this.port));
			
			// Get the ip addresses
			for(int i=0; i<laddrs.length; i++)
			{
				String hostname = laddrs[i].getHostName().toLowerCase();
				String ip_addr = laddrs[i].getHostAddress();
				addrs.add(getAddress(ip_addr, this.port));
				if(!ip_addr.equals(hostname))
				{
					// We have a fully qualified domain name.
					addrs.add(getAddress(hostname, this.port));
				}
			}
			addresses = (String[])addrs.toArray(new String[addrs.size()]);
			
			// Start receiver thread.
			
			SServiceProvider.getService(container, ILibraryService.class).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					libservice = (ILibraryService)result;
					SServiceProvider.getService(container, IThreadPoolService.class).addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							IThreadPoolService tp = (IThreadPoolService)result;
							tp.execute(new Runnable()
							{
								public void run()
								{
									while(ssc.isOpen())
									{
										// This is a blocking call that only returns when traffic occurs.
										Iterator it = null;
										try
										{
											selector.select();
											it = selector.selectedKeys().iterator();
										}
										catch(IOException e)
										{
											logger.warning("NIOTCP selector error.");
											//e.printStackTrace();
										}
										
										while(it!=null && it.hasNext())
										{
								            // Get the selection key
								            final SelectionKey key = (SelectionKey)it.next();
								    
								            // Remove it from the list to indicate that it is being processed
								            it.remove();
								            
											if(key.isValid() && key.isAcceptable())
											{
												try
												{
													// Returns only null if no connection request is available.
													SocketChannel sc = ssc.accept();
													if(sc!=null) 
													{
														sc.configureBlocking(false);
//														ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader();
														sc.register(selector, SelectionKey.OP_READ, new NIOTCPInputConnection(sc, codecfac, libservice.getClassLoader()));
													}
												}
												catch(IOException e)
												{
													logger.warning("NIOTCP connection error on receiver side.");
													//e.printStackTrace();
													key.cancel();
												}
											}
											else if(key.isValid() && key.isReadable())
											{
												final NIOTCPInputConnection con = (NIOTCPInputConnection)key.attachment();
												SServiceProvider.getService(container, IMessageService.class).addResultListener(new DefaultResultListener()
												{
													public void resultAvailable(Object source, Object result)
													{
														try
														{
															IMessageService ms = (IMessageService)result;
															for(MessageEnvelope msg=con.read(); msg!=null; msg=con.read())
															{
																ms.deliverMessage(msg.getMessage(), msg.getTypeName(), msg.getReceivers());
															}
														}
														catch(IOException e)
														{ 
//															logger.warning("NIOTCP receiving error while reading data.");
//															e.printStackTrace();
															con.close();
															key.cancel();
														}
													}
												});
											}
											else
											{
												key.cancel();
											}
										}
									}
									logger.info("TCPNIO receiver closed.");
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
			//e.printStackTrace();
			throw new RuntimeException("Transport initialization error: "+e.getMessage());
		}
	}

	/**
	 *  Perform cleanup operations (if any).
	 */
	public void shutdown()
	{
		try{this.ssc.close();}catch(Exception e){}
		connections = null; // Help gc
	}
	
	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param message The message to send.
	 */
//	public ComponentIdentifier[] sendMessage(IMessageEnvelope message)
	public IComponentIdentifier[] sendMessage(Map message, String msgtype, IComponentIdentifier[] receivers)
	{
		// Fetch all receivers 
		IComponentIdentifier[] recstodel = receivers;
//		IComponentIdentifier[] recstodel = message.getReceivers();
		List undelivered = SUtil.arrayToList(recstodel);
		
		// Find receivers with same address and send only once for 
		// them as message is delivered to all
		// address -> (aid1, aid2, ...)
		MultiCollection adrsets = new MultiCollection(SCollection.createHashMap(), HashSet.class);
		for(int i=0; i<recstodel.length; i++)
		{
			String[] addrs = recstodel[i].getAddresses();
			for(int j=0; j<addrs.length; j++)
			{
				adrsets.put(addrs[j], recstodel[i]);
			}
		}

		// Iterate over all different addresses and try to send
		// to missing and appropriate receivers
		String[] addrs = (String[])adrsets.getKeys(String.class);
		for(int i=0; i<addrs.length && undelivered.size()>0; i++)
		{
			try
			{
				boolean fresh = false;
				// Is the cached connection is dead the call will
				// cause a IOException been thrown
				NIOTCPOutputConnection con = getConnection(addrs[i]);
				if(con==null)
				{
					fresh = true;
					con = createConnection(addrs[i]);
				}
						
				if(con!=null)
				{
					Set aidset = (Set)adrsets.get(addrs[i]);
					aidset.retainAll(undelivered);
//					ComponentIdentifier[] aids = (ComponentIdentifier[])aidset.toArray(new ComponentIdentifier[aidset.size()]);
//					message.setReceivers(aids);
					
					// The send process must be performed once or twice
					// as there is no possibility to check if the cached connection
					// is still connected to the other end. This can only be
					// checked by the write operation.
					while(true)
					{
						try
						{
							con.send(new MessageEnvelope(message, aidset, msgtype));
							undelivered.removeAll(aidset);
							break;
						}
						catch(IOException e)
						{
							removeConnection(addrs[i]);
							if(!fresh)
							{
								fresh = true;
								con = createConnection(addrs[i]);
								if(con==null)
									break;
							}
							else
							{
								logger.warning("Send connection closed: "+addrs[i]);
								break;
							}
						}
					}
				}
			}
			catch(IOException e)
			{
//				logger.warning("Address unreachable: "+addrs[i]);
			}
		}
		
		return (ComponentIdentifier[])undelivered.toArray(new ComponentIdentifier[undelivered.size()]);
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
	 *  Get the cached connection.
	 *  @param address The address.
	 *  @return The cached connection.
	 */
	protected NIOTCPOutputConnection getConnection(String address) throws IOException
	{
		address = address.toLowerCase();
		
		Object ret = connections.get(address);
		if(ret instanceof NIOTCPDeadConnection)
		{
			NIOTCPDeadConnection dead = (NIOTCPDeadConnection)ret;
			// Reset connection if connection should be retried.
			if(dead.shouldRetry())
			{
				connections.remove(address);
				ret = null; 
			}
			else
			{
				throw new IOException("Dead connection.");
			}
		}
		return (NIOTCPOutputConnection)ret;
	}
	
	/**
	 *  Remove a cached connection.
	 *  @param address The address.
	 */
	protected void removeConnection(String address)
	{
		address = address.toLowerCase();
		
		NIOTCPOutputConnection con = (NIOTCPOutputConnection)connections.remove(address);
		if(con!=null)
			con.close();
	}
	
	/**
	 *  Create a outgoing connection.
	 *  @param address The connection address.
	 *  @return the connection to this address
	 */
	protected NIOTCPOutputConnection createConnection(String address)
	{
		address = address.toLowerCase();
		
		NIOTCPOutputConnection ret = null;
		
		if(address.startsWith(getServiceSchema()))
		{
			// Parse the address
			try
			{
				int schemalen = getServiceSchema().length();
				int div = address.indexOf(':', schemalen);
				String hostname;
				int iport;
				if(div>0)
				{
					hostname = address.substring(schemalen, div);
					iport = Integer.parseInt(address.substring(div+1));
				}
				else
				{
					hostname = address.substring(schemalen);
					iport = DEFAULT_PORT;
				}
			
//				ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader()
				ret = new NIOTCPOutputConnection(InetAddress.getByName(hostname), iport, codecfac, new Cleaner(address), libservice.getClassLoader());
				connections.put(address, ret);
			}
			catch(Exception e)
			{ 
				connections.put(address, new NIOTCPDeadConnection());
//				logger.warning("Could not establish connection to: "+address);
				//e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Class for cleaning output connections after 
	 *  max keep alive time has been reached.
	 */
	protected class Cleaner implements ITimedObject
	{
		//-------- attributes --------
		
		/** The address of the connection. */
		protected String address;
		
		/** The timer. */
		protected ITimer timer;
		
		//-------- constructors --------
		
		/**
		 *  Cleaner for a specified output connection.
		 *  @param address The address.
		 */
		public Cleaner(String address)
		{
			this.address = address;
		}
		
		//-------- methods --------
		
		/**
		 *  Called when timepoint was reached.
		 */
		public void timeEventOccurred(long currenttime)
		{
			//System.out.println("Timeout reached for: "+address);
			removeConnection(address);
		}
		
		/**
		 *  Refresh the timeout.
		 */
		public void refresh()
		{
			//platform.getTimerService().addEntry(this, System.currentTimeMillis()+MAX_KEEPALIVE);
			/*if(timer!=null)
				timer.cancel();
			timer = platform.getClock().createTimer(System.currentTimeMillis()+MAX_KEEPALIVE, this);*/
			SServiceProvider.getService(container, IClockService.class).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IClockService clock = (IClockService)result;
					long time = clock.getTime()+MAX_KEEPALIVE;
					if(timer==null)
						timer = clock.createTimer(time, Cleaner.this);
					else
						timer.setNotificationTime(time);
				}
			});
		}
		
		/**
		 *  Remove this cleaner.
		 */
		public void remove()
		{
			//platform.getTimerService().removeEntry(this);
			if(timer!=null)
				timer.cancel();
		}
	}
}
