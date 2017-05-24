package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
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

import jadex.bridge.IInternalAccess;
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
	protected IInternalAccess agent;
	
	/** The tasks enqueued from external threads. */
	protected List<Runnable>	tasks;
	
	/** The write tasks of data waiting to be written to a connection. */
	protected Map<SocketChannel, List<Tuple2<List<ByteBuffer>, Future<Void>>>>	writetasks;
	
	// -------- constructors --------

	/**
	 * Create a NIO selector thread.
	 */
	public TcpSelectorThread(Selector selector, IInternalAccess agent)
	{
		this.running = true;
		this.selector	= selector;
		this.agent	= agent;

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
//				System.out.println("NIO selector idle");
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
//				e.printStackTrace();
			}
		}
		
		for(SelectionKey key: selector.keys())
		{
			closeKeyAttachment(key);
		}
		
		try
		{
			selector.close();
		}
		catch(IOException e)
		{
			agent.getLogger().warning("Exception during NIO TCP shutdown: "+e);
		}
//		System.out.println("nio selector end");
	}
	
	/**
	 * 
	 */
	protected void closeKeyAttachment(SelectionKey key)
	{
		// TODO: Still needed?
//		Object	con	= key.attachment();
//		
//		if(con instanceof Tuple2<?, ?>)
//		{
//			con	= ((Tuple2<?,?>)con).getSecondEntity();
//		}
//		
//		if(con instanceof IFuture<?> && ((IFuture<?>)con).isDone()  && ((IFuture<?>)con).getException()==null)
//		{
//			con	= ((IFuture<?>)con).get();
//		}
//
//		if(con instanceof Closeable)
//		{
//			try
//			{
//				((Closeable)con).close();
////				System.out.println("closed: "+con);
//			}
//			catch(IOException e)
//			{
//			}
//		}
		
		try
		{
			SelectableChannel	sc	= key.channel();
			sc.close();
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
	}
	
	//-------- methods to be called from external --------
	
	/**
	 *  Set the running flag to false to gracefully terminate the thread.
	 */
	public void	shutdown()
	{
		this.running	= false;
		selector.wakeup();
	}
	
	/**
	 *  Create a connection to a given addresses.
	 *  
	 *  @param address	The address to connect to.
	 *  @return A future containing the connection when succeeded.
	 */
	public IFuture<SocketChannel>	createConnection(final String address)
	{
		final Future<SocketChannel>	ret	= new Future<SocketChannel>();
		
		final InetSocketAddress	sock;
		try
		{
			// Some scheme required for URI parsing
			URI uri = new URI("tcp://" + address);
			sock	= new InetSocketAddress(uri.getHost(), uri.getPort());

			Runnable	task	= new Runnable()
			{
				public void run()
				{
					SocketChannel sc = null;
					try
					{
						sc = SocketChannel.open();
//						sc.socket().setSoTimeout(10);
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_CONNECT, new Tuple2<InetSocketAddress, Future<SocketChannel>>(sock, ret));
						sc.connect(sock);
						agent.getLogger().info("Attempting connection to: "+address);
					}
					catch(Exception e)
					{
						if(sc!=null)
						{
							try{sc.close();}catch(Exception ex){}
						}
						
						agent.getLogger().info("Failed connection to: "+address);
						ret.setException(e);
					}
				}			
			};
			synchronized(tasks)
			{
				tasks.add(task);
			}
			selector.wakeup();
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
		Runnable	run	= new Runnable()
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
						closeKeyAttachment(key);
					}
					
//					System.err.println("writetasks4: "+writetasks.get(con.getSocketChannel())+", "+e);
//					e.printStackTrace();
					
					// Message encoding failed.
					ret.setException(e);
				}
			}			
		};
		
		synchronized(tasks)
		{
			tasks.add(run);
		}
		selector.wakeup();

		
		return ret;
	}
	
	//-------- handler methods --------

	/**
	 *  Accept a connection request.
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
			
			// Write one byte for handshake (always works due to local network buffers).
			sc.write(ByteBuffer.wrap(new byte[1]));
	
			// Register the new SocketChannel with our Selector, indicating
			// we'd like to be notified when there's data waiting to be read
			
			sc.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, null);
			
			agent.getLogger().info("Accepted connection from: "+sc.socket().getRemoteSocketAddress());
		}
		catch(Exception e)
		{
			connectionFailed(key, e);
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
			
			// Normal connection operation.
			if(key.attachment() instanceof TcpMessageBuffer)
			{
				// Read as much messages as available (if any).
				for(byte[] msg=con.read(); msg!=null; msg=con.read())
				{
//					System.out.println("Read message from: "+con);
					msgservice.deliverMessage(msg);
				}
			}
			
			// Handle output handshake (read one byte to make sure that channel is working before sending).
			else
			{
				// TODO: why increase buffer?
//				try
//				{
//					sc.socket().setSendBufferSize(sc.socket().getSendBufferSize()<<1);
//				}
//				catch(Exception e)
//				{
//					agent.getLogger().warning("Cannot set send buffer size: "+e);
//				}
				
				Tuple2<InetSocketAddress, Future<SocketChannel>> tuple = (Tuple2<InetSocketAddress, Future<SocketChannel>>)key.attachment();
				InetSocketAddress	address	= tuple.getFirstEntity();
				Future<SocketChannel>	ret	= tuple.getSecondEntity();
	
				if(sc.read(ByteBuffer.wrap(new byte[1]))!=1)
					throw new IOException("Error receiving handshake byte.");
					
				agent.getLogger().info("NIOTCP connected to: "+address);
				ret.setResult(sc);
			}
		}
		catch(Exception e)
		{
			connectionFailed(key, e);
		}
	}

	/**
	 *  Finish outgoing connection establishment.
	 */
	protected void	handleConnect(SelectionKey key)
	{
		SocketChannel sc = (SocketChannel)key.channel();
		Tuple2<InetSocketAddress, Future<SocketChannel>> tuple = (Tuple2<InetSocketAddress, Future<SocketChannel>>)key.attachment();
		InetSocketAddress	address	= tuple.getFirstEntity();
		Future<SocketChannel>	ret	= tuple.getSecondEntity();
		try
		{
			boolean	finished = sc.finishConnect();
			assert finished;
			// Before connection can be used, make sure that it works by waiting for handshake byte.
			key.interestOps(SelectionKey.OP_READ);
			
			agent.getLogger().info("NIOTCP connected to: "+address+", waiting for handshake...");
		}
		catch(Exception e)
		{
			connectionFailed(key, e);
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
			connectionFailed(key, e);
		}
	}
	
	/**
	 *  Cleanup for a failed connection.
	 */
	protected void	connectionFailed(SelectionKey key, Exception e)
	{
		SocketChannel sc = (SocketChannel)key.channel();
		if(sc!=null)
		{
			try{sc.close();}catch(Exception ex){}
		}
		
		// Connection failure: notify all open write tasks.
		List<Tuple2<List<ByteBuffer>, Future<Void>>>	queue	= (List<Tuple2<List<ByteBuffer>, Future<Void>>>)this.writetasks.get(sc);
		for(Iterator<Tuple2<List<ByteBuffer>, Future<Void>>> it=queue.iterator(); it.hasNext(); )
		{
			Tuple2<List<ByteBuffer>, Future<Void>>	task	= (Tuple2<List<ByteBuffer>, Future<Void>>)it.next();
			Future<Void>	fut	= task.getSecondEntity();
			fut.setException(e);
			it.remove();
		}
		writetasks.remove(sc);
//		System.out.println("writetasks1: "+writetasks.size());
		
		agent.getLogger().info("NIOTCP error on connection: "+sc.socket().getRemoteSocketAddress()+", "+e);
//		e.printStackTrace();
		key.cancel();
	}
}
