package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
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
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  The selector thread waits for NIO events and issues the appropriate actions
 *  for asynchronous sending and receiving as data becomes available.
 */
public class TcpSelectorThread implements Runnable
{
	// -------- attributes --------

	/** Flag indicating the thread should be running (set to false for shutdown). */
	protected boolean	running;
	
	/** The NIO selector. */
	protected Selector	selector;

	/** The transport agent, e.g. for delivering received messages. */
	protected TcpTransportAgent tcpagent;
	
	/** The tasks enqueued from external threads. */
	protected List<Runnable>	tasks;
	
	/** The write tasks of data waiting to be written to a connection. */
	protected Map<SocketChannel, List<Tuple2<List<ByteBuffer>, Future<Void>>>>	writetasks;
	
	// -------- constructors --------

	/**
	 * Create a NIO selector thread.
	 */
	public TcpSelectorThread(TcpTransportAgent tcpagent)
	{
		this.tcpagent	= tcpagent;

		this.tasks	= new ArrayList<Runnable>();
		this.writetasks	= new LinkedHashMap<SocketChannel, List<Tuple2<List<ByteBuffer>, Future<Void>>>>();
	}
	
	// -------- Runnable interface --------

	/**
	 * Main cycle.
	 */
	public void run()
	{
		while(running)
		{
			try
			{
				// Process pending tasks.
				Runnable[]	atasks;
				synchronized(tasks)
				{
					atasks	= tasks.isEmpty() ? null : tasks.toArray(new Runnable[tasks.size()]);
					tasks.clear();
				}
				for(int i=0; atasks!=null && i<atasks.length; i++)
				{
					atasks[i].run();
				}
				
				// Wait for an event one of the registered channels
//				String wait	= "";
//				for(SelectionKey key: selector.keys())
//				{
//					if(key.isValid())
//					{
//						wait += key.interestOps()+" ";
//					}
//				}
//				agent.getLogger().info(agent.getComponentIdentifier()+ " selector wait: "+wait);
				this.selector.select();
				
//				System.out.println("cons: "+cnt);

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while(selectedKeys.hasNext())
				{
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();

					if(key.isValid())
					{
						if(key.isAcceptable())
						{
							this.handleAccept(key);
						}
						else if(key.isReadable())
						{
							this.handleRead(key);
						}
						else if(key.isConnectable())
						{
							this.handleConnect(key);
						}
						else if(key.isWritable())
						{
							this.handleWrite(key);
						}
					}
					else
					{
						
//						System.out.println("writetasks3: "+writetasks.get(key.channel()));

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
		
		for(SelectionKey key: selector.keys())
		{
			closeConnection(key, null);
		}
		
		try
		{
			selector.close();
		}
		catch(IOException e)
		{
			tcpagent.getLogger().warning("Exception during NIO TCP shutdown: "+e);
		}
//		System.out.println("nio selector end");
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create the selector.
	 */
	protected void	createSelector()	throws Exception
	{
		assert selector==null;
		
		// ANDROID: Selector.open() causes an exception in a 2.2
		// emulator due to IPv6 addresses, see:
		// http://code.google.com/p/android/issues/detail?id=9431
		// Causes problem with maven too (only with Win firewall?)
		// http://www.thatsjava.com/java-core-apis/28232/
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		this.selector = Selector.open();
	}

	/**
	 *  Perform close operations on a channel as identified by the given key.
	 *  Potentially cleans up key attachments as well.
	 *  @param e	The exception, if any.
	 */
	protected void closeConnection(SelectionKey key, Exception e)
	{
		SelectableChannel	sc	= key.channel();
		
		if(e!=null)
		{
			e.printStackTrace();
			if(sc instanceof SocketChannel)
			{
				tcpagent.getLogger().info("Error on connection: "+((SocketChannel)sc).socket().getRemoteSocketAddress()+", "+e);
			}
			else
			{
				tcpagent.getLogger().info("Error on server socket with port: "+((ServerSocketChannel)sc).socket().getLocalPort()+", "+e);			
			}
		}

		try
		{
			sc.close();
		}
		catch(Exception ex){}
		
		// Connection closed: abort all open write tasks.
		List<Tuple2<List<ByteBuffer>, Future<Void>>>	queue	= (List<Tuple2<List<ByteBuffer>, Future<Void>>>)this.writetasks.get(sc);
		if(queue!=null)
		{
			for(Iterator<Tuple2<List<ByteBuffer>, Future<Void>>> it=queue.iterator(); it.hasNext(); )
			{
				Tuple2<List<ByteBuffer>, Future<Void>>	task	= (Tuple2<List<ByteBuffer>, Future<Void>>)it.next();
				Future<Void>	fut	= task.getSecondEntity();
				fut.setException(e!=null ? e : new RuntimeException("Channel closed."));
				it.remove();
			}
			writetasks.remove(sc);
		}

		key.attach(null);
		key.cancel();
	}
	

	/**
	 *  Schedule a task on the thread.
	 */
	protected void schedule(Runnable task)
	{
		assert running;
		synchronized(tasks)
		{
			tasks.add(task);
		}
		selector.wakeup();
	}
	
	
	//-------- methods to be called from external --------
	
	/**
	 *  Open a server socket.
	 *  Must not be called while the thread is running.
	 */
	public int	openPort(int port)	throws Exception
	{
		assert !running;
		assert port>=0;
		
		ServerSocketChannel	ssc	= null;
		try
		{
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ServerSocket serversocket = ssc.socket();
			serversocket.bind(new InetSocketAddress(port));
			port = serversocket.getLocalPort();
			tcpagent.getLogger().info("TCP transport listening to port: "+port);
			
			// Better be done before selector thread is started due to deadlocks, grrr: https://stackoverflow.com/questions/12822298/nio-selector-how-to-properly-register-new-channel-while-selecting
			createSelector();
			ssc.register(selector, SelectionKey.OP_ACCEPT);
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
			throw e;
		}
		
		return port;
	}
	
	/**
	 *  Start the tread.
	 */
	public void	start()	throws Exception
	{
		assert !running;
		this.running	= true;
		
		// Start selector thread for asynchronous sending and/or receiving
		if(selector==null)	// When no port has been opened, we need to create the selector here. 
		{
			createSelector();
		}
		IDaemonThreadPoolService	tps	= SServiceProvider.getLocalService(tcpagent.getAccess(), IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		tps.execute(this);
	}
	
	/**
	 *  Set the running flag to false to gracefully terminate the thread.
	 */
	public void	stop()
	{
		this.running	= false;
		selector.wakeup();
	}
	
	/**
	 *  Perform close operations on a channel.
	 *  Potentially cleans up key attachments as well.
	 */
	public void closeChannel(final SelectableChannel sc)
	{
		schedule(new Runnable()
		{
			@Override
			public void run()
			{
				SelectionKey	sk	= sc.keyFor(selector);
				assert sk!=null;
				closeConnection(sk, null);
			}
		});
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
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_CONNECT, new TcpChannelHandler(sc, target, ret));
						sc.connect(sock);
						tcpagent.getLogger().info("Attempting connection to: "+address);
					}
					catch(Exception e)
					{
						if(sc!=null)
						{
							try{sc.close();}catch(Exception ex){}
						}
						
						tcpagent.getLogger().info("Failed connection to: "+address);
						ret.setException(e);
					}
				}			
			});
		}
		catch(URISyntaxException ex)
		{
			ret.setException(ex);
		}
		
		return ret;
	}
	
