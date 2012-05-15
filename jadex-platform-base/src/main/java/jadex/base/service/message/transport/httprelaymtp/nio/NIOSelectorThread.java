package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ISendTask;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.Tuple2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedSelectorException;
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
	
	/** Limit the number of parallel connections (at least 2 connections required for receive and send). */ 
	protected int connection_limit	= 5;
	
	//-------- constructors --------
	
	/**
	 *  Create and start a new receiver.
	 * @throws IOException 
	 */
	public NIOSelectorThread(IComponentIdentifier root, String url, Tuple2<Tuple2<String, Integer>, String> address, IMessageService ms, Logger logger, IExternalAccess access) throws IOException
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
		synchronized(queue)
		{
			queue.add(new ReceiveRequest(root, url, address.getFirstEntity(), address.getSecondEntity(), ms, logger, access));
		}
		
		// ANDROID: Selector.open() causes an exception in a 2.2
		// emulator due to IPv6 addresses, see:
		// http://code.google.com/p/android/issues/detail?id=9431
		/* if[android8]
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		end[android8]*/
		
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
								if(connection_limit>0 && !connecting.contains(queue.get(i).getAddress()) ||
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
								req.setIdle(true);
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
								connection_limit--;
//								System.out.println("connection_limit-: "+connection_limit);
								req.setIdle(false);
								NIOSelectorThread.this.logger.info("nio-relay creating connection to: "+req.getAddress().getFirstEntity()+":"+req.getAddress().getSecondEntity());
								connecting.add(req.getAddress());
								SocketChannel	sc	= null;
								try
								{
									sc	= SocketChannel.open();
									sc.configureBlocking(false);
									sc.connect(new InetSocketAddress(req.getAddress().getFirstEntity(), req.getAddress().getSecondEntity().intValue()));
									sc.register(selector, SelectionKey.OP_CONNECT, req);
								}
								catch(Exception e)
								{
									NIOSelectorThread.this.logger.info("nio-relay could not connect to relay server (re-attempting in 30 seconds): "+e);
									handleStep(req, null, 30000, sc);
									sc	= null;
								}
								
								if(sc!=null)
								{
									createConnectionTimer(sc, req.getAddress());
								}
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
									
									handleStep(req, key, reschedule, sc);
								}
								else
								{
									NIOSelectorThread.this.logger.info("nio-relay cancelling key: "+key.channel());
									key.cancel();
								}
							}
						}
					}
					catch(ClosedSelectorException e)
					{
						// closed on exit -> ignore and thread will exit.
					}
					catch(Exception e)
					{
						// Key may be cancelled just after isValid() has been tested.
						NIOSelectorThread.this.logger.info("nio-relay exception in HttpSelectorThread: "+e);
						e.printStackTrace();
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
	public void addSendTask(ISendTask task, Tuple2<Tuple2<String, Integer>, String> address)
	{
		SendRequest	req	= new SendRequest(task, address.getFirstEntity(), address.getSecondEntity(), logger);
		
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
		if(timer==null)
			System.out.println("sdvh vk");
		timers.remove(timer);
		// Add 25% to ping delay (30 sec) for timeout.
		timer.setTaskTime(System.currentTimeMillis()+(long)(SRelay.PING_DELAY*1.25));
		timers.add(timer);
		// Only called from selector thread itself -> no need to wake it up.		
	}

	/**
	 *  Called after some part of a request has been executed.
	 */
	protected void handleStep(IHttpRequest req, SelectionKey key, int reschedule, SocketChannel sc)
	{
		// Reschedule request in case of error.
		if(reschedule==0)
		{
			NIOSelectorThread.this.logger.info("nio-relay rescheduling request immediately: "+req);
			synchronized(queue)
			{
				// Queue at beginning to keep message order in case of outdated idle connections.
				queue.add(0, req);
			}
		}
		else if(reschedule>0)
		{
			NIOSelectorThread.this.logger.info("nio-relay rescheduling request after "+reschedule+" ms: "+req);
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
		if(key!=null && key.isValid() && key.channel().isOpen() && key.interestOps()==0)
		{
			List<SocketChannel>	cons	= idle.get(req.getAddress());
			if(cons==null)
			{
				cons	= new LinkedList<SocketChannel>();
				idle.put(req.getAddress(), cons);
			}
			cons.add((SocketChannel)key.channel());
			key.attach(null);
//			System.out.println("Idle connections+: "+cons);
		}
		else if(key!=null && !key.isValid() || key!=null && !key.channel().isOpen() || key!=null && !((SocketChannel)key.channel()).isConnected())
		{
			NIOSelectorThread.this.logger.info("nio-relay removed connection: "+key.isValid()+", "+!key.channel().isOpen()+", "+((SocketChannel)key.channel()).isConnected()+", "+sc);
			SelectorTimer	timer	= channeltimers.get(sc);
			timers.remove(timer);
			channeltimers.remove(sc);
			connection_limit++;
//			System.out.println("connection_limit+: "+connection_limit);
		}
	}

	/**
	 *  Create inactivity timer for connection.
	 */
	protected void createConnectionTimer(final SocketChannel sc, final Tuple2<String, Integer> address)
	{
		SelectorTimer	timer	= new SelectorTimer()
		{
			public void	run()
			{
				try
				{
					NIOSelectorThread.this.logger.info("nio-relay closing connection due to inactivity: "+sc);
					
					// close() cancels the key and does not generate nio event.
					// therefore we need manual cleanup and re-add request, if any
					connecting.remove(address);
					if(idle.get(address)!=null)
					{
						idle.get(address).remove(sc);
//						System.out.println("Idle connections-: "+idle.get(address));
						if(idle.get(address).isEmpty())
						{
							idle.remove(address);
						}
					}
					channeltimers.remove(sc);
					
					SelectionKey	key	= sc.keyFor(selector);
					IHttpRequest	req	= (IHttpRequest)key.attachment();
					if(req!=null)
					{
						if(req.reschedule())
						{
							synchronized(queue)
							{
								queue.add(req);
							}
						}
					}
					
					sc.close();
					connection_limit++;
//					System.out.println("connection_limit+: "+connection_limit);
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
