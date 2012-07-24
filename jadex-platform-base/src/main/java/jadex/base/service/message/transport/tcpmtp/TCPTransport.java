package jadex.base.service.message.transport.tcpmtp;

import jadex.base.AbstractComponentAdapter;
import jadex.base.service.message.ISendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
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
	public final static String[] SCHEMAS = new String[]{"tcp-mtp://"};
	
	/** Constant for asynchronous setting. */
	public final static String ASYNCHRONOUS = "asynchronous";
	
	/** The receiving port. */
	public final static String PORT = "port";
	
	/** How long to keep output connections alive (5 min). */
	protected final static int	MAX_KEEPALIVE	= 300000;

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
	protected Map<String, Object> connections;
	
	/** Should be received asynchronously? One thread for receiving is
		unavoidable. Async defines if the receival should be done on a
		new thread always or on the one receiver thread. */
	protected boolean async;
	
	/** The logger. */
	protected Logger logger;
	
	/** The cleanup timer. */
	protected Timer	timer;
	
	/** The thread pool. */
	protected IDaemonThreadPoolService threadpool;
	
	/** The message service . */
	protected IMessageService msgservice;
	
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
		this.logger = Logger.getLogger(AbstractComponentAdapter.getLoggerName(container.getId())+".TCPTransport");
		
		this.container = container;
		this.async = async;
		this.port = port;
		
		// Set up sending side.
		this.connections = SCollection.createLRU(MAX_CONNECTIONS);
		((LRU<String, Object>)this.connections).setCleaner(new ILRUEntryCleaner<String, Object>()
		{
			public void cleanupEldestEntry(Entry<String, Object> eldest)
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
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		try
		{
			// Set up receiver side.
			// If port==0 -> any free port
			this.serversocket = createServerSocket();
			this.port = serversocket.getLocalPort();
			
			String[]	addresses	= SUtil.getNetworkAddresses();
			this.addresses	= new String[addresses.length];
			for(int i=0; i<addresses.length; i++)
			{
				for(int j=0; j<getServiceSchemas().length; j++)
				{
					this.addresses[i]	= getAddress(getServiceSchemas()[j], addresses[i], port);
				}
			}
			
			// Start the receiver thread.
			SServiceProvider.getService(container, IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IDaemonThreadPoolService, Void>(ret)
			{
				public void customResultAvailable(final IDaemonThreadPoolService tp)
				{
					threadpool = tp;
					
					ret.setResult(null);
					tp.execute(new Runnable()
					{
						List<Object> openincons = Collections.synchronizedList(new ArrayList<Object>());
						public void run()
						{
							//try{serversocket.setSoTimeout(10000);} catch(SocketException e) {}
							while(!serversocket.isClosed())
							{
								try
								{
									final TCPInputConnection con = new TCPInputConnection(serversocket.accept());
									openincons.add(con);
									if(!async)
									{
										TCPTransport.this.deliverMessages(con)
											.addResultListener(new IResultListener<Void>()
										{
											public void resultAvailable(Void result)
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
												TCPTransport.this.deliverMessages(con)
													.addResultListener(new IResultListener<Void>()
												{
													public void resultAvailable(Void result)
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
//								System.out.println("close: "+incons[i]);
								incons[i].close();
							}
//							logger.warning("TCPTransport serversocket closed.");
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
	public IFuture<Void> shutdown()
	{
		try{this.serversocket.close();}catch(Exception e){}
		connections = null; // Help gc
		return IFuture.DONE;
	}
	
	//-------- methods --------

	/**
	 *  Create a server socket.
	 *  @return The server socket.
	 */
	public ServerSocket createServerSocket() throws Exception
	{
		return new ServerSocket(port);
	}
	
	/**
	 *  Create a client socket.
	 *  @return The client socket.
	 */
	public Socket createClientSocket(String host, int port) throws Exception
	{
		return new Socket(host, port);
	}
	
	/**
	 *  Test if a transport is applicable for the target address.
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
	 *  @return True, if the transport satisfies the non-functional requirements.
	 */
	public boolean isNonFunctionalSatisfied(Map<String, Object> nonfunc)
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
	public void	sendMessage(final String address, final ISendTask task)
	{
		final IResultCommand<IFuture<Void>, Void>	send_failure = new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				return new Future<Void>(new RuntimeException("Send failed"));
			}
		};
		
		if(connections==null)
		{
			task.ready(send_failure);
		}
		else
		{
			threadpool.execute(new Runnable()
			{
				public void run()
				{
					final TCPOutputConnection con = getConnection(address, true);
					if(con!=null)
					{
						task.ready(new IResultCommand<IFuture<Void>, Void>()
						{
							public IFuture<Void> execute(Void args)
							{
								if(con.send(task.getProlog(), task.getData()))
								{
	//								System.out.println("Sent with IO TCP: "+task.getReceivers()[0]);
									return IFuture.DONE;
								}
								else
								{
									return new Future<Void>(new RuntimeException("Send failed: "+con));
								}
							}
						});
					}
					else
					{
						task.ready(send_failure);
					}
				}
			});
		}
		
//		IResultCommand<IFuture<Void>, Void>	send	= new IResultCommand<IFuture<Void>, Void>()
//		{
//			public IFuture<Void> execute(Void args)
//			{
//				IFuture<Void>	ret	= null;
//				
//				// Fetch all addresses
//				Set<String>	addresses	= new LinkedHashSet<String>();
//				for(int i=0; i<task.getReceivers().length; i++)
//				{
//					String[]	raddrs	= task.getReceivers()[i].getAddresses();
//					for(int j=0; j<raddrs.length; j++)
//					{
//						addresses.add(raddrs[j]);
//					}			
//				}
//
//				// Iterate over all different addresses and try to send
//				// to missing and appropriate receivers
//				String[] addrs = (String[])addresses.toArray(new String[addresses.size()]);
//				for(int i=0; ret==null && i<addrs.length; i++)
//				{
//					TCPOutputConnection con = getConnection(addrs[i], true);
//					if(con!=null)
//					{
//						if(con.send(task.getProlog(), task.getData()))
//						{
////							System.out.println("Sent with IO TCP: "+task.getReceivers()[0]);
//							ret	= IFuture.DONE;
//						}
//						else
//						{
//							ret	= new Future<Void>(new RuntimeException("Send failed: "+con));
//						}
//					}
//				}
//				
//				if(ret==null)
//				{
//					ret	= new Future<Void>(new RuntimeException("No working connection."));			
//				}
//				
//				return ret;
//			}
//		};
//		task.ready(send);
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
	protected String getAddress(String schema, String hostname, int port)
	{
		return schema+hostname+":"+port;
	}
	
	/**
	 *  Get the connection.
	 *  @param address
	 *  @return a connection of this type
	 */
	protected TCPOutputConnection getConnection(String address, boolean create)
	{
		if(connections==null)
			return null;
		
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
		
		if(ret==null && create)
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
		for(int i=0; i<getServiceSchemas().length; i++)
		{
			if(address.startsWith(getServiceSchemas()[i]))
			{
				// Parse the address
				// todo: handle V6 ip adresses (0:0:0:0 ...)
				try
				{
					int schemalen = getServiceSchemas()[i].length();
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
	
					// todo: which resource identifier to use for outgoing connections?
//					ret = new TCPOutputConnection(InetAddress.getByName(hostname), iport, new Cleaner(address), createClientSocket());
					ret = new TCPOutputConnection(new Cleaner(address), createClientSocket(hostname, iport));
					connections.put(address, ret);
				}
				catch(Exception e)
				{
					if(connections!=null)	// May be already shut down.
					{
						connections.put(address, new TCPDeadConnection());
					}
	//				logger.warning("Could not create connection: "+e.getMessage());
					//e.printStackTrace();
				}
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
	 *  for dispatching to the components.
	 *  @param con The connection.
	 */
	protected IFuture<Void> deliverMessages(final TCPInputConnection con)
	{
		final Future<Void> ret = new Future<Void>();
		getMessageService().addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(IMessageService ms)
			{
				try
				{
					for(byte[] msg=con.read(); msg!=null; msg=con.read())
					{
						ms.deliverMessage(msg);
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
	 * 
	 */
	protected IFuture<IMessageService> getMessageService()
	{
		final Future<IMessageService> ret = new Future<IMessageService>();
		
		if(msgservice==null)
		{
			SServiceProvider.getService(container, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener<IMessageService>(ret)
			{
				public void customResultAvailable(IMessageService result)
				{
					msgservice = result;
					super.customResultAvailable(result);
				}	
			});
		}
		else
		{
			ret.setResult(msgservice);
		}
			
		return ret;
	}
	
	/**
	 *  Class for cleaning output connections after 
	 *  max keep alive time has been reached.
	 */
	protected class Cleaner
	{
		//-------- attributes --------
		
		/** The address of the connection. */
		protected String address;
		
		/** The timer task. */
		protected TimerTask timertask;
		
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
		 *  Refresh the timeout.
		 */
		public void refresh()
		{
			if(timer==null)
			{
				timer	= new Timer(true);
			}
			
			if(timertask!=null)
			{
				timertask.cancel();
			}
			timertask	= new TimerTask()
			{
				public void run()
				{
					logger.info("Timeout reached for: "+address);
					// Hack?! might already be shutdowned, should do better cleanup 
					if(connections!=null)
					{
						removeConnection(address);						
					}
				}
			};
			timer.schedule(timertask, MAX_KEEPALIVE);
		}
		
		/**
		 *  Remove this cleaner.
		 */
		public void remove()
		{
			if(timertask!=null)
			{
				timertask.cancel();
			}
		}
	}
}