	/**
	 *  Send a message using the given connection.
	 *  @param sc	The connection.
	 * 	@param msg	The message.
	 *  @param codecids	The codec ids.
	 *  @return	A future indicating success.
	 */
	public IFuture<Void> sendMessage(final SocketChannel sc, final byte[] header, final byte[] body)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		schedule(new Runnable()
		{
			public void run()
			{
				SelectionKey key = null;
				try
				{
					key = sc.keyFor(selector);
					if(key!=null && key.isValid())
					{
						// Convert message into buffers.
						List<ByteBuffer>	buffers	= new ArrayList<ByteBuffer>();
						
						buffers.add(ByteBuffer.wrap(SUtil.intToBytes(header.length)));
						buffers.add(ByteBuffer.wrap(header));
						buffers.add(ByteBuffer.wrap(SUtil.intToBytes(body.length)));
						buffers.add(ByteBuffer.wrap(body));
						
						// Add buffers as new write task.
						Tuple2<List<ByteBuffer>, Future<Void>>	task	= new Tuple2<List<ByteBuffer>, Future<Void>>(buffers, ret);
						List<Tuple2<List<ByteBuffer>, Future<Void>>>	queue	= (List<Tuple2<List<ByteBuffer>, Future<Void>>>)writetasks.get(sc);
						if(queue==null)
						{
							queue	= new LinkedList<Tuple2<List<ByteBuffer>, Future<Void>>>();
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
						ret.setException(new RuntimeException("key is null or invalid!? "+key));
					}
				}
				catch(RuntimeException e)
				{
					if(key!=null)
					{
						closeConnection(key, e);
					}
					
//					System.err.println("writetasks4: "+writetasks.get(con.getSocketChannel())+", "+e);
//					e.printStackTrace();
					
					// Message encoding failed.
					ret.setException(e);
				}
			}			
		});

		
		return ret;
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
			sc.configureBlocking(false);
			
			// Add empty channel info for unsolicited connections.
			SelectionKey	sckey	= sc.register(selector, 0, new TcpChannelHandler(sc, null, null));
			startHandshake(sckey);
			
			tcpagent.getLogger().info("Accepted connection from: "+sc.socket().getRemoteSocketAddress()+", waiting for handshake...");
		}
		catch(Exception e)
		{
			closeConnection(key, e);
		}
	}

	/**
	 *  Complete a connection request to a server.
	 */
	protected void	handleConnect(SelectionKey key)
	{
		SocketChannel sc = (SocketChannel)key.channel();
		try
		{
			boolean	finished = sc.finishConnect();
			assert finished;

			startHandshake(key);
			
			tcpagent.getLogger().info("Connected to: "+sc.socket().getRemoteSocketAddress()+", waiting for handshake...");
		}
		catch(Exception e)
		{
			closeConnection(key, e);
		}
	}
	
	/**
	 *  Start the hand shake on a new client or server connection.
	 */
	protected void	startHandshake(SelectionKey key)	throws Exception
	{
		// Initialize key to start reading.
		// Will not send user messages over the connection before remote CID is received.
		key.interestOps(SelectionKey.OP_READ);
		
		// Queue sending of own CID to complete bidirectional handshake.
		sendMessage((SocketChannel)key.channel(),
			new byte[0], tcpagent.getAccess().getComponentIdentifier().getPlatformName().getBytes(SUtil.UTF8));
	}
	
	/**
	 *  Read data from a connection.
	 */
	protected void	handleRead(SelectionKey key)
	{
		try
		{
			SocketChannel sc = (SocketChannel)key.channel();
			TcpChannelHandler	handler	= (TcpChannelHandler)key.attachment();
			
			// Try to complete handshake.
			if(!handler.isOpen())
			{
				Tuple2<byte[], byte[]> msg=handler.read();
				if(msg!=null)
				{
					// Make connection available for outgoing messages.
					String	remotecid	= new String(msg.getSecondEntity(), SUtil.UTF8);
					tcpagent.getLogger().info("Handshake completed to: "+remotecid+" at "+sc.socket().getRemoteSocketAddress());
					handler.handshakeComplete(remotecid);
					
//					tcpagent.addConnection(handler.getOpposite(), sc);
				}
			}
			
			// Normal connection operation
			if(handler.isOpen())
			{
				// Read as much messages as available (if any).
				for(Tuple2<byte[], byte[]> msg=handler.read(); msg!=null; msg=handler.read())
				{
//					System.out.println("Read message from: "+sc+", "+new String(msg.getSecondEntity(), SUtil.UTF8));
					tcpagent.deliverMessage(handler.getOpposite(), msg.getFirstEntity(), msg.getSecondEntity());
				}
			}
		}
		catch(Exception e)
		{
			closeConnection(key, e);
		}
	}
	
	/**
	 *  Write data to a connection.
	 */
	protected void handleWrite(SelectionKey key)
	{
		SocketChannel sc = (SocketChannel)key.channel();
		
//		System.out.println("write: "+sc.socket().isInputShutdown()+", "+sc.socket().isOutputShutdown());
		
		List<Tuple2<List<ByteBuffer>, Future<Void>>>	queue	= (List<Tuple2<List<ByteBuffer>, Future<Void>>>)this.writetasks.get(sc);

		try
		{
			boolean	more	= true;
			while(more)
			{
				if(queue.isEmpty())
				{
					more	= false;
					// We wrote away all data, so we're no longer interested in
					// writing on this socket.
					key.interestOps(SelectionKey.OP_READ);

					writetasks.remove(sc);
//					System.out.println("writetasks5: "+writetasks.size());
				}
				else
				{
					Tuple2<List<ByteBuffer>, Future<Void>>	task	= queue.get(0);
					List<ByteBuffer>	buffers	= task.getFirstEntity();	
					Future<Void>	fut	= task.getSecondEntity();	
					ByteBuffer buf = buffers.get(0);
					sc.write(buf);
					if(buf.remaining()>0)
					{
						// Output buffer is full: stop sending for now.
						more	= false;
					}
					else
					{
						// Buffer written: remove task and inform future, when no more buffers for this task.
						buffers.remove(buf);
						if(buffers.isEmpty())
						{
							queue.remove(task);
//							System.out.println("Sent with NIO TCP: "+System.currentTimeMillis()+", "+con.address);
							fut.setResult(null);
						}
					}
//					System.out.println("Wrote data to: "+sc.socket().getRemoteSocketAddress());
				}
			}
		}
		catch(Exception e)
		{
			closeConnection(key, e);
		}
	}
}