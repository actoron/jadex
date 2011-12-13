package jadex.base.service.message.transport.niotcpmtp;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/* $if !android $ */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/* $endif $ */
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
import java.util.logging.Logger;

/* $if !android $ */
import javax.swing.Timer;
/* $else $
import jadex.base.service.message.Timer;
import jadex.base.service.message.TimerListener;
$endif $ */

/**
 * The selector thread waits for NIO events and issues the appropriate actions
 * for asynchronous sending and receiving as data becomes available.
 */
public class SelectorThread implements Runnable
{
	// -------- attributes --------

	/** Flag indicating the thread should be running (set to false for shutdown). */
	protected boolean	running;
	
	/** The NIO selector. */
	protected Selector	selector;

	/** The message service for delivering received messages. */
	protected IMessageService msgservice;
	
	/** The codec factory for encoding/decoding messages. */
	protected CodecFactory codecfac;
	
	/** The library service for encoding/decoding messages. */
	protected ILibraryService libservice;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The service provider. */
	protected IServiceProvider	provider;
	
	/** The tasks enqueued from external threads. */
	protected List<Runnable>	tasks;

	/** The pool of output connections (InetSocketAddress -> NIOTCPOutputConnection). */
	protected Map	connections;

	/** The write tasks of data waiting to be written to a connection (socketchannel->tuple{buffers, future}). */
	protected Map	writetasks;

	// -------- constructors --------

	/**
	 * Create a NIO selector thread.
	 */
	public SelectorThread(Selector selector, IMessageService msgsservice, CodecFactory codecfac, ILibraryService libservice, Logger logger, IServiceProvider provider)
	{
		this.running = true;
		this.selector	= selector;
		this.msgservice	= msgsservice;
		this.codecfac	= codecfac;
		this.libservice	= libservice;
		this.logger	= logger;
		this.provider	= provider;
		this.tasks	= new ArrayList<Runnable>();
		this.connections	= new LinkedHashMap();
		this.writetasks	= new LinkedHashMap();
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
		
		for(Iterator it=connections.values().iterator(); it.hasNext(); )
		{
			Object	con	= it.next();
			if(con instanceof NIOTCPOutputConnection)
			{
				((NIOTCPOutputConnection)con).getCleaner().remove();
			}
		}
	}
	
	//-------- methods to be called from external --------
	
	/**
	 *  Set the running flag to false to gracefully terminate the thread.
	 */
	public void	setRunning(boolean running)
	{
		this.running	= running;
		selector.wakeup();
	}
	
