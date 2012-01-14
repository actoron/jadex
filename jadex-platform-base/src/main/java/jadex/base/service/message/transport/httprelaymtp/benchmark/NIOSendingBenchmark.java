package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
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
	protected List<Future<Void>>	open;
	
	/** The NIO selector. */
	protected Selector	selector;
	protected List<Runnable>	tasks;
	
	/** The socket channel (if any). */
	protected SocketChannel	sc;
	
	/** The messages to be sent. */
	protected List<byte[]>	messages;
	
	/** The currently sending message (if any). */
	protected ByteBuffer	buf;
	protected StringBuffer	response;
	
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
		open	= new LinkedList<Future<Void>>();
		messages	= new LinkedList<byte[]>();
		tasks	= new ArrayList<Runnable>();
		
		// ANDROID: the following line causes an exception in a 2.2
		// emulator, see:
		// http://code.google.com/p/android/issues/detail?id=9431
		// try this:
//		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
//		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		
		// Causes problem with maven too (only with Win firewall?)
		// http://www.thatsjava.com/java-core-apis/28232/
		selector	= Selector.open();
		SelectorThread	thread	= new SelectorThread();
		thread.selector	= selector;
		new Thread(thread).start();
		
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
		selector.close();
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
			open.get(0).get(new ThreadSuspendable(), 2000000);
			open.remove(0);
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
		
		final byte[]	msg	= new byte[header.length+4+id.length+4+data.length];
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
				System.out.println("adding message");
				messages.add(msg);
				
				if(sc!=null)
				{
					SelectionKey	key	= sc.keyFor(selector);
					if((key.interestOps()&SelectionKey.OP_WRITE)==0)
						key.interestOps(key.interestOps()|SelectionKey.OP_WRITE);
//					System.out.println("ops1w: "+key.interestOps());
				}
				else
				{
					try
					{
						sc	= SocketChannel.open();
						sc.configureBlocking(false);
						sc.connect(new InetSocketAddress(host, port));
						sc.register(selector, SelectionKey.OP_CONNECT);
//						System.out.println("ops0c: "+sc.keyFor(selector).interestOps());
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}			
		};
		
		synchronized(tasks)
		{
			System.out.println("adding task");
			tasks.add(run);
		}
		selector.wakeup();
	}
	
	//-------- NIO handler --------
	
	public class SelectorThread	implements Runnable
	{
		protected Selector	selector;
		
		public void run()
		{
			System.out.println("starting selector thread");
			while(selector.isOpen())
			{
//				System.out.println("running selector thread");
				try
				{
					Runnable[]	atasks	= null;
					synchronized(tasks)
					{
						if(!tasks.isEmpty())
						{
							atasks	= tasks.toArray(new Runnable[tasks.size()]);
							tasks.clear();
						}
					}
					for(int i=0; atasks!=null && i<atasks.length; i++)
					{
						try
						{
							atasks[i].run();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
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
			System.out.println("leaving selector thread");
		}
		
		//-------- handler methods --------

		/**
		 *  Establish a connection.
		 */
		protected void	handleConnect(SelectionKey key)
		{
			System.out.println("Opening connection");
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
//					System.out.println("ops2w: "+key.interestOps());
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
			System.out.println("Sending message");
			SocketChannel	sc	= (SocketChannel)key.channel();

			try
			{
				boolean	more	= true;
				while(more)
				{
					if(buf==null && messages.isEmpty())
					{
						more	= false;
						// We wrote away all data, deregister writing interest.
						assert (key.interestOps()&SelectionKey.OP_WRITE)!=0;
						key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
//						System.out.println("ops3-w: "+key.interestOps());
					}
					else if(buf!=null)
					{
						sc.write(buf);
						if(buf.remaining()>0)
						{
							// Output buffer is full: stop sending for now, but keep interest.
							more	= false;
						}
						else
						{
							// Buffer written, register interest in answer.
							System.out.println("Message sent.");
							key.interestOps(key.interestOps()|SelectionKey.OP_READ);
//							System.out.println("ops4r: "+key.interestOps());
							buf	= null;
						}
					}
					else
					{
						byte[]	msg	= messages.remove(0);
						buf	= ByteBuffer.wrap(msg);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				key.cancel();
			}
		}
		
		/**
		 *  Read data from a connection.
		 */
		protected void	handleRead(SelectionKey key)
		{
			System.out.println("Reading response");
			try
			{
				ReadableByteChannel	rbc	= (ReadableByteChannel)key.channel();
				ByteBuffer	inbuf = ByteBuffer.allocate(256);
				int	read	= rbc.read(inbuf);
				while(read>0)
				{
					if(response==null)
						response	= new StringBuffer();
					response.append(new String(inbuf.array(), 0, read));
					inbuf.clear();
					read	= rbc.read(inbuf);
				}
				
				// All available data read.
				assert (key.interestOps()&SelectionKey.OP_READ)!=0;
				key.interestOps(key.interestOps()-SelectionKey.OP_READ);
//				System.out.println("ops5-r: "+key.interestOps());
				
				// Extract HTTP responses
				int	idx;
				while((idx=response.indexOf("\r\n\r\n"))!=-1)
				{
					String	resp	= response.substring(0, idx);
					response.delete(0, idx+4);
					boolean	close	= resp.indexOf("Connection: close")!=-1;
					if(close)
						System.out.print("Close requested.");
					if(resp.indexOf("\r\n")!=-1)
					{
						resp	= resp.substring(0, resp.indexOf("\r\n"));
					}
					if(!"HTTP/1.1 200 OK".equals(resp))
						throw new IOException("HTTP response: "+resp);
					
					boolean	result	= false;
					for(int i=0; !result && i<open.size(); i++)
					{
						if(!open.get(i).isDone())
						{
							open.get(i).setResult(null);
							result	= true;
						}
					}
					
					// Make sure that future was found.
					assert result;
				}
			}
			catch(Exception e)
			{ 
				e.printStackTrace();
				key.cancel();
			}
		}
	}
}
