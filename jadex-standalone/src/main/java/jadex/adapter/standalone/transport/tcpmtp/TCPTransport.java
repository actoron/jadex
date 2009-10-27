package jadex.adapter.standalone.transport.tcpmtp;

import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.adapter.standalone.transport.ITransport;
import jadex.adapter.standalone.transport.MessageEnvelope;
import jadex.adapter.standalone.transport.codecs.CodecFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IMessageService;
import jadex.bridge.IPlatform;
import jadex.commons.SUtil;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;
import jadex.service.library.ILibraryService;
import jadex.service.threadpool.ThreadPoolService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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
public class TCPTransport implements ITransport
{
	//-------- constants --------
	
	/** The schema name. */
	public final static String SCHEMA = "tcp-mtp://";
	
	/** Constant for asynchronous setting. */
	public final static String ASYNCHRONOUS = "asynchronous";
	
	/** The receiving port. */
	public final static String PORT = "port";
	
	/** How long to keep output connections alive (5 min). */
	protected final static int	MAX_KEEPALIVE	= 300000;

	/** The prolog size. */
	protected final static int PROLOG_SIZE = 5;
	
	/** 2MB as message buffer */
	protected final static int BUFFER_SIZE	= 1024 * 1024 * 2;
	
	/** Maximum number of outgoing connections */
	protected final static int MAX_CONNECTIONS	= 10;
	
	/** Default port. */
	protected final static int DEFAULT_PORT	= 9876;
	
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform platform;
	
	/** The addresses. */
	protected String[] addresses;
	
	/** The port. */
	protected int port;
	
	/** The server socket for receiving messages. */
	protected ServerSocket serversocket;
	
	/** The opened connections for addresses. (aid address -> connection). */
	protected Map connections;
	
	/** Should be received asynchronously? One thread for receiving is
		unavoidable. Async defines if the receival should be done on a
		new thread always or on the one receiver thread. */
	protected boolean async;
	
	/** The codec factory. */
	protected CodecFactory codecfac;
	
	/** The logger. */
	protected Logger logger;
	
	//-------- constructors --------
	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public TCPTransport(final IPlatform platform, int port)
	{
		this(platform, port, true);
	}

	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public TCPTransport(final IPlatform platform, int port, final boolean async)
	{
		this.logger = Logger.getLogger("TCPTransport" + this);
		this.codecfac = new CodecFactory();
		
		this.platform = platform;
		this.async = async;
		this.port = port;
		
		// Set up sending side.
		this.connections = SCollection.createLRU(MAX_CONNECTIONS);
		((LRU)this.connections).setCleaner(new ILRUEntryCleaner()
		{
			public void cleanupEldestEntry(Entry eldest)
			{
				TCPOutputConnection con = (TCPOutputConnection)eldest.getValue();
				con.close();
			}
		});
		this.connections = Collections.synchronizedMap(this.connections);
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
			this.serversocket = new ServerSocket(port);
			this.port = serversocket.getLocalPort();

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
			
			// Start the receiver thread.
			((IThreadPool)platform.getService(ThreadPoolService.class)).execute(new Runnable()
			{
				public void run()
				{
					//try{serversocket.setSoTimeout(10000);} catch(SocketException e) {}
					while(!serversocket.isClosed())
					{
						try
						{
							ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
							final TCPInputConnection con = new TCPInputConnection(serversocket.accept(), codecfac, cl);
							if(!async)
							{
								TCPTransport.this.deliverMessages(con);
							}
							else
							{
								// Each accepted incoming connection request is handled
								// in a separate thread in async mode.
								((IThreadPool)platform.getService(ThreadPoolService.class)).execute(new Runnable()
								{
									public void run()
									{
										TCPTransport.this.deliverMessages(con);
									}
								});
							}
						}
						catch(IOException e)
						{
							//logger.warning("TCPTransport receiver connect error: "+e);
							//e.printStackTrace();
						}
					}
					logger.warning("TCPTransport serversocket closed.");
				}
			});
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
		try{this.serversocket.close();}catch(Exception e){}
		connections = null; // Help gc
	}
	
	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param message The message to send.
	 *  (todo: On which thread this should be done?)
	 */
	public AgentIdentifier[] sendMessage(Map msg, String type, IComponentIdentifier[] receivers)
	{
		// Fetch all receivers 
//		IComponentIdentifier[] recstodel = message.getReceivers();
		IComponentIdentifier[] recstodel = receivers;
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
		for(int i=0; i<addrs.length; i++)
		{
			TCPOutputConnection con = getConnection(addrs[i]);
			if(con!=null)
			{
				Set aidset = (Set)adrsets.get(addrs[i]);
				aidset.retainAll(undelivered);
//				AgentIdentifier[] aids = (AgentIdentifier[])aidset.toArray(new AgentIdentifier[aidset.size()]);
				if(con.send(new MessageEnvelope(msg, aidset, type)))
					undelivered.removeAll(aidset);
			}
		}
		
		return (AgentIdentifier[])undelivered.toArray(new AgentIdentifier[undelivered.size()]);
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
	 *  Get the connection.
	 *  @param address
	 *  @return a connection of this type
	 */
	protected TCPOutputConnection getConnection(String address)
	{
		address = address.toLowerCase();
		
		Object ret = connections.get(address);
		if(ret instanceof TCPOutputConnection && ((TCPOutputConnection)ret).isClosed())
		{
			removeConnection(address);
			ret = null;
		}
		
		if(ret instanceof TCPDeadConnection)
		{
			TCPDeadConnection dead = (TCPDeadConnection)ret;
			// Reset connection if connection should be retried.
			if(dead.shouldRetry())
			{
				connections.remove(address);
				ret = null; 
			}
		}
		
		if(ret==null)
			ret = createConnection(address);
		if(ret instanceof TCPDeadConnection)
			ret = null;
		
		return (TCPOutputConnection)ret;
	}
	
	/**
	 *  Create a outgoing connection.
	 *  @param address The connection address.
	 *  @return the connection to this address
	 */
	protected TCPOutputConnection createConnection(String address)
	{
		TCPOutputConnection ret = null;
		
		address = address.toLowerCase();
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

				ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
				ret = new TCPOutputConnection(InetAddress.getByName(hostname), iport, codecfac, new Cleaner(address), cl);
				connections.put(address, ret);
			}
			catch(Exception e)
			{ 
				connections.put(address, new TCPDeadConnection());
				
				logger.warning("Could not create connection: "+e.getMessage());
				//e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove a cached connection.
	 *  @param address The address.
	 */
	protected void removeConnection(String address)
	{
		address = address.toLowerCase();
		
		Object con = connections.remove(address);
		if(con instanceof TCPOutputConnection)
			((TCPOutputConnection)con).close();
	}
	
	/**
	 *  Deliver messages to local message service
	 *  for disptaching to the agents.
	 *  @param con The connection.
	 */
	protected void deliverMessages(TCPInputConnection con)
	{
		try
		{
			for(MessageEnvelope msg=con.read(); msg!=null; msg=con.read())
			{
				((IMessageService)platform.getService(IMessageService.class))
				.deliverMessage(msg.getMessage(), msg.getTypeName(), msg.getReceivers());
			}
		}
		catch(Exception e)
		{
			logger.warning("TCPTransport receiving error: "+e);
			e.printStackTrace();
			con.close();
		}
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
			IClockService clock = (IClockService)platform.getService(IClockService.class);
			long time = clock.getTime()+MAX_KEEPALIVE;
			if(timer==null)
				timer = clock.createTimer(time, this);
			else
				timer.setNotificationTime(time);
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
