package jadex.base.service.message.transport.tcpmtp;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IMessageService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/* $if !android $ */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/* $endif $ */
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

/* $if !android $ */
import javax.swing.Timer;
/* $else $
import jadex.base.service.message.Timer;
import jadex.base.service.message.TimerListener;
$endif $ */

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
	protected final static int PROLOG_SIZE = 4;
	
	/** 2MB as message buffer */
	protected final static int BUFFER_SIZE	= 1024 * 1024 * 2;
	
	/** Maximum number of outgoing connections */
	protected final static int MAX_CONNECTIONS	= 20;
	
	/** Default port. */
	protected final static int DEFAULT_PORT	= 9876;
	
	//-------- attributes --------
	
	/** The platform. */
	protected IServiceProvider container;
	
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
	
	/** The library service. */
	protected ILibraryService libservice;
	
	//-------- constructors --------
	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public TCPTransport(final IServiceProvider container, int port)
	{
		this(container, port, true);
	}

	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public TCPTransport(final IServiceProvider container, int port, final boolean async)
	{
		this.logger = Logger.getLogger("TCPTransport" + this);
		
		this.container = container;
		this.async = async;
		this.port = port;
		
		// Set up sending side.
		this.connections = SCollection.createLRU(MAX_CONNECTIONS);
		((LRU)this.connections).setCleaner(new ILRUEntryCleaner()
		{
			public void cleanupEldestEntry(Entry eldest)
			{
				Object con = eldest.getValue();
				if(con instanceof TCPOutputConnection)
				{
					((TCPOutputConnection)con).close();
				}
			}
		});
		this.connections = Collections.synchronizedMap(this.connections);
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
			this.serversocket = new ServerSocket(port);
			this.port = serversocket.getLocalPort();

			
			// Determine all transport addresses.
			InetAddress iaddr = InetAddress.getLocalHost();
			String lhostname = iaddr.getCanonicalHostName();
			InetAddress[] laddrs = InetAddress.getAllByName(lhostname);
	
			Set addrs = new HashSet();
			for(Enumeration nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); )
			{
				NetworkInterface ni = (NetworkInterface)nis.nextElement();
				for(Enumeration iadrs = ni.getInetAddresses(); iadrs.hasMoreElements(); )
				{
					addrs.add(getAddress(((InetAddress)iadrs.nextElement()).getHostAddress(), this.port));
				}
			}
			