	/**
	 *  Get a connection to one of the given addresses.
	 *  Tries all addresses in parallel and returns the first
	 *  available connection.
	 *  
	 *  @param addresses	The addresses to connect to.
	 *  @return A future containing a connection to the first responsive address.
	 */
	public IFuture	getConnection(final InetSocketAddress[] addresses)
	{
		final Future	ret	= new Future();
		
		NIOTCPOutputConnection	con	= null;
		List<Runnable>	todo	= null;
		List	futures	= null;
		synchronized(connections)
		{
			// Try to find existing connection.
			for(int i=0; con==null && i<addresses.length; i++)
			{
				Object	val	= connections.get(addresses[i]); 
				if(val instanceof NIOTCPOutputConnection)
				{
					con	= (NIOTCPOutputConnection)val;
				}
			}
			
			// Not found: get futures for connecting to addresses
			if(con==null)
			{
				for(int i=0; con==null && i<addresses.length; i++)
				{
					Object	val	= connections.get(addresses[i]);
					
					// Reset connection if connection should be retried.
					if(val instanceof NIOTCPDeadConnection && ((NIOTCPDeadConnection)val).shouldRetry())
					{
						val	= null;
					}
					
					if(val==null)
					{
						final Future	fut	= new Future();
						connections.put(addresses[i], fut);
						final InetSocketAddress	address	= addresses[i];
						Runnable	task	= new Runnable()
						{
							public void run()
							{
								try
								{
									SocketChannel	sc	= SocketChannel.open();
									sc.configureBlocking(false);
									sc.connect(address);
									sc.register(selector, SelectionKey.OP_CONNECT, new Tuple(address, fut));
									logger.info("Attempting connection to: "+address);
								}
								catch(Exception e)
								{
									fut.setException(e);
								}
							}			
						};
						if(todo==null)
						{
							todo	= new ArrayList<Runnable>();
						}
						todo.add(task);
						if(futures==null)
						{
							futures	= new ArrayList();
						}
						futures.add(fut);
					}
					else if(val instanceof Future)
					{
						if(futures==null)
						{
							futures	= new ArrayList();
						}
						futures.add(val);						
					}
				}
			}
		}
		
		// Connection available.
		if(con!=null)
		{
			ret.setResult(con);
		}
		
		// Connection not available: wait for futures.
		else
		{
			// Enqueue connection tasks if any.
			if(todo!=null)
			{
				synchronized(tasks)
				{
					for(int i=0; i<todo.size(); i++)
					{
						tasks.add(todo.get(i));
					}
				}
				selector.wakeup();
			}
			
			if(futures!=null)
			{
				// Listener called on NIO thread only
				final int	num	= futures.size();
				IResultListener	lis	= new IResultListener()
				{
					protected int	cnt;
					
					public void resultAvailable(Object result)
					{
						cnt++;
						if(!ret.isDone())
						{
							ret.setResult(result);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						cnt++;
						if(cnt==num && !ret.isDone())
						{
							ret.setExceptionIfUndone(new RuntimeException("Cannot open connection: no working addresses. "+SUtil.arrayToString(addresses)));
						}
					}
				};
				
				for(int i=0; i<futures.size(); i++)
				{
					((IFuture)futures.get(i)).addResultListener(lis);
				}
			}
			else
			{				
				// Happens if only dead connections are found.
				ret.setException(new RuntimeException("Cannot open connection: no working addresses. "+SUtil.arrayToString(addresses)));
			}
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
//	public IFuture sendMessage(final NIOTCPOutputConnection con, final MessageEnvelope msg, final byte[] codecids)
//	public IFuture<Void> sendMessage(ManagerSendTask task)
	public IFuture<Void> sendMessage(final NIOTCPOutputConnection con, final byte[] prolog, final byte[] data)
	{
		final Future<Void>	ret	= new Future<Void>();
		Runnable	run	= new Runnable()
		{
			public void run()
			{
				try
				{
					// Convert message into buffers.
					List	buffers	= new ArrayList();
//					byte[]	codecs	= codecids==null || codecids.length==0 ? codecfac.getDefaultCodecIds() : codecids;
//			
//					Object enc_msg = msg;
//					for(int i=0; i<codecs.length; i++)
//					{
//						ICodec codec = codecfac.getCodec(codecs[i]);
//						// todo: which resource identifier to use for incoming connections?
//						ClassLoader cl = getClass().getClassLoader(); // libservice.getClassLoader(null)
//						enc_msg	= codec.encode(enc_msg, cl);
//					}
//					byte[] data = (byte[])enc_msg;
					
//					byte[] prolog = new byte[1+codecs.length+NIOTCPTransport.PROLOG_SIZE];
//					prolog[0] = (byte)codecs.length;
//					System.arraycopy(codecs, 0, prolog, 1, codecs.length);
//					System.arraycopy(SUtil.intToBytes(prolog.length+data.length), 0, prolog, codecs.length+1, 4);
					
					buffers.add(ByteBuffer.wrap(prolog));
					buffers.add(ByteBuffer.wrap(data));
					
					// Add buffers as new write task.
					Tuple	task	= new Tuple(buffers, ret);
					List	queue	= (List)writetasks.get(con.getSocketChannel());
					if(queue==null)
					{
						queue	= new LinkedList();
						writetasks.put(con.getSocketChannel(), queue);
					}
					queue.add(task);
					
					// Inform NIO that we want to write data.
					SelectionKey	key	= con.getSocketChannel().keyFor(selector);
					key.interestOps(SelectionKey.OP_WRITE);
					key.attach(con);
				}
				catch(RuntimeException e)
				{
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
		try
		{
			// Accept the connection and make it non-blocking
			SocketChannel sc = ssc.accept();
			sc.configureBlocking(false);
	
			// Register the new SocketChannel with our Selector, indicating
			// we'd like to be notified when there's data waiting to be read
			
			// todo: which resource identifier to use for outgoing connections?
			ClassLoader cl = getClass().getClassLoader(); // libservice.getClassLoader(null)
			sc.register(this.selector, SelectionKey.OP_READ, new NIOTCPInputConnection(sc, codecfac, cl));
			
			logger.info("Accepted connection from: "+sc.socket().getRemoteSocketAddress());
		}
		catch(Exception e)
		{
			this.logger.info("Failed connection attempt: "+ssc+", "+e);
//			e.printStackTrace();
			key.cancel();
		}
	}
	
	/**
	 *  Read data from a connection.
	 */
	protected void	handleRead(SelectionKey key)
	{
		NIOTCPInputConnection	con	= (NIOTCPInputConnection)key.attachment();
		try
		{
			// Read as much messages as available (if any).
			for(MessageEnvelope msg=con.read(); msg!=null; msg=con.read())
			{
//				System.out.println("Read message from: "+con);
				msgservice.deliverMessage(msg.getMessage(), msg.getTypeName(), msg.getReceivers());
			}
		}
		catch(Exception e)
		{ 
			logger.warning("NIOTCP receiving error while reading data: "+con+", "+e);
//			e.printStackTrace();
			con.close();
			key.cancel();
		}

	}

	/**
	 *  Read data from a connection.
	 */
	protected void	handleConnect(SelectionKey key)
	{
		SocketChannel	sc	= (SocketChannel)key.channel();
		Tuple	tuple	= (Tuple)key.attachment();
		InetSocketAddress	address	= (InetSocketAddress)tuple.get(0);
		Future	ret	= (Future)tuple.get(1);
		try
		{
			boolean	finished	= sc.finishConnect();
			assert finished;
			Cleaner	cleaner	= new Cleaner(address);
			NIOTCPOutputConnection	con	= new NIOTCPOutputConnection(sc, address, cleaner);
			cleaner.refresh();
			synchronized(connections)
			{
				connections.put(address, con);
			}
			// Keep channel on hold until we are ready to write.
		    key.interestOps(0);
			logger.info("Connected to : "+address);
			ret.setResult(con);
		}
		catch(Exception e)
		{ 
			synchronized(connections)
			{
				connections.put(address, new NIOTCPDeadConnection());
			}
			ret.setException(e);
			logger.info("NIOTCP receiving error while opening connection: "+address+", "+e);
//			e.printStackTrace();
			key.cancel();
		}
	}
	
	/**
	 *  Write data to a connection.
	 */
	protected void handleWrite(SelectionKey key)
	{
		SocketChannel	sc	= (SocketChannel)key.channel();
		NIOTCPOutputConnection	con	= (NIOTCPOutputConnection)key.attachment();
		List	queue	= (List)this.writetasks.get(sc);

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
					key.interestOps(0);
				}
				else
				{
					Tuple	task	= (Tuple)queue.get(0);
					List	buffers	= (List)task.get(0);	
					Future	fut	= (Future)task.get(1);	
					ByteBuffer buf = (ByteBuffer)buffers.get(0);
					sc.write(buf);
					if(buf.remaining()>0)
					{
						// Ouput buffer is full: stop sending for now.
						more	= false;
					}
					else
					{
						// Buffer written: remove task and inform future, when no more buffers for this task.
						buffers.remove(buf);
						if(buffers.isEmpty())
						{
							queue.remove(task);
							fut.setResult(null);
						}
					}
					
					con.getCleaner().refresh();
//					System.out.println("Wrote data to: "+sc.socket().getRemoteSocketAddress());
				}
			}
		}
		catch(Exception e)
		{
			con.getCleaner().remove();
			synchronized(connections)
			{
				connections.put(con.getAddress(), new NIOTCPDeadConnection());
			}
			
			// Connection failure: notify all open tasks.
			for(Iterator it=queue.iterator(); it.hasNext(); )
			{
				Tuple	task	= (Tuple)it.next();
				Future	fut	= (Future)task.get(1);	
				fut.setException(e);
				it.remove();
			}
			writetasks.remove(sc);
			
			logger.info("NIOTCP receiving error while writing to connection: "+sc.socket().getRemoteSocketAddress()+", "+e);
//			e.printStackTrace();
			key.cancel();
		}
	}
	
	//-------- helper classes --------

	/**
	 *  Class for cleaning output connections after 
	 *  max keep alive time has been reached.
	 */
	/* $if !android $ */
	protected class Cleaner	implements	ActionListener
	/* $else $
	protected class Cleaner implements TimerListener
	$endif $ */
	{
		//-------- attributes --------
		
		/** The address of the connection. */
		protected InetSocketAddress address;
		
		/** The timer. */
		// Hack!!! java.util.Timer does not support cancellation of scheduled tasks, grrr.
		protected Timer timer;
		
		//-------- constructors --------
		
		/**
		 *  Cleaner for a specified output connection.
		 *  @param address The address.
		 */
		public Cleaner(InetSocketAddress address)
		{
			this.address = address;
		}
		
		//-------- methods --------
		
		
		/**
		 *  Called when timepoint was reached.
		 */
		/* $if !android $ */
	    public void actionPerformed(ActionEvent event)
	    /* $else    
	    public void actionPerformed()
	    $endif $ */
		{
			Object	con;
			synchronized(connections)
			{
				con	= connections.remove(address);
			}
			if(con instanceof NIOTCPOutputConnection)
			{
				try
				{
					((NIOTCPOutputConnection)con).getSocketChannel().close();
				}
				catch(Exception e)
				{
				}
				logger.info("Removed connection to : "+address);
			}
		}
		
		/**
		 *  Refresh the timeout.
		 */
		public void refresh()
		{
			if(timer==null)
			{
				timer = new Timer(NIOTCPTransport.MAX_KEEPALIVE, this);
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
			{
				timer.stop();
			}
		}
	}
}
