package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *  Test sending using new connections.
 */
public class NIOSendingBenchmark	extends AbstractRelayBenchmark
{
	//-------- constants --------
	
	/** The max number of open messages. */
	protected final int	MAX	= 2;
	
	//-------- attributes --------
	
	/** The open futures. */
	protected List<IFuture<Void>>	open;
	
	/** The NIO selector. */
	protected Selector	selector;
	
	/** The socket channel (if any). */
	protected SocketChannel	sc;
	
	/** The messages to be sent. */
	protected List<byte[]>	messages;
	
	protected String	host;
	protected String	path;
	protected int	port;
	
	//-------- template methods --------
		
	/**
	 *  Init the time array and
	 *  open the connection.
	 */
	protected void setUp() throws Exception
	{
		open	= new LinkedList<IFuture<Void>>();
		messages	= new LinkedList<byte[]>();
		
		// ANDROID: the following line causes an exception in a 2.2
		// emulator, see:
		// http://code.google.com/p/android/issues/detail?id=9431
		// try this:
//		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
//		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		
		// Causes problem with maven too (only with Win firewall?)
		// http://www.thatsjava.com/java-core-apis/28232/
		selector	= Selector.open();
		
		if(!ADDRESS.startsWith("http://"))
			throw new IOException("Unknown URL scheme: "+ADDRESS);
		path	= "";
		port	= 80;
		host	= ADDRESS.substring(7);
		if(host.indexOf('/')!=-1)
		{
			path	= host.substring(host.indexOf('/'));
			host	= host.substring(0, host.indexOf('/'));
		}
		if(host.indexOf(':')!=-1)
		{
			port	= Integer.parseInt(host.substring(host.indexOf(':')+1));
			host	= host.substring(0, host.indexOf(':'));			
		}

	}
	
	/**
	 *  Close the socket.
	 */
	protected void tearDown() throws Exception
	{
		while(!open.isEmpty())
		{
			open.remove(0).get(new ThreadSuspendable(), 20000);
		}
	}
	
	/**
	 *  Receive a message.
	 */
	protected void doSingleRun() throws Exception
	{
		// Prepare data.
		byte[]	data	= new byte[SIZE];
		Random	rnd	= new Random();
		rnd.nextBytes(data);
		
		sendMessage("benchmark", data);
		
		while(open.size()>MAX)
		{
			open.remove(0).get(new ThreadSuspendable(), 20000);			
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Asynchronously send a message.
	 */
	protected void	sendMessage(Object oid, byte[] data)
	{
		open.add(new Future<Void>());

		byte[]	id	= JavaWriter.objectToByteArray(oid, getClass().getClassLoader());

		byte[]	header	= 
			( "POST "+path+" HTTP/1.1\r\n"
			+ "Content-Type: application/octet-stream\r\n"
			+ "Host: "+host+":"+port+"\r\n"
			+ "Content-Length: "+(4+id.length+4+data.length)+"\r\n"
			+ "\r\n"
			).getBytes();
		
		byte[]	msg	= new byte[header.length+4+id.length+4+data.length];
		System.arraycopy(header, 0, msg, 0, header.length);
		System.arraycopy(SUtil.intToBytes(id.length), 0, msg, header.length, 4);
		System.arraycopy(id, 0, msg, header.length+4, id.length);
		System.arraycopy(SUtil.intToBytes(data.length), 0, msg, header.length+4+id.length, 4);
		System.arraycopy(data, 0, msg,  header.length+4+id.length+4, data.length);

		// Inform NIO that we want to write data.
		Runnable	run	= new Runnable()
		{
			public void run()
			{
				if(sc!=null)
				{
					SelectionKey	key	= sc.keyFor(selector);
					key.interestOps(SelectionKey.OP_WRITE);
				}
				else
				{
					try
					{
						sc	= SocketChannel.open();
						sc.configureBlocking(false);
						sc.connect(new InetSocketAddress(host, port));
						sc.register(selector, SelectionKey.OP_CONNECT);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}			
		};
		
//		synchronized(tasks)
		{
//			tasks.add(run);
		}
		selector.wakeup();
	
	}
	
	//-------- NIO handler --------
	
	public class SelectorThread	implements Runnable
	{
		protected Selector	selector;
		
		public void run()
		{
			while(true)
			{
				try
				{
					// Wait for an event one of the registered channels
					selector.select();

					// Iterate over the set of keys for which events are available
					Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
					while(selectedKeys.hasNext())
					{
						SelectionKey key = selectedKeys.next();
						selectedKeys.remove();

						if(key.isValid())
						{
							if(key.isConnectable())
							{
								this.handleConnect(key);
							}
							else if(key.isWritable())
							{
								this.handleWrite(key);
							}
							else if(key.isReadable())
							{
								this.handleRead(key);
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
		}
		
		//-------- handler methods --------

		/**
		 *  Establish a connection.
		 */
		protected void	handleConnect(SelectionKey key)
		{
			SocketChannel	sc	= (SocketChannel)key.channel();
			try
			{
				boolean	finished	= sc.finishConnect();
				assert finished;
				
				if(messages.isEmpty())
				{
					// Keep channel on hold until we are ready to write.
				    key.interestOps(0);
				}
				else
				{
					key.interestOps(SelectionKey.OP_WRITE);
				}
			}
			catch(Exception e)
			{ 
				e.printStackTrace();
				key.cancel();
			}
		}
		
		/**
		 *  Write data to a connection.
		 */
		protected void handleWrite(SelectionKey key)
		{
//			SocketChannel	sc	= (SocketChannel)key.channel();
//			NIOTCPOutputConnection	con	= (NIOTCPOutputConnection)key.attachment();
//			List	queue	= (List)this.writetasks.get(sc);
//
//			try
//			{
//				boolean	more	= true;
//				while(more)
//				{
//					if(queue.isEmpty())
//					{
//						more	= false;
//						// We wrote away all data, so we're no longer interested in
//						// writing on this socket.
//						key.interestOps(0);
//					}
//					else
//					{
//						Tuple	task	= (Tuple)queue.get(0);
//						List	buffers	= (List)task.get(0);	
//						Future	fut	= (Future)task.get(1);	
//						ByteBuffer buf = (ByteBuffer)buffers.get(0);
//						sc.write(buf);
//						if(buf.remaining()>0)
//						{
//							// Output buffer is full: stop sending for now.
//							more	= false;
//						}
//						else
//						{
//							// Buffer written: remove task and inform future, when no more buffers for this task.
//							buffers.remove(buf);
//							if(buffers.isEmpty())
//							{
//								queue.remove(task);
//								fut.setResult(null);
//							}
//						}
//					}
//				}
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				key.cancel();
//			}
		}
		
		/**
		 *  Read data from a connection.
		 */
		protected void	handleRead(SelectionKey key)
		{
//			try
//			{
//				ReadableByteChannel	rbc	= (ReadableByteChannel)key.channel();
//			}
//			catch(Exception e)
//			{ 
//				e.printStackTrace();
//				key.cancel();
//			}
		}
	}
}
