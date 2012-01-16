package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
	protected final int	MAX	= 100;
	
	//-------- attributes --------
	
	/** The open futures. */
	protected List<Message>	open;
	
	/** The NIO selector. */
	protected Selector	selector;
	
	/** The tasks for the NIO thread. */
	protected List<Runnable>	tasks;
	
	/** The queue for the NIO thread (messages waiting for a free or new connection). */
	protected List<Message>	queue;
	
	/** True, if a new connection is currently established (only one at a time). */
	protected boolean	connecting;
	
	/** The idle connections to be reused. */
	protected List<SocketChannel>	idle;
	
	protected String	host;
	protected String	path;
	protected int	port;
	
	protected int	added, sent, received, connections;
	
	//-------- template methods --------
		
	/**
	 *  Init the time array and
	 *  open the connection.
	 */
	protected void setUp() throws Exception
	{
		open	= new LinkedList<Message>();
		queue	= new LinkedList<Message>();
		tasks	= new ArrayList<Runnable>();
		idle	= new ArrayList<SocketChannel>();
		added	= sent	= received	= connections	= 0;
		
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
		Message[]	futs	= open.toArray(new Message[open.size()]);
		for(int i=0; i<futs.length; i++)
		{
//			System.out.println("sent "+i+": "+sent);
//			System.out.println("received "+i+": "+received);
			futs[i].fut.get(new ThreadSuspendable(), 20000);
		}
		selector.close();
//		System.out.println("sent: "+sent);
//		System.out.println("received: "+received);
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
		byte[]	id	= JavaWriter.objectToByteArray("benchmark", getClass().getClassLoader());
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
		
		Message	m	= new Message(new Future<Void>(), msg);
		open.add(m);
		added++;
//		System.out.println("open: "+open.size()+" of "+added);
		sendMessage(m);
		
		if(open.size()>=MAX)
		{
			open.remove(0).fut.get(new ThreadSuspendable(), 20000);
//			System.out.println("open-: "+open.size()+" of "+added);
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Asynchronously send a message.
	 */
	protected void	sendMessage(final Message msg)
	{
		// Inform NIO that we want to write data.
		Runnable	run	= new Runnable()
		{
			public void run()
			{
				if(!idle.isEmpty())
				{
					SocketChannel	sc	= idle.remove(0);
					SelectionKey	key	= sc.keyFor(selector);
					key.attach(msg);
					key.interestOps(SelectionKey.OP_WRITE);
				}
				else if(!connecting)
				{
					connecting	= true;
					connections++;
					System.out.println("connections: "+connections);
					try
					{
						SocketChannel	sc	= SocketChannel.open();
						sc.configureBlocking(false);
						sc.connect(new InetSocketAddress(host, port));
						sc.register(selector, SelectionKey.OP_CONNECT, msg);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					queue.add(msg);
				}
			}
		};
		
		synchronized(tasks)
		{
//			System.out.println("adding task");
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
//			System.out.println("starting selector thread");
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
//					System.out.println("selector idle");
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
//					e.printStackTrace();
				}
			}
//			System.out.println("leaving selector thread");
		}
		
		//-------- handler methods --------

		/**
		 *  Establish a connection.
		 */
		protected void	handleConnect(SelectionKey key)
		{
//			System.out.println("Opening connection");
			SocketChannel	sc	= (SocketChannel)key.channel();
			try
			{
				boolean	finished	= sc.finishConnect();
				assert finished;
				key.interestOps(SelectionKey.OP_WRITE);
				connecting	= false;
				
				// Check if more messages are waiting
				if(!queue.isEmpty())
				{
					sendMessage(queue.remove(0));
				}
			}
			catch(Exception e)
			{ 
				e.printStackTrace();
			}
		}
		
		/**
		 *  Write data to a connection.
		 */
		protected void handleWrite(SelectionKey key)
		{
			SocketChannel	sc	= (SocketChannel)key.channel();
			Message	msg	= (Message)key.attachment();
//			System.out.println("Sending on "+sc);

			try
			{
				if(msg.buf==null)
					msg.buf	= ByteBuffer.wrap(msg.data);

				sc.write(msg.buf);
				if(msg.buf.remaining()>0)
				{
					// Output buffer is full: stop sending for now, but keep interest.
				}
				else
				{
					// Buffer written, register interest in answer.
					sent++;
//					System.out.println("Message sent: "+sent);
					key.interestOps(SelectionKey.OP_READ);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		/**
		 *  Read data from a connection.
		 */
		protected void	handleRead(SelectionKey key)
		{
			SocketChannel	sc	= (SocketChannel)key.channel();
			Message	msg	= (Message)key.attachment();
//			System.out.println("Reading from "+sc);
			try
			{
				StringBuffer	response	= new StringBuffer();
				ByteBuffer	inbuf = ByteBuffer.allocate(256);
				int	read	= sc.read(inbuf);
				while(read>0)
				{
					response.append(new String(inbuf.array(), 0, read));
					inbuf.clear();
					read	= sc.read(inbuf);
				}
				
				// Extract HTTP response
				int	idx	= response.indexOf("\r\n\r\n");
				if(idx==-1)
					throw new RuntimeException("HTTP response: "+response);
				String	resp	= response.substring(0, idx);
				boolean	close	= resp.indexOf("Connection: close")!=-1;
				if(close)
				{
					connections--;
					System.out.println("connections-: "+connections);
					sc.close();
					key.cancel();
				}
				else
				{
					idle.add(sc);
					// Check if more messages are waiting
					if(!queue.isEmpty())
					{
						sendMessage(queue.remove(0));
					}
				}
				
				if(resp.indexOf("\r\n")!=-1)
				{
					resp	= resp.substring(0, resp.indexOf("\r\n"));
				}
				if(!"HTTP/1.1 200 OK".equals(resp))
					throw new IOException("HTTP response: "+resp);
					
				received++;
				msg.fut.setResult(null);
//				System.out.println("Received response: "+received);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static class	Message
	{
		/** To be notified when the message was sent. */
		public Future<Void>	fut;
		
		/** The message content. */
		public byte[]	data;
		
		/** The wrapped message content. */
		public ByteBuffer	buf;
		
		/**
		 *  Create a new message.
		 */
		public Message(Future<Void> fut, byte[] data)
		{
			this.fut	= fut;
			this.data	= data;
		}
	}
}
