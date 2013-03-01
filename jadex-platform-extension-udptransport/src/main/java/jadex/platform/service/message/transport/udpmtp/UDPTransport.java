package jadex.platform.service.message.transport.udpmtp;

import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.message.ISendTask;
import jadex.platform.service.message.transport.ITransport;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.RxMessage;
import jadex.platform.service.message.transport.udpmtp.receiving.handlers.IPacketHandler;
import jadex.platform.service.message.transport.udpmtp.receiving.handlers.MsgAckFinHandler;
import jadex.platform.service.message.transport.udpmtp.receiving.handlers.MsgConfirmationHandler;
import jadex.platform.service.message.transport.udpmtp.receiving.handlers.MsgPacketHandler;
import jadex.platform.service.message.transport.udpmtp.receiving.handlers.ProbeHandler;
import jadex.platform.service.message.transport.udpmtp.sending.FlowControl;
import jadex.platform.service.message.transport.udpmtp.sending.SendMessageTask;
import jadex.platform.service.message.transport.udpmtp.sending.SendingThreadTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;
import jadex.platform.service.message.transport.udpmtp.sending.TxShutdownTask;
import jadex.platform.service.message.transport.udpmtp.timed.PeerProber;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class UDPTransport implements ITransport
{
	/** Debug GUI. */
	public final static boolean DEBUG_GUI = true;
//	public final static boolean DEBUG_GUI = false;
	
	/** The direct connect schema */
	public final static String DIRECT_SCHEMA = "udp-mtp://";
	
	/** The schema names. */
	public final static String[] SCHEMAS = new String[] { DIRECT_SCHEMA };
	
	/** The local schema. */
	public final static String schema = DIRECT_SCHEMA;
	
	/** The service provider. */
	protected IServiceProvider provider;
	
	/** The resolve address cache. */
	protected Map<String, InetSocketAddress> resolvecache;
	
	/** The thread pool. */
	protected IDaemonThreadPoolService threadpoool;
	
	/** The timed task dispatcher. */
	protected TimedTaskDispatcher timedtaskdispatcher;
	
	/** The message service . */
	protected IMessageService msgservice;
	
	/** The socket */
	protected DatagramSocket socket;
	
	/** The addresses. */
	protected String[] addresses;
	
	/** Lower bound of the port range. */
	protected int lowport;
	
	/** Upper bound of the port range. */
	protected int highport;
	
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** Handlers for incoming packets. */
	protected List<IPacketHandler> packethandlers;
	
	/** Message ID counter. */
	protected int idcounter;
	
	/** Currently used message IDs */
	protected Set<Integer> usedids;
	
	/** Incoming Message pool. */
	protected Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages;
	
	/** The remaining send quota. */
	protected AtomicInteger sendquota;
	
	/** The flow control. */
	protected FlowControl flowcontrol;
	
	/**
	 *  Creates a UDP transport.
	 * 
	 * 	@param provider The service provider.
	 * 	@param lowport The lowest port in the usable port range.
	 * 	@param highport The highest port in the usable port range.
	 */
	public UDPTransport(IServiceProvider provider, int lowport, int highport)
	{
		this(provider, lowport, highport, STunables.PARSE_CACHE_SIZE);
	}
	
	/**
	 *  Creates a UDP transport.
	 * 
	 * 	@param provider The service provider.
	 * 	@param lowport The lowest port in the usable port range.
	 * 	@param highport The highest port in the usable port range.
	 *  @param resolvecachesize The size of the address resolve cache.
	 */
	public UDPTransport(IServiceProvider provider, int lowport, int highport, int resolvecachesize)
	{
		this.peerinfos = Collections.synchronizedMap(new HashMap<InetSocketAddress, PeerInfo>());
		this.usedids = Collections.synchronizedSet(new HashSet<Integer>());
		this.incomingmessages = Collections.synchronizedMap(new HashMap<InetSocketAddress, Map<Integer, RxMessage>>());
		this.inflightmessages = Collections.synchronizedMap(new HashMap<Integer, TxMessage>());
		this.packetqueue = new PriorityBlockingQueue<TxPacket>(11, new Comparator<TxPacket>()
		{
			public int compare(TxPacket o1, TxPacket o2)
			{
				int prio1 = o1.getPriority();
				int prio2 = o2.getPriority();
				if (prio1 == prio2)
				{
					return o1.getPacketNumber() - o2.getPacketNumber();
				}
				return o1.getPriority() - o2.getPriority();
			}
		});
		
		this.resolvecache = Collections.synchronizedMap((new LRU<String, InetSocketAddress>(resolvecachesize)));
		this.lowport = lowport;
		this.highport = highport;
		if (lowport > highport)
		{
			lowport = this.highport;
			this.highport = this.lowport;
			this.lowport = lowport;
		}
		
		this.provider = provider;
		this.sendquota = new AtomicInteger(STunables.MIN_SENDABLE_BYTES);
		this.flowcontrol = new FlowControl(sendquota);
	}
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		System.out.println("Starting UDP Transport");
		final Future<Void> ret = new Future<Void>();
		socket = null;
		Random r = new Random();
		int randrange = highport - lowport + 1;
		
		int cycles = STunables.RANDOM_PORT_CYCLES;
		while (socket == null && cycles > 0)
		{
			int port = r.nextInt(randrange) + lowport;
			try
			{
				socket = new DatagramSocket(port);
			}
			catch (Exception e)
			{
			}
			--cycles;
		}
		
		if (socket == null)
		{
			// Systematic search.
			for (int port = lowport; port < highport + 1 && socket == null; ++port)
			{
				try
				{
					socket = new DatagramSocket(port);
				}
				catch (Exception e)
				{
				}
			}
		}
		
		if (socket != null)
		{
			try
			{
				socket.setSendBufferSize(STunables.BUFFER_SIZE);
				socket.setReceiveBufferSize(STunables.BUFFER_SIZE);
			}
			catch (SocketException e2)
			{
			}
			String[] addresses;
			try
			{
				addresses = SUtil.getNetworkAddresses();
				List<String> addr = new ArrayList();
				for(int i=0; i<addresses.length; i++)
				{
					for(int j=0; j<getServiceSchemas().length; j++)
					{
						String ad = getServiceSchemas()[j] + addresses[i] + ":" + socket.getLocalPort();
						addr.add(ad);
					}
				}
				
				this.addresses	= addr.toArray(new String[addr.size()]);
			}
			catch (SocketException e1)
			{
				throw new RuntimeException(e1);
			}
			
			SServiceProvider.getService(provider, IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IDaemonThreadPoolService, Void>(ret)
			{
				public void customResultAvailable(IDaemonThreadPoolService tp)
				{
					timedtaskdispatcher = new TimedTaskDispatcher(tp);
					
					SServiceProvider.getService(provider, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
					{
						public void customResultAvailable(IMessageService result)
						{
							msgservice = result;
							
							// Sender Thread.
							SendingThreadTask stt = new SendingThreadTask(socket, packetqueue, sendquota, inflightmessages, peerinfos, timedtaskdispatcher);
							timedtaskdispatcher.executeNow(stt);
							
							// Message handlers, the order has a purpose.
							packethandlers = Collections.synchronizedList(new ArrayList<IPacketHandler>());
							packethandlers.add(new ProbeHandler(peerinfos, packetqueue, timedtaskdispatcher));
							packethandlers.add(new MsgPacketHandler(msgservice, incomingmessages, peerinfos, packetqueue, timedtaskdispatcher));
							packethandlers.add(new MsgAckFinHandler(incomingmessages, inflightmessages, sendquota, flowcontrol, packetqueue, peerinfos, usedids, timedtaskdispatcher));
							packethandlers.add(new MsgConfirmationHandler(incomingmessages, inflightmessages, sendquota, packetqueue, peerinfos));
							
							
							// Receiver Thread.
							timedtaskdispatcher.executeNow(new Runnable()
							{
								public void run()
								{
									byte[] buf = new byte[65536];
									boolean running = true;
									
									while (running)
									{
										try
										{
											DatagramPacket dgp = new DatagramPacket(buf, buf.length);
											socket.receive(dgp);
											
											final byte[] packet = new byte[dgp.getLength()];
											System.arraycopy(dgp.getData(), 0, packet, 0, packet.length);
											
											InetSocketAddress sender = new InetSocketAddress(dgp.getAddress(), dgp.getPort());
											
											timedtaskdispatcher.executeNow(new PacketDispatcher(sender, packet, packethandlers, timedtaskdispatcher));
										}
										catch (IOException e)
										{
											e.printStackTrace();
											running = false;
										}
									}
								}
							});
							if (DEBUG_GUI)
							{
								new DebugGui(peerinfos, inflightmessages, packetqueue, sendquota, flowcontrol, stt);
							}
							System.out.println("UDP Transport start done.");
							ret.setResult(null);
						}	
					});
				}
			});
			
		}
		else
		{
			ret.setException(new RuntimeException("No free UDP port found in specified port range (" + lowport + "-" + highport + ")."));
		}
		
		System.out.println("UDP-Socket bound port: " + socket.getLocalPort());
		return ret;
	}

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture<Void> shutdown()
	{
		SendingThreadTask.queuePacket(packetqueue, new TxShutdownTask());
		socket.close();
		return IFuture.DONE;
	}
	
	/**
	 *  Test if a transport is applicable for the target address.
	 *  @return True, if the transport is applicable for the address.
	 */
	public boolean	isApplicable(String address)
	{
		return resolvecache.containsKey(address) || ParsedAddress.parseAddress(address) != null;
	}
	
	/**
	 *  Test if a transport satisfies the non-functional requirements.
	 *  @return True, if the transport satisfies the non-functional requirements.
	 */
	public boolean	isNonFunctionalSatisfied(Map<String, Object> nonfunc)
	{
		Boolean sec = nonfunc!=null? (Boolean)nonfunc.get(SecureTransmission.SECURE_TRANSMISSION): null;
		return sec==null || !sec.booleanValue();
	}
	
	/**
	 *  Send a message to the given address.
	 *  This method is called multiple times for the same message, i.e. once for each applicable transport / address pair.
	 *  The transport should asynchronously try to connect to the target address
	 *  (or reuse an existing connection) and afterwards call-back the ready() method on the send task.
	 *  
	 *  The send manager calls the obtained send commands of the transports and makes sure that the message
	 *  gets sent only once (i.e. call send commands sequentially and stop, when a send command finished successfully).
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param address The address to send to.
	 *  @param task A task representing the message to send.
	 */
	public void	sendMessage(final String address, final ISendTask task)
	{
		timedtaskdispatcher.executeNow(new Runnable()
		{
			public void run()
			{
				ParsedAddress parsedaddress = ParsedAddress.parseAddress(address);
				
				if (parsedaddress != null)
				{
					InetSocketAddress receiver = resolvecache.get(address);
					if (receiver == null)
					{
						try
						{
							InetAddress resolvedaddr = InetAddress.getByName(parsedaddress.getHostname());
							receiver = new InetSocketAddress(resolvedaddr, parsedaddress.getPort());
							resolvecache.put(address, receiver);
						}
						catch (final UnknownHostException e)
						{
							task.ready(new IResultCommand<IFuture<Void>, Void>()
							{
								public IFuture<Void> execute(Void args)
								{
									return new Future<Void>(new IOException("Resolver error: " + e.getMessage()));
								}
							});
							return;
						}
					}
					
					final InetSocketAddress resolvedreceiver = receiver;
					PeerInfo info = peerinfos.get(resolvedreceiver);
					int state = info != null ? info.getState() : PeerInfo.STATE_UNKNOWN;
					if (state == PeerInfo.STATE_OK)
					{
						final int msgid = allocateMsgId();
						final PeerInfo peerinfo = info;
						SendMessageTask.executeTask(resolvedreceiver,
													msgid,
													task,
													peerinfo,
													inflightmessages,
													packetqueue,
													timedtaskdispatcher,
													flowcontrol);
					}
					else
					{
						final IResultCommand<IFuture<Void>, Void> lostconn = new IResultCommand<IFuture<Void>, Void>()
								{
							public IFuture<Void> execute(Void args)
							{
								return new Future<Void>(new IOException("Connection to peer lost: " + address));
							}
						};
						
						if (state == PeerInfo.STATE_LOST)
						{
							task.ready(lostconn);
						}
						else
						{
							// Untested connection, probe first.
							
							synchronized (peerinfos)
							{
								info = peerinfos.get(resolvedreceiver);
								if (info == null)
								{
									info = new PeerInfo(resolvedreceiver, timedtaskdispatcher);
									System.out.println("Creating peer:" + info);
									peerinfos.put(resolvedreceiver, info);
									
									TimedTask peerprober = new PeerProber(peerinfos, info, timedtaskdispatcher, packetqueue, inflightmessages);
									timedtaskdispatcher.executeNow(peerprober);
									
									//timedtaskdispatcher.scheduleTask(info.getFlowController());
								}
							}
								
							if (info.getState() == PeerInfo.STATE_OK)
							{
								timedtaskdispatcher.executeNow(new Runnable()
								{
									public void run()
									{
										sendMessage(address, task);
									}
								});
								return;
							}
							else
							{
								final PeerInfo peerinfo = info;
								info.getStateWaiters().offer(new Runnable()
								{
									public void run()
									{
										if (peerinfo.getState() == PeerInfo.STATE_OK)
										{
											sendMessage(address, task);
										}
										else
										{
											task.ready(lostconn);
										}
									}
								});
							}
						}
						
					}
				}
				else
				{
					task.ready(new IResultCommand<IFuture<Void>, Void>()
						{
							public IFuture<Void> execute(Void args)
							{
								return new Future<Void>(new RuntimeException("Unparsable address: " + address));
							}
						});
				}
			}
		});
	}
	
	/**
	 *  Returns the prefixes of this transport
	 *  @return Transport prefixes.
	 */
	public String[] getServiceSchemas()
	{
		return SCHEMAS;
	}
	
	/**
	 *  Get the addresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		return addresses;
	}
	
	/**
	 *  Allocates a message ID;
	 *  
	 *  @return A message ID or null if none is available.
	 */
	protected int allocateMsgId()
	{
		int ret = -1;
		synchronized (usedids)
		{
			do
			{
				ret = ++idcounter;
			}
			while (usedids.contains(ret));
		}
		
		return ret;
	}
}
