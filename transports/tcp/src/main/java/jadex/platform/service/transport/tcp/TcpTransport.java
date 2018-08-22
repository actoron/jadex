package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransport;
import jadex.platform.service.transport.ITransportHandler;


/**
 *  The selector thread waits for NIO events and issues the appropriate actions
 *  for asynchronous sending and receiving as data becomes available.
 */
public class TcpTransport	implements ITransport<SocketChannel>
{
	//-------- constants --------
	
	/** Priority of transport. */
	public static final int	PRIORITY	= 1000;
	
	// -------- attributes --------

	/** The transport handler, e.g. for delivering received messages. */
	protected ITransportHandler<SocketChannel>	handler;
	
	/** Flag indicating the thread should be running (set to false for shutdown). */
	protected boolean	running;
	
	/** Flag indicating the transport has been shut down.. */
	protected boolean	shutdown;
	
	/** Maximum size a message is allowed to have (including header). */
	protected int maxmsgsize;
	
	/** The NIO selector. */
	protected Selector	selector;

	/** The tasks enqueued from external threads. */
	protected List<Runnable>	tasks;
	
	/** The write tasks of data waiting to be written to a connection. */
	protected Map<SocketChannel, List<Tuple2<ByteBuffer, Future<Integer>>>>	writetasks;
	
	/** Daemon thread pool. */
	protected IDaemonThreadPoolService tps;
	
	/**
	 *  Creates the transport
	 *  
	 *  @param maxmsgsize Maximum size a message is allowed to have (including header).
	 */
	public TcpTransport(int maxmsgsize)
	{
		this.maxmsgsize = maxmsgsize;
	}
	
	//-------- ITransport interface --------	

	/**
	 *  Initialize the transport.
	 *  To be called once, before any other method.
	 *  @param handler 	The transport handler with callback methods. 
	 */
	public void	init(ITransportHandler<SocketChannel> handler)
	{
		tps = ((IInternalRequiredServicesFeature)handler.getAccess().getFeature(IRequiredServicesFeature.class)).getRawService(IDaemonThreadPoolService.class);
		this.handler	= handler;
	}
		
	/**
	 *  Set the running flag to false to gracefully terminate the thread.
	 */
	public void	shutdown()
	{
		Selector	sel	= null;
		synchronized(this)
		{
			assert !shutdown;
			if(running)
			{
				sel	= selector;
				this.running	= false;
			}
			this.shutdown	= true;
		}
		
		if(sel!=null)
		{
			sel.wakeup();
		}
	}
	
	/**
	 *  Get the protocol name.
	 */
	public String	getProtocolName()
	{
		return "tcp";
	}