//			// Determine all transport addresses.
//			InetAddress iaddr = InetAddress.getLocalHost();
//			String lhostname = iaddr.getHostName().toLowerCase();
//			InetAddress[] laddrs = InetAddress.getAllByName(lhostname);
//
//			Set addrs = new HashSet();
//			addrs.add(getAddress(iaddr.getHostAddress(), this.port));
//			
//			// Get the ip addresses
//			for(int i=0; i<laddrs.length; i++)
//			{
//				String hostname = laddrs[i].getHostName().toLowerCase();
//				String ip_addr = laddrs[i].getHostAddress();
//				addrs.add(getAddress(ip_addr, this.port));
//				if(!ip_addr.equals(hostname))
//				{
//					// We have a fully qualified domain name.
//					addrs.add(getAddress(hostname, this.port));
//				}
//			}
			
			addresses = (String[])addrs.toArray(new String[addrs.size()]);
			
			// Start the receiver thread.
			
			SServiceProvider.getService(container, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					IMessageService ms = (IMessageService)result;
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
									ret.setResult(null);
									final IThreadPoolService tp = (IThreadPoolService)result;
									tp.execute(new Runnable()
									{
										List openincons = Collections.synchronizedList(new ArrayList());
										public void run()
										{
											//try{serversocket.setSoTimeout(10000);} catch(SocketException e) {}
											while(!serversocket.isClosed())
											{
												try
												{
		//											ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader();
		//											System.out.println("accepting");
													
													// todo: which resource identifier to use for incoming connections?
													ClassLoader cl = getClass().getClassLoader(); // libservice.getClassLoader(null)
													final TCPInputConnection con = new TCPInputConnection(serversocket.accept(), codecfac, cl);
													openincons.add(con);
													if(!async)
													{
														TCPTransport.this.deliverMessages(con).addResultListener(new IResultListener()
														{
															public void resultAvailable(Object result)
															{
																openincons.remove(con);
															}
															
															public void exceptionOccurred(Exception exception)
															{
																openincons.remove(con);
															}
														});
													}
													else
													{
														// Each accepted incoming connection request is handled
														// in a separate thread in async mode.
														tp.execute(new Runnable()
														{
															public void run()
															{
																TCPTransport.this.deliverMessages(con).addResultListener(new IResultListener()
																{
																	public void resultAvailable(Object result)
																	{
																		openincons.remove(con);
																	}
																	
																	public void exceptionOccurred(Exception exception)
																	{
																		openincons.remove(con);
																	}
																});
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
											
											TCPInputConnection[] incons = (TCPInputConnection[])openincons.toArray(new TCPInputConnection[0]);
											for(int i=0; i<incons.length; i++)
											{
//												System.out.println("close: "+incons[i]);
												incons[i].close();
											}
//											logger.warning("TCPTransport serversocket closed.");
										}
									});
								}
							});
						}
					});
				}
			});
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			ret.setException(new RuntimeException("Transport initialization error: "+e.getMessage()));
//			throw new RuntimeException("Transport initialization error: "+e.getMessage());
		}
		return ret;
	}
	
	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture shutdown()
	{
		try{this.serversocket.close();}catch(Exception e){}
		connections = null; // Help gc
		return new Future(null);
	}
	
	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param message The message to send.
	 *  (todo: On which thread this should be done?)
	 */
	public IFuture sendMessage(Map msg, String type, IComponentIdentifier[] receivers, byte[] codecids)
	{
		// Fetch all addresses
		Set	addresses	= new LinkedHashSet();
		for(int i=0; i<receivers.length; i++)
		{
			String[]	raddrs	= receivers[i].getAddresses();
			for(int j=0; j<raddrs.length; j++)
			{
				addresses.add(raddrs[j]);
			}			
		}

		// Iterate over all different addresses and try to send
		// to missing and appropriate receivers
		String[] addrs = (String[])addresses.toArray(new String[addresses.size()]);
		boolean	delivered	= false;
		for(int i=0; !delivered && i<addrs.length; i++)
		{
			TCPOutputConnection con = getConnection(addrs[i]);
			if(con!=null)
			{
				delivered	= con.send(new MessageEnvelope(msg, Arrays.asList(receivers), type), codecids);
			}
		}
		
		return delivered ? IFuture.DONE : new Future(new RuntimeException("Could not deliver message"));
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
			// todo: handle V6 ip adresses (0:0:0:0 ...)
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

//				ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader();
				
				// todo: which resource identifier to use for outgoing connections?
				ClassLoader cl = getClass().getClassLoader(); // libservice.getClassLoader(null)
				ret = new TCPOutputConnection(InetAddress.getByName(hostname), iport, codecfac, new Cleaner(address), cl);
				connections.put(address, ret);
			}
			catch(Exception e)
			{ 
				connections.put(address, new TCPDeadConnection());
				
//				logger.warning("Could not create connection: "+e.getMessage());
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
	 *  for disptaching to the components.
	 *  @param con The connection.
	 */
	protected IFuture deliverMessages(final TCPInputConnection con)
	{
		final Future ret = new Future();
		SServiceProvider.getService(container, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IMessageService ms = (IMessageService)result;
				try
				{
					for(MessageEnvelope msg=con.read(); msg!=null; msg=con.read())
					{
						ms.deliverMessage(msg.getMessage(), msg.getTypeName(), msg.getReceivers());
					}
					con.close();
					ret.setResult(null);
				}
				catch(Exception e)
				{
//					logger.warning("TCPTransport receiving error: "+e);
//					e.printStackTrace();
					con.close();
					ret.setException(e);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Class for cleaning output connections after 
	 *  max keep alive time has been reached.
	 */
	/* $if !android $ */
	protected class Cleaner implements ActionListener
	/* $else $
	protected class Cleaner implements TimerListener
	$endif $ */
	{
		//-------- attributes --------
		
		/** The address of the connection. */
		protected String address;
		
		/** The timer. */
		// Hack!!! java.util.timer does not support cancellation of scheduled tasks.
		protected Timer timer;
		
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
		/* $if !android $ */
	    public void actionPerformed(ActionEvent e)
		/* $else       
		public void actionPerformed()
	    $endif $ */
		{
			logger.info("Timeout reached for: "+address);
			removeConnection(address);
		}
		
		/**
		 *  Refresh the timeout.
		 */
		public void refresh()
		{
			if(timer==null)
			{
				timer	= new Timer(MAX_KEEPALIVE, this);
				timer.start();
			}
			else
			{
				timer.restart();
			}
		}
		
		/**
		 *  Remove this cleaner.
		 */
		public void remove()
		{
			if(timer!=null)
				timer.stop();
		}
	}
}
