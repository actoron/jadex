package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.Token;
import jadex.commons.future.Future;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

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
	
	/** The NIO Selector. */
	protected Selector	selector;

	/** Request for the NIO thread. */
	protected List<IHttpRequest>	queue;
	
	/** Addresses for there is a pending connection open request (for opening only one connection to a specific address at a time). */
	protected Set<Tuple2<String, Integer>>	connecting;
	
	/** Idle connections available for reuse (host,port -> channel). */
	protected Map<Tuple2<String, Integer>, List<SocketChannel>>	idle;
	
	/** List of open connections for ITransport.isConnected. */
	protected Map<Tuple2<String, Integer>, Set<SocketChannel>>	connections;
	
	/** The timeout tasks for the connections. */
	protected Map<SocketChannel, TimerTask>	timeouts;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The timer for delayed reconnect attempts. */
	protected Timer	timer;
	
	//-------- constructors --------
	
	/**
	 *  Create and start a new receiver.
	 * @throws IOException 
	 */
	public HttpSelectorThread(IComponentIdentifier root, String address, IMessageService ms, Logger logger, IExternalAccess access) throws IOException
	{
		this.logger	= logger;
		Tuple2<Tuple2<String, Integer>, String> tup = HttpRelayTransport.parseAddress(address); 
		
		// ANDROID: Selector.open() causes an exception in a 2.2
		// emulator due to IPv6 addresses, see:
		// http://code.google.com/p/android/issues/detail?id=9431
		/* $if android && androidVersion < 9 $
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		$endif $ */
		
		// Causes problem with maven too (only with Win firewall?)
		// http://www.thatsjava.com/java-core-apis/28232/
		selector	= Selector.open();
		queue	= new ArrayList<IHttpRequest>();
		idle	= new HashMap<Tuple2<String, Integer>, List<SocketChannel>>();
		connecting	= Collections.synchronizedSet(new HashSet<Tuple2<String, Integer>>());
		connections	= Collections.synchronizedMap(new HashMap<Tuple2<String, Integer>, Set<SocketChannel>>());
		timeouts	= new HashMap<SocketChannel, TimerTask>();
		timer	= new Timer(true);
		
		queue.add(new ReceiveRequest(root, tup.getFirstEntity(), tup.getSecondEntity(), ms, logger, access));
		
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
							// Find request ready for connecting.
							for(int i=0; req==null && i<queue.size(); i++)
							{
								if(!connecting.contains(queue.get(i).getAddress()) ||
									idle.containsKey(queue.get(i).getAddress()) && !idle.get(queue.get(i).getAddress()).isEmpty() )
								{
									req	= queue.remove(i);
								}
							}
						}
						if(req!=null)
						{
							if(idle.containsKey(req.getAddress()) && !idle.get(req.getAddress()).isEmpty() )
							{
								SocketChannel	sc	= idle.get(req.getAddress()).remove(0);
								// Can not find out if connection still works!? Just try request and reschedule on connection problems.
//								System.out.println("Reusing connection: "+sc.isOpen()+", "+sc.isConnected());
								SelectionKey	key	= sc.keyFor(selector);
								key.interestOps(SelectionKey.OP_WRITE);
								key.attach(req);
							}
							else
							{
								HttpSelectorThread.this.logger.info("nio-relay creating connection to: "+req.getAddress().getFirstEntity()+":"+req.getAddress().getSecondEntity());
								connecting.add(req.getAddress());
								final SocketChannel	sc	= SocketChannel.open();
								sc.configureBlocking(false);
								sc.connect(new InetSocketAddress(req.getAddress().getFirstEntity(), req.getAddress().getSecondEntity().intValue()));
								sc.register(selector, SelectionKey.OP_CONNECT, req);
								
								addTimeoutTask(sc);
							}
						}
						
						// Wait for an event one of the registered channels