	/**
	 *  Open a server socket.
	 *  Must not be called while the thread is running.
	 */
	public IFuture<Integer>	openPort(final int port)
	{
		final Future<Integer>	ret	= new Future<Integer>();
		if(port<0)
		{
			ret.setException(new IllegalArgumentException("Port must be greater or equal to zero: "+port));
		}
		else
		{
			try
			{
				// Can not register channel while selecting -> do asynchronously on selector thread.
				// Causes deadlocks otherwise: https://stackoverflow.com/questions/12822298/nio-selector-how-to-properly-register-new-channel-while-selecting
				schedule(new Runnable()
				{
					@Override
					public void run()
					{
						ServerSocketChannel	ssc	= null;
						try
						{
							ssc = ServerSocketChannel.open();
							ssc.configureBlocking(false);
							ServerSocket serversocket = ssc.socket();
							serversocket.bind(new InetSocketAddress(port));
							ssc.register(selector, SelectionKey.OP_ACCEPT);
							int port = serversocket.getLocalPort();
							ret.setResult(port);
						}
						catch(Exception e)
						{
							if(ssc!=null)
							{
								try
								{
									ssc.close();
								}catch(IOException e2){}
							}
							ret.setException(e);
						}
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a connection to a given address.
	 *  @param	address	The target platform's address.
	 *  @param target	The target identifier to maybe perform authentication of the connection.
	 *  @return A future containing the connection when succeeded.
	 */
	public IFuture<SocketChannel>	createConnection(final String address, final IComponentIdentifier target)
	{
		final Future<SocketChannel>	ret	= new Future<SocketChannel>();
		
		final InetSocketAddress	sock;
		try
		{
			// Some scheme required for URI parsing
			URI uri = new URI("tcp://" + address);
			sock	= new InetSocketAddress(uri.getHost(), uri.getPort());

			schedule(new Runnable()
			{
				public void run()
				{
					SocketChannel sc = null;
					try
					{
						sc = SocketChannel.open();
//						sc.socket().setSoTimeout(10);
//						Java 1.7
//						sc.setOption(StandardSocketOptions.TCP_NODELAY, Boolean.TRUE);
						sc.socket().setTcpNoDelay(true);
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_CONNECT, ret);
						sc.connect(sock);
					}
					catch(Exception e)
					{
						if(sc!=null)
						{
							try{sc.close();}catch(Exception ex){}
						}
						
						ret.setException(e);
					}
				}			
			});
		}
		catch(Exception ex)
		{
			ret.setException(ex);
		}
		
		return ret;
	}
	
	/**
	 *  Perform close operations on a connection.
	 *  Potentially cleans up key attachments as well.
	 */
	public void closeConnection(final SocketChannel sc)
	{
		try
		{
			schedule(new Runnable()
			{
				@Override
				public void run()
				{
					SelectionKey	sk	= sc.keyFor(selector);
					assert sk!=null;
					closeConnection(sk, null, true);
				}
			});
		}
		catch(Exception e)
		{
			handler.getAccess().getLogger().warning("Closing connection failed: "+e);
		}
	}
	
	/**
	 *  Send bytes using the given connection.
	 *  @param sc	The connection.
	 *  @param header	The message header.
	 *  @param body	The message body.
	 *  @return	A future indicating success.
	 */
	public IFuture<Integer> sendMessage(final SocketChannel sc, final byte[] header, final byte[] body)
	{
		final Future<Integer>	ret	= new Future<>();
		
		try
		{
			schedule(new Runnable()
			{
				public void run()
				{
					SelectionKey key = null;
					try
					{
						key = sc.keyFor(selector);
						if(key!=null && key.isValid() && sc.isOpen())
						{
							// Convert message into buffer.
							ByteBuffer buf = ByteBuffer.allocateDirect(8 + header.length + body.length);
							buf.put(SUtil.intToBytes(header.length));
							buf.put(header);
							buf.put(SUtil.intToBytes(body.length));
							buf.put(body);
							buf.rewind();
							
							// Add buffer as new write task.
							if(writetasks==null)
							{
								writetasks	= new LinkedHashMap<SocketChannel, List<Tuple2<ByteBuffer,Future<Integer>>>>();
							}
							Tuple2<ByteBuffer, Future<Integer>>	task	= new Tuple2<ByteBuffer, Future<Integer>>(buf, ret);
							List<Tuple2<ByteBuffer, Future<Integer>>>	queue	= (List<Tuple2<ByteBuffer, Future<Integer>>>)writetasks.get(sc);
							if(queue==null)
							{
								queue	= new LinkedList<Tuple2<ByteBuffer, Future<Integer>>>();
								writetasks.put(sc, queue);
		//						System.out.println("writetasks0: "+writetasks.size());
							}
							queue.add(task);
							
							// Inform NIO that we want to write data.
							key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
						}
						else
						{						
	//						System.err.println("writetasks6: "+writetasks.get(con.getSocketChannel()));
							ret.setException(new RuntimeException("Invalid connection: "+sc+", "+key));
						}
					}
					catch(RuntimeException e)
					{
						if(key!=null)
						{
							closeConnection(key, e, true);
						}
						
	//					System.err.println("writetasks4: "+writetasks.get(con.getSocketChannel())+", "+e);
	//					e.printStackTrace();
						
						// Set exception when failed before added to task list.
						ret.setExceptionIfUndone(e);
					}
				}			
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}

		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Perform close operations on a channel as identified by the given key.
	 *  Potentially cleans up key attachments as well.
	 *  @param e	The exception, if any.
	 *  @param remove	Notify the handler to remove the connection (should only be called for connections known to the handler).
	 */
	protected void closeConnection(SelectionKey key, Exception e, boolean remove)
	{
		SelectableChannel	sc	= key.channel();
		
//		if(e!=null)
//		{
//			e.printStackTrace();
//			handler.getAccess().getLogger().info("Error on connection: "+((SocketChannel)sc).socket().getRemoteSocketAddress()+", "+e);
//		}

		try
		{
			sc.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		if(sc instanceof SocketChannel)
		{
			// Connection closed: abort all open write tasks.
			List<Tuple2<ByteBuffer, Future<Integer>>>	queue	= (List<Tuple2<ByteBuffer, Future<Integer>>>)(this.writetasks!=null ? this.writetasks.get(sc) : null);
			if(queue!=null)
			{
				for(Iterator<Tuple2<ByteBuffer, Future<Integer>>> it=queue.iterator(); it.hasNext(); )
				{
					Tuple2<ByteBuffer, Future<Integer>>	task	= (Tuple2<ByteBuffer, Future<Integer>>)it.next();
					Future<Integer>	fut	= task.getSecondEntity();
					fut.setException(e!=null ? e : new RuntimeException("Channel closed."));
					it.remove();
				}
				writetasks.remove(sc);
			}
	
			key.attach(null);
			key.cancel();
			
			if(remove)
			{
				handler.connectionClosed((SocketChannel)sc, e);
			}
		}
	}
	

	/**
	 *  Schedule a task on the thread.
	 *  Can be called on arbitrary threads.
	 */
	protected void schedule(Runnable task)
	{
		boolean	start	= false;
		Selector	selector	= null;
		synchronized(this)
		{
			if(shutdown)
			{
				throw new IllegalStateException("Transport already shut down: "+this+", "+handler.getAccess().getId());
			}
			if(!running)
			{
				start	= true;
				running	= true;
			}
			selector	= this.selector;
			if(tasks==null)
			{
				tasks	= new ArrayList<Runnable>();
			}
			tasks.add(task);
		}
		
		if(start)
		{
//			IDaemonThreadPoolService	tps	= handler.getAccess().getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM));
			tps.executeForever(new Runnable()
			{
				public void run()
				{
					while(running)
					{
						try
						{
							if(TcpTransport.this.selector==null)
							{
								// ANDROID: Selector.open() causes an exception in a 2.2
								// emulator due to IPv6 addresses, see:
								// http://code.google.com/p/android/issues/detail?id=9431
								// Causes problem with maven too (only with Win firewall?)
								// http://www.thatsjava.com/java-core-apis/28232/
								java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
								java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
								TcpTransport.this.selector = Selector.open();
							}
							
							// Process pending tasks.
							Runnable[]	atasks;
							synchronized(TcpTransport.this)
							{
								atasks	= tasks==null || tasks.isEmpty() ? null : tasks.toArray(new Runnable[tasks.size()]);
								tasks.clear();
							}
							for(int i=0; atasks!=null && i<atasks.length; i++)
							{
								atasks[i].run();
							}
							
							TcpTransport.this.selector.select();
							
							// Iterate over the set of keys for which events are available
							Iterator<SelectionKey> selectedKeys = TcpTransport.this.selector.selectedKeys().iterator();
							while(selectedKeys.hasNext())
							{
								SelectionKey key = selectedKeys.next();
								selectedKeys.remove();

								if(key.isValid())
								{
									if(key.isAcceptable())
									{
										handleAccept(key);
									}
									else if(key.isReadable())
									{
										handleRead(key);
									}
									else if(key.isConnectable())
									{
										handleConnect(key);
									}
									else if(key.isWritable())
									{
										handleWrite(key);
									}
								}
								else
								{
									key.cancel();
								}
							}
						}
						catch(Exception e)
						{
							// Key may be cancelled just after isValid() has been tested.
							e.printStackTrace();
						}
					}
					
					for(SelectionKey key: TcpTransport.this.selector.keys())
					{
						closeConnection(key, null, false);
					}
					
					try
					{
						TcpTransport.this.selector.close();
					}
					catch(IOException e)
					{
						handler.getAccess().getLogger().warning("Exception during NIO TCP shutdown: "+e);
					}
				}
			});
		}
		
		else if(selector!=null)
		{
			selector.wakeup();
		}
	}
	
	
	
	//-------- handler methods --------

	/**
	 *  Accept a connection request from a client.
	 */
	protected void handleAccept(SelectionKey key)
	{
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
		SocketChannel sc = null;
		try
		{
			// Accept the connection and make it non-blocking
			sc = ssc.accept();
//			Java 1.7
//			sc.setOption(StandardSocketOptions.TCP_NODELAY, Boolean.TRUE);
			sc.socket().setTcpNoDelay(true);
			sc.configureBlocking(false);
			
			// Add empty channel info for unsolicited connections.
			sc.register(selector, SelectionKey.OP_READ);
			
			handler.connectionEstablished(sc);
		}
		catch(Exception e)
		{
			closeConnection(key, e, false);
		}
	}

	/**
	 *  Complete a connection request to a server.
	 */
	protected void	handleConnect(SelectionKey key)
	{
		// Remove attached future to pass result.
		@SuppressWarnings("unchecked")
		Future<SocketChannel>	fut	= (Future<SocketChannel>)key.attach(null);
		
		SocketChannel sc = (SocketChannel)key.channel();
		try
		{
			boolean	finished = sc.finishConnect();
			assert finished;
			
			// Initialize key to start reading.
			key.interestOps(SelectionKey.OP_READ);

			fut.setResult(sc);
		}
		catch(Exception e)
		{
			// Notify failure and close connection.
			fut.setException(e);
			closeConnection(key, e, false);
		}
	}
	
	/**
	 *  Read data from a connection.
	 */
	protected void	handleRead(SelectionKey key)
	{
		try
		{
			SocketChannel sc = (SocketChannel)key.channel();
			TcpMessageBuffer	buf	= (TcpMessageBuffer)key.attachment();
			if(buf==null)
			{
				buf	= new TcpMessageBuffer(maxmsgsize);
				key.attach(buf);
			}
			
			// Read as much messages as available (if any).
			for(Tuple2<byte[], byte[]> msg=buf.read(sc); msg!=null; msg=buf.read(sc))
			{
				handler.messageReceived(sc, msg.getFirstEntity(), msg.getSecondEntity());
			}
			
			// TODO: remove bufs for idle connections?
		}
		catch(Exception e)
		{
			closeConnection(key, e, true);
		}
	}
	
	/**
	 *  Write data to a connection.
	 */
	protected void handleWrite(SelectionKey key)
	{
		SocketChannel sc = (SocketChannel)key.channel();
		
		List<Tuple2<ByteBuffer, Future<Integer>>>	queue	= (List<Tuple2<ByteBuffer, Future<Integer>>>) (writetasks!=null ? (this.writetasks.get(sc)) : null);

		try
		{
			boolean	more	= true;
			while(more)
			{
				if(queue==null || queue.isEmpty())
				{
					more	= false;
					// We wrote away all data, so we're no longer interested in
					// writing on this socket.
					key.interestOps(SelectionKey.OP_READ);

					if(writetasks!=null)
					{
						writetasks.remove(sc);
					}
				}
				else
				{
					Tuple2<ByteBuffer, Future<Integer>>	task	= queue.get(0);
					ByteBuffer buf = task.getFirstEntity();	
					sc.write(buf);
					if(buf.remaining()>0)
					{
						// Output buffer is full: stop sending for now.
						more	= false;
					}
					else
					{
						// Buffer written: remove task and inform future, when no more buffers for this task.
						queue.remove(task);
						Future<Integer>	fut	= task.getSecondEntity();
						fut.setResult(PRIORITY);
					}
				}
			}
		}
		catch(Exception e)
		{
			closeConnection(key, e, true);
		}
	}
}
