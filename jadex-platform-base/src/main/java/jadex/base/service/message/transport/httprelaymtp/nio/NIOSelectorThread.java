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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 *  The handler perform all NIO operations on a single thread.
 */
public class NIOSelectorThread
{
	//-------- attributes --------
	
	/** The NIO Selector. */
	protected Selector	selector;

	/** Requests for the NIO thread that are ready to execute but do not currently have a connection assigned. */
	// Entries are added from external threads access must be synchronized.
	protected List<IHttpRequest>	queue;
	
	/** Addresses for there is a pending connection open request (for opening only one connection to a specific address at a time). */
	protected Set<Tuple2<String, Integer>>	connecting;
	
	/** Idle connections available for reuse (host,port -> channel). */
	protected Map<Tuple2<String, Integer>, List<SocketChannel>>	idle;
	
	/** The timers (timer tasks ordered by task timepoint). */
	public Set<SelectorTimer>	timers;
	
	/** Each socket channel has an idle timer that closes the connection after inactivity. */
	protected Map<SocketChannel, SelectorTimer>	channeltimers;
	
	/** The logger. */
	protected Logger	logger;
	
	//-------- constructors --------
	
	/**
	 *  Create and start a new receiver.
	 * @throws IOException 
	 */
	public NIOSelectorThread(IComponentIdentifier root, String address, IMessageService ms, Logger logger, IExternalAccess access) throws IOException
	{
		this.logger	= logger;
		this.queue	= new ArrayList<IHttpRequest>();
		this.idle	= new HashMap<Tuple2<String, Integer>, List<SocketChannel>>();
		this.connecting	= new HashSet<Tuple2<String, Integer>>();
		this.channeltimers	= new HashMap<SocketChannel, SelectorTimer>();
		// Sorted set for scheduled requests ordered by due time.
		this.timers	= new TreeSet<SelectorTimer>(new Comparator<SelectorTimer>()
		{
			public int compare(SelectorTimer t1, SelectorTimer t2)
			{
				long ret	= t1.getTaskTime() - t2.getTaskTime();
				if(ret==0 && t1!=t2)
					ret	= t1.getId()-t2.getId();
				return ret>0 ? 1 : ret<0 ? -1 : 0;
			}
		});
		
		// Add request for receiving messages from relay server.
		Tuple2<Tuple2<String, Integer>, String> tup = HttpRelayTransport.parseAddress(address);
		synchronized(queue)
		{
			queue.add(new ReceiveRequest(root, tup.getFirstEntity(), tup.getSecondEntity(), ms, logger, access));
		}
		
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
						// Add outstanding requests from queue
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
							req.initRequest();
							
							if(idle.containsKey(req.getAddress()) && !idle.get(req.getAddress()).isEmpty() )
							{
								SocketChannel	sc	= idle.get(req.getAddress()).remove(0);
								updateTimer(sc);
								
								// Can not find out if connection still works!? Just try request and reschedule on connection problems.
//								System.out.println("Reusing connection: "+sc.isOpen()+", "+sc.isConnected());
								SelectionKey	key	= sc.keyFor(selector);
								key.attach(req);
								key.interestOps(SelectionKey.OP_WRITE);
							}
							else
							{
								NIOSelectorThread.this.logger.info("nio-relay creating connection to: "+req.getAddress().getFirstEntity()+":"+req.getAddress().getSecondEntity());
								connecting.add(req.getAddress());
								final SocketChannel	sc	= SocketChannel.open();
								sc.configureBlocking(false);
								sc.connect(new InetSocketAddress(req.getAddress().getFirstEntity(), req.getAddress().getSecondEntity().intValue()));
								sc.register(selector, SelectionKey.OP_CONNECT, req);
								
								// Create inactivity timer for connection.
								SelectorTimer	timer	= new SelectorTimer()
								{
									public void	run()
									{
										try
										{
											NIOSelectorThread.this.logger.info("nio-relay closing connection due to inactivity: "+sc);
											
											// close() cancels the key and does not generate nio event.
											// therefore we need manual cleanup and re-add request 
											SelectionKey	key	= sc.keyFor(selector);
											IHttpRequest	req	= (IHttpRequest)key.attachment();
											if(req!=null)
											{
												connecting.remove(req.getAddress());
												if(idle.get(req.getAddress())!=null)
													idle.get(req.getAddress()).remove(sc);
												
												if(req.reschedule())
												{
													synchronized(queue)
													{
														queue.add(req);
													}
												}
											}
											sc.close();
											channeltimers.remove(sc);
										}
										catch(IOException e)
										{
											e.printStackTrace();
										}
									}
								};
								channeltimers.put(sc, timer);
								updateTimer(sc);
							}
						}
						
						// Perform due tasks or store next timeout.
						long	timeout	= 0;
						boolean	wait	= true;
						for(Iterator<SelectorTimer> it= timers.iterator(); it.hasNext(); )
						{
							SelectorTimer	timer	= it.next();
							timeout	= timer.getTaskTime() - System.currentTimeMillis();
							// If timer is due -> execute task.
							if(timeout<=0)
							{
								it.remove();
								timer.run();
								wait	= false;	// check queue first, if entries are executed.
							}
						}
						
						if(wait)
						{
							// Wait for an event one of the registered channels
	//						System.out.println("selector idle");
							selector.select(timeout);
	
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
									updateTimer(sc);
									
									if(key.isConnectable())
									{
										try
										{
											boolean	finished	= sc.finishConnect();
											assert finished;
											key.interestOps(SelectionKey.OP_WRITE);
											NIOSelectorThread.this.logger.info("nio-relay connected to: "+req.getAddress().getFirstEntity()+":"+req.getAddress().getSecondEntity());
											connecting.remove(req.getAddress());
										}
										catch(Exception e)
										{
											NIOSelectorThread.this.logger.info("nio-relay could not connect to relay server (re-attempting in 30 seconds): "+e);
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
										SelectorTimer	timer	= new SelectorTimer()
										{
											public void run()
											{
												synchronized(queue)
												{
													queue.add(0, freq);
												}
												connecting.remove(freq.getAddress());
												selector.wakeup();
											}
										};
										timer.setTaskTime(reschedule+System.currentTimeMillis());
										timers.add(timer);
									}
									
									// If connection is open but no longer needed, add to idle list.
									if(key.isValid() && key.channel().isOpen() && key.interestOps()==0)
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
										NIOSelectorThread.this.logger.info("nio-relay removed connection: "+sc);
										SelectorTimer	timer	= channeltimers.get(sc);
										timers.remove(timer);
										channeltimers.remove(sc);
									}
								}
								else
								{
									key.cancel();
								}
							}
						}
					}
					catch(Exception e)
					{
						// Key may be cancelled just after isValid() has been tested.
						NIOSelectorThread.this.logger.info("nio-relay exception in HttpSelectorThread: "+e);
//						e.printStackTrace();
					}
				}
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
	 *  Update timer for closing a socket channel after some period of inactivity.
	 */
	protected void updateTimer(final SocketChannel sc)
	{
		// Update timer.
		SelectorTimer	timer	= channeltimers.get(sc);
		timers.remove(timer);
		// Add 25% to ping delay (30 sec) for timeout.
		timer.setTaskTime(System.currentTimeMillis()+(long)(SRelay.PING_DELAY*1.25));
		timers.add(timer);
		// Only called from selector thread itself -> no need to wake it up.		
	}
}