//						System.out.println("selector idle");
						selector.select();

						// Iterate over the set of keys for which events are available
						Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
						while(selectedKeys.hasNext())
						{
							SelectionKey key = selectedKeys.next();
							selectedKeys.remove();

							if(key.isValid())
							{
								int	reschedule	= -1;
								req	= (IHttpRequest)key.attachment();
								SocketChannel	sc	= (SocketChannel)key.channel();
								TimerTask	timeout	= timeouts.get(sc);
								timeout.cancel();
								addTimeoutTask(sc);
								
								if(key.isConnectable())
								{
									try
									{
										boolean	finished	= sc.finishConnect();
										assert finished;
										key.interestOps(SelectionKey.OP_WRITE);

										req.handleConnect();
										HttpSelectorThread.this.logger.info("nio-relay connected to: "+req.getAddress().getFirstEntity()+":"+req.getAddress().getSecondEntity());
										connecting.remove(req.getAddress());
										
										// Remember open connections
										Set<SocketChannel>	chs	= connections.get(req.getAddress());
										if(chs==null)
										{
											chs	= new HashSet<SocketChannel>();
											connections.put(req.getAddress(), chs);
										}
										chs.add((SocketChannel)key.channel());
									}
									catch(Exception e)
									{
										HttpSelectorThread.this.logger.info("nio-relay could not connect to relay server (re-attempting in 30 seconds): "+e);
										key.cancel();
										reschedule	= 30000;
									}
								}
								else if(key.isWritable())
								{
									reschedule	= req.handleWrite(key);
								}
								else if(key.isReadable())
								{
									reschedule	= req.handleRead(key);
								}
								
								// Reschedule request in case of error.
								if(reschedule==0)
								{
									synchronized(queue)
									{
										// Queue at beginning to keep message order in case of outdated idle connections.
										queue.add(0, req);
									}
								}
								else if(reschedule>0)
								{
									final IHttpRequest	freq	= req;
									timer.schedule(new TimerTask()
									{
										public void run()
										{
											synchronized(queue)
											{
												connecting.remove(freq.getAddress());
												queue.add(freq);
												selector.wakeup();
											}
										}
									}, reschedule);
								}
								
								// If connection is open but no longer needed, add to idle list.
								if(key.channel().isOpen() && key.interestOps()==0)
								{
									List<SocketChannel>	cons	= idle.get(req.getAddress());
									if(cons==null)
									{
										cons	= new LinkedList<SocketChannel>();
										idle.put(req.getAddress(), cons);
									}
									cons.add((SocketChannel)key.channel());
//									System.out.println("Idle connections: "+idle.size()+" of "+cons);
								}
								else if(!key.channel().isOpen())
								{
//									System.out.println("Closed connection: "+cons);
									timeouts.remove(sc);
									
									// Remove from open connections
									Set<SocketChannel>	chs	= connections.get(req.getAddress());
									if(chs!=null)
									{
										chs.remove((SocketChannel)key.channel());
										if(chs.isEmpty())
										{
											connections.remove(req.getAddress());
										}
									}
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
						HttpSelectorThread.this.logger.info("nio-relay exception in HttpSelectorThread: "+e);
//						e.printStackTrace();
					}
				}
				
//				System.out.println("leaving selector thread");
				if(timer!=null)
					timer.cancel();
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
	public void addSendTask(ManagerSendTask task, Token token, String address, Future<Void> fut)
	{
		Tuple2<Tuple2<String, Integer>, String> tup = HttpRelayTransport.parseAddress(address); 
		SendRequest	req	= new SendRequest(task, token, fut, tup.getFirstEntity(), tup.getSecondEntity(), logger);
		
		synchronized(queue)
		{
			queue.add(req);
		}
		selector.wakeup();
	}

	/**
	 *  Add a task to close a socket channel after some period of inactivity.
	 */
	protected void addTimeoutTask(final SocketChannel sc)
	{
		TimerTask	timeout	= new TimerTask()
		{
			public void run()
			{
				try
				{
					logger.info("nio-relay closing connection due to inactivity: "+sc);
					sc.close();
				}
				catch(IOException e)
				{
//					e.printStackTrace();
				}
			}
		};
		
		// Add 25% to ping delay (30 sec) for timeout.
		timer.schedule(timeout, (long)(SRelay.PING_DELAY*1.25));
		timeouts.put(sc, timeout);
	}
}
