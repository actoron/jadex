package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.Future;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  The receiver connects to the relay server
 *  and accepts messages.
 */
public class HttpSelectorThread
{
	//-------- constants --------
	
	/** Identifier for the receiving connection. */
	protected static final String	CON_RECEIVE	= "receive";
	
	//-------- attributes --------
	
	/** The relay server host. */
	protected String	host;
	
	/** The relay server port. */
	protected int	port;
	
	/** The relay server URL path. */
	protected String	path;
	
	/** The NIO Selector. */
	protected Selector	selector;

	/** Request for the NIO thread. */
	protected List<IHttpRequest>	queue;
	
	/** True, if there is a pending connection open request (for opening only one connection at a time). */
	protected boolean	connecting;
	
	/** Idle connections available for reuse. */
	protected List<SocketChannel>	idle;
	
	/** Connection counter (for testing). */
	protected int	cons;
	
	//-------- constructors --------
	
	/**
	 *  Create and start a new receiver.
	 * @throws IOException 
	 */
	public HttpSelectorThread(IComponentIdentifier root, String address, IMessageService ms) throws IOException
	{
		if(!address.startsWith("http://"))
			throw new IOException("Unknown URL scheme: "+address);
		path	= "";
		port	= 80;
		host	= address.substring(7);
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
		
		// ANDROID: the following line causes an exception in a 2.2
		// emulator, see:
		// http://code.google.com/p/android/issues/detail?id=9431
		// try this:
//		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
//		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		
		// Causes problem with maven too (only with Win firewall?)
		// http://www.thatsjava.com/java-core-apis/28232/
		selector	= Selector.open();
		queue	= new ArrayList<IHttpRequest>();
		idle	= new ArrayList<SocketChannel>();
		
		queue.add(new ReceiveRequest(root, host, port, path, ms));
		
		new Thread(new Runnable()
		{
			public void run()
			{
//				System.out.println("starting selector thread");
				while(selector.isOpen())
				{
//					System.out.println("running selector thread");
					try
					{
						IHttpRequest	req	= null;
						synchronized(queue)
						{
							if(!queue.isEmpty() && (!connecting || !idle.isEmpty()))
							{
								req	= queue.remove(0);
							}
						}
						if(req!=null)
						{
							if(idle.isEmpty())
							{
								cons++;
								System.out.println("Creating connection: "+cons);
//								connecting	= true;
								SocketChannel	sc	= SocketChannel.open();
								sc.configureBlocking(false);
								sc.connect(new InetSocketAddress(host, port));
								sc.register(selector, SelectionKey.OP_CONNECT, req);
							}
							else
							{
//								System.out.println("Reusing connection");
								SocketChannel	sc	= idle.remove(0);
								SelectionKey	key	= sc.keyFor(selector);
								key.interestOps(SelectionKey.OP_WRITE);
								key.attach(req);
							}
						}
						
						// Wait for an event one of the registered channels
						System.out.println("selector idle");
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
									SocketChannel	sc	= (SocketChannel)key.channel();
									boolean	finished	= sc.finishConnect();
									assert finished;
									key.interestOps(SelectionKey.OP_WRITE);
									connecting	= false;
								}
								else if(key.isWritable())
								{
									req	= (IHttpRequest)key.attachment();
									req.handleWrite(key);
								}
								else if(key.isReadable())
								{
									req	= (IHttpRequest)key.attachment();
									req.handleRead(key);
								}
								
								// If connection is open but no longer needed, add to idle list.
								if(key.channel().isOpen() && key.interestOps()==0)
								{
									idle.add((SocketChannel)key.channel());
//									System.out.println("Idle connections: "+idle.size()+" of "+cons);
								}
								else if(!key.channel().isOpen())
								{
									cons--;
//									System.out.println("Closed connection: "+cons);									
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
//						e.printStackTrace();
					}
				}
//				System.out.println("leaving selector thread");
			}
		}).start();
	}
	
	//-------- methods --------
	
	/**
	 *  Stop the receiver.
	 */
	public void	stop()
	{
		try
		{
			selector.close();
		}
		catch(IOException e)
		{
			// Shouldn't happen!?
			e.printStackTrace();
		}
	}
	
	/**
	 *  Add a send task.
	 */
	public void addSendTask(ManagerSendTask task, Future<Void> fut)
	{
		SendRequest	req	= new SendRequest(task, fut, host, port, path);
		synchronized(queue)
		{
			queue.add(req);
		}
		selector.wakeup();
	}
}
