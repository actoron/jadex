package jadex.base.service.awareness.discovery.ipscanner;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.DiscoveryEntry;
import jadex.base.service.awareness.discovery.DiscoveryState;
import jadex.base.service.awareness.discovery.IDiscoveryService;
import jadex.base.service.awareness.discovery.LeaseTimeHandler;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.base.service.awareness.management.IManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/* $if !android $ */
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
/* $else $ 
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLStreamException;
$endif $ */

/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(
{
	@Argument(name="port", clazz=int.class, defaultvalue="55668", description="The port used for finding other agents."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="scanfactor", clazz=long.class, defaultvalue="1", description="The delay between scanning as factor of delay time, e.g. 1=10000, 2=20000."),
//	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior)."),
	@Argument(name="buffersize", clazz=int.class, defaultvalue="1024*1024", description="The size of the send buffer (determines the number of messages that can be sent at once).")
})
@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000")),
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000")),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"))
})
@ProvidedServices(
	@ProvidedService(type=IDiscoveryService.class, implementation=@Implementation(expression="$component"))
)
@RequiredServices(
{
	@RequiredService(name="threadpool", type=IThreadPoolService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="management", type=IManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class ScannerDiscoveryAgent extends MicroAgent implements IDiscoveryService
{
	//-------- attributes --------
	
	/** The agent state. */
	protected DiscoveryState state;

	/** The send handler. */
	protected SendHandler sender;
	
	/** The local slaves. */
	protected LeaseTimeHandler locals;
	
	/** The local slaves. */
	protected LeaseTimeHandler remotes;

	
	/** The receiver port. */
	protected int port;
	
	/** The current ip to send probes to. */
	protected int currentip;
	
	
	/** The socket to receive. */
	protected DatagramChannel channel;
	protected Selector selector;
	
	/** The root component id. */
	protected IComponentIdentifier root;
	
//	/** Flag indicating that the agent has received its own discovery info. */
//	protected boolean received_self;
	
	/** The scan delay factor. */
	protected int scanfactor;
	
	/** The send counter. */
	protected int sendcount;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		Future ret = new Future();
		try
		{
			this.state = new DiscoveryState(getExternalAccess());
			this.selector = Selector.open();
			initArguments();
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Read arguments and set initial values. 
	 */
	protected void initArguments()
	{
		this.port = ((Number)getArgument("port")).intValue();
		this.scanfactor = ((Number)getArgument("scanfactor")).intValue();
		state.setDelay(((Number)getArgument("delay")).longValue());
//		this.fast = ((Boolean)getArgument("fast")).booleanValue();
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		root = getComponentIdentifier().getRoot();
		
		this.locals = new LeaseTimeHandler(state);
		this.remotes = new LeaseTimeHandler(state)
		{
			public void entryDeleted(DiscoveryEntry entry)
			{
				// If master is lost, try to become master
				if(entry.isMaster())
				{
					System.out.println("Master deleted.");
					try
					{
						synchronized(ScannerDiscoveryAgent.this)
						{
//							receivesocket.close();
//							receivesocket = null;
							if(channel!=null)
							{
								channel.close();
								channel = null;
							}
							getChannel();
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		};
		
		root = getComponentIdentifier().getRoot();
		
		// Wait before starting send behavior to not miss fast awareness pingpong replies,
		// because receiver thread is not yet running. (hack???)
		startReceiving().addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				state.setStarted(true);
				sender = new ScannerSendHandler(state);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Send also when receiving does not work?
				state.setStarted(true);
				sender = new ScannerSendHandler(state);
			}
		}));
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture	agentKilled()
	{
		state.setKilled(true);
		
		DatagramChannel channel = getChannel();
		if(channel!=null)
		{
			sender.send(state.createAwarenessInfo(AwarenessInfo.STATE_OFFLINE, !isMaster()));
		}
		
//		System.out.println("killed set to true: "+getComponentIdentifier());
		synchronized(ScannerDiscoveryAgent.this)
		{
			if(channel!=null)
			{
				try
				{
					channel.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes)
	{
		state.setIncludes(includes);
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
		state.setExcludes(excludes);
	}
	
	/**
	 *  Send a packet over the channel.
	 */
	protected boolean send(byte[] data, InetAddress address, int port)
	{
		boolean ret = true;
		
//		System.out.println("sending to: "+address+" "+port);
		
		try
		{
			ByteBuffer buf = ByteBuffer.allocate(data.length);
//			buf.clear();
			buf.put(data);
			buf.flip();
			int	bytes = getChannel().send(buf, new InetSocketAddress(address, port));
			ret = bytes==data.length;
		}
		catch(Exception e)
		{
			// Can happen in case of specific reserved ips, e.g. 0 or 255=broacast.
//			System.out.println("ex: "+address);
		}
		
		return ret;
	}
	
	/**
	 *  Send a packet over the channel.
	 */
	protected Object[] receive(ByteBuffer buf)
	{
		Object[] ret = null;
		
		try
		{
			buf.clear();
			SocketAddress address = getChannel().receive(buf);
			if(address!=null)
			{
				buf.flip();
				byte[] data = new byte[buf.remaining()];
				buf.get(data);
				ret = new Object[]{address, data};
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			System.out.println("ex: "+address);
		}
		
		return ret;
	}
	
	/**
	 *  Send awareness info to remote scanner services.
	 *  @param data The data to be send.
	 *  @param maxsend The maximum number of messages to send.
	 */
	protected int sendToRemotes(byte[] data)
	{
		return sendToRemotes(data, -1);
	}
	
	/**
	 *  Send awareness info to remote scanner services.
	 *  @param data The data to be send.
	 *  @param maxsend The maximum number of messages to send.
	 */
	protected int sendToRemotes(byte[] data, int maxsend)
	{
		int ret = 0;
		try
		{
			DiscoveryEntry[] rems = remotes.getEntries();
			for(int i=0; i<rems.length && (maxsend==-1 || ret<maxsend); i++)
			{
				// Only send to remote masters directly.
				// A master will forward a message to its slaves.
				if(!rems[i].getInfo().isIgnore())
				{
					InetSocketAddress sa = (InetSocketAddress)rems[i].getEntry();
					// Use received port, as enables slave to slave communication
					if(!send(data, sa.getAddress(), sa.getPort()))
						break;
					ret++;
				}
			}
			
			System.out.println("sent to remotes: "+ret+" "+SUtil.arrayToString(remotes));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Send to local masters.
	 *  @param data The data to be send.
	 */
	protected void sendToMaster(byte[] data)
	{
		send(data, SUtil.getInet4Address(), port);
	}
	
	/**
	 *  Send messages to discover new platforms.
	 *  @param data The data to be send.
	 *  @param maxsend The maximum number of messages to send.
	 */
	protected int sendToDiscover(byte[] data, int maxsend)
	{
		int ret = 0;
		try
		{
			InetAddress iadr = SUtil.getInet4Address();
			short sublen = SUtil.getNetworkPrefixLength(iadr);
			if(sublen==-1) // Guess C class if nothing can be determined.
				sublen = 24;
			byte[] byinet = SUtil.getInet4Address().getAddress();
			int hostbits = 32-sublen;
			int numips = (int)Math.pow(2, hostbits);
			
			int mask = ~(numips-1);
			int iinet = SUtil.bytesToInt(byinet);
			int prefix = iinet & mask;
			
			int ipnum = currentip;
			for(; ret<numips && ret<maxsend; ret++)
			{
				int iip = prefix | ipnum; 
				byte[] bip = SUtil.intToBytes(iip);
				InetAddress address = InetAddress.getByAddress(bip);
				if(!send(data, address, port))
					break;
				
				ipnum = (ipnum+1)%numips;
			}
			currentip = ipnum;
			
			System.out.println("sent to discover: "+ret+" "+currentip);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Send/forward to locals.
	 *  @param data The data to be send.
	 */
	protected void sendToLocals(byte[] data)
	{
		DiscoveryEntry[] locs = locals.getEntries();
		for(int i=0; i<locs.length; i++)
		{
			InetSocketAddress sa = (InetSocketAddress)locs[i].getEntry();
			send(data, sa.getAddress(), sa.getPort());
		}
		System.out.println("sent to locals: "+locs.length+" "+SUtil.arrayToString(locs));
	}
	
	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public synchronized void setDelay(long delay)
	{
//		System.out.println("setDelay: "+delay+" "+getComponentIdentifier());
//		if(this.delay>=0 && delay>0)
//			scheduleStep(send);
		if(state.getDelay()!=delay)
		{
			state.setDelay(delay);
			sender.startSendBehavior();
		}
	}
	
	
	/**
	 *  Start receiving awareness infos.
	 *  @return A future indicating when the receiver thread is ready.
	 */
	public IFuture	startReceiving()
	{
		final Future	ret	= new Future();
		
		// Start the receiver thread.
		getServiceContainer().getRequiredService("threadpool").addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IThreadPoolService tp = (IThreadPoolService)result;
				
				tp.execute(new Runnable()
				{
					public void run()
					{
						// todo: max ip datagram length (is there a better way to determine length?)
//						byte buf[] = new byte[65535];
						ByteBuffer buf = ByteBuffer.allocate(8192);
						
						try
						{
							getChannel();
							ret.setResult(null);
						}
						catch(Exception e)
						{
							ret.setResultIfUndone(e);
							return;
						}
							
						while(!state.isKilled())
						{
							try
							{
								selector.select();
								// Let thread wait for new channel being registered.
								synchronized(ScannerDiscoveryAgent.this)
								{
								}
								
//								System.out.println("selector");
							    Iterator it = selector.selectedKeys().iterator();
							    while(it.hasNext()) 
							    {
							        SelectionKey key = (SelectionKey)it.next();
							        it.remove();
							        if(key.isValid() && key.isReadable()) 
							        {
//							        	System.out.println("key: "+key+" "+key.channel());
							            final Object[] packet = receive(buf);
//										final DatagramPacket pack = new DatagramPacket(buf, buf.length);
//										getReceiveSocket().receive(pack);
//										System.out.println("received: "+getComponentIdentifier());
										
							            if(packet!=null)
							            {
											scheduleStep(new IComponentStep()
											{
												public Object execute(IInternalAccess ia) 
												{
													handleReceivedPacket((InetSocketAddress)packet[0], (byte[])packet[1]);
													return null;
												};
											});
							            }
							        }
							    }
							}
							catch(Exception e)
							{
//								getLogger().warning("Receiving awareness info error: "+e);
								ret.setExceptionIfUndone(e);
							}
						}
						
//						System.out.println("comp and receiver terminated: "+getComponentIdentifier());
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				getLogger().warning("Awareness agent problem, could not get threadpool service: "+exception);
//				exception.printStackTrace();
				ret.setExceptionIfUndone(exception);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get or create a channel.
	 */
	protected synchronized DatagramChannel getChannel()
	{
		if(!state.isKilled())
		{
//			if(receivesocket==null)
			if(channel==null)
			{
				try
				{
					channel = DatagramChannel.open();
					channel.configureBlocking(false);
					channel.socket().bind(new InetSocketAddress(port));
					channel.socket().setSendBufferSize(((Integer)getArgument("buffersize")).intValue());
					// Register blocks when other thread waits in it.
					// Must be synchronized due to selector wakeup freeing other thread.
					synchronized(ScannerDiscoveryAgent.this)
					{
						selector.wakeup();
						channel.register(selector, SelectionKey.OP_READ);
					}
					System.out.println("local master at: "+SUtil.getInet4Address()+" "+port);
				}
				catch(Exception e)
				{
					try
					{
						// In case the receiversocket cannot be opened
						// open another local socket at an arbitrary port
						// and send this port to the master.
						channel = DatagramChannel.open();
						channel.configureBlocking(false);
						channel.socket().bind(new InetSocketAddress(0));
						channel.socket().setSendBufferSize(((Integer)getArgument("buffersize")).intValue());
						synchronized(ScannerDiscoveryAgent.this)
						{
							selector.wakeup();
							channel.register(selector, SelectionKey.OP_READ);
						}
						InetAddress address = SUtil.getInet4Address();
						AwarenessInfo info = state.createAwarenessInfo(AwarenessInfo.STATE_OFFLINE, !isMaster());
						byte[] data = DiscoveryState.encodeObject(info, getModel().getClassLoader());
						send(data, address, port);
						
						System.out.println("local slave at: "+SUtil.getInet4Address()+" "+channel.socket().getLocalPort());
//						getLogger().warning("Running in local mode: "+e);
					}
					catch(Exception e2)
					{
						e2.printStackTrace();
						throw new RuntimeException(e2);
					}
				}
			}
		}
		
		return channel;
//		return receivesocket;
	}
	
	/**
	 *  Handle a received packet.
	 */
	protected void handleReceivedPacket(InetSocketAddress sa, byte[] data)
	{
		InetAddress address = sa.getAddress();
		int port = sa.getPort();
				
//		byte[] data = new byte[pack.getLength()];
//		System.arraycopy(pack.getData(), 0, data, 0, pack.getLength());
		AwarenessInfo info = (AwarenessInfo)DiscoveryState.decodeObject(data, getModel().getClassLoader());
		
//		System.out.println("received: "+obj+" "+address);
			
		if(info!=null && info.getSender()!=null)
		{
			if(!info.getSender().equals(root))
			{
				announceAwareness(info);
			}
//			else
//			{
//				received_self	= true;
//			}
//			System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());
		}	
		
		// Received awareness info
		// When master -> 
		//   if slave info -> save in locals and sent to remote masters and local slaves
		//   if remote info -> save in remotes and send to local slaves
		// When slave ->
		//   save as remote info (also other slaves, for lease time management)
		
		if(isMaster())
		{
			if(address.equals(SUtil.getInet4Address()))
			{
				// If awareness message comes from local slave.
				locals.updateEntry(new DiscoveryEntry(info, state.getClockTime(), sa, false));
				
				// Forward the slave update to remote masters.
				sendToRemotes(data);
			}
			else
			{
				// If awareness message comes from remove node.
				remotes.addOrUpdateEntry(new DiscoveryEntry(info, state.getClockTime(), sa, false));
			}
			
			sendToLocals(data);
		}
		else
		{
			remotes.addOrUpdateEntry(new DiscoveryEntry(info, state.getClockTime(), sa, false));
		}
			
		System.out.println("received awa info: "+getComponentIdentifier().getLocalName()+" "+info.getSender());
	}
	
	/**
	 *  Test if is master.
	 */
	protected boolean isMaster()
	{
		return this.port==getChannel().socket().getLocalPort();
	}
	
	/**
	 *  Announce newly arrived awareness info to management service.
	 */
	protected void announceAwareness(final AwarenessInfo info)
	{
//		System.out.println("announcing: "+info);
		getRequiredService("management").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IManagementService ms = (IManagementService)result;
				ms.addAwarenessInfo(info);
				
//				if(initial && fast && started && !killed)
//				{
////				System.out.println(System.currentTimeMillis()+" fast discovery: "+getComponentIdentifier()+", "+sender);
//					received_self	= false;
//					waitFor((long)(Math.random()*500), new IComponentStep()
//					{
//						int	cnt;
//						public Object execute(IInternalAccess ia)
//						{
//							if(!received_self)
//							{
//								cnt++;
////							System.out.println("CSMACD try #"+(++cnt));
//								send(new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, delay, includes, excludes));
//								waitFor((long)(Math.random()*500*cnt), this);
//							}
//							return null;
//						}
//					});
//				}
			}
		});
	}
		
	/**
	 *  Handle sending.
	 */
	class ScannerSendHandler extends SendHandler
	{
		/**
		 *  Create a new lease time handling object.
		 */
		public ScannerSendHandler(DiscoveryState state)
		{
			super(state);
		}
		
		/**
		 *  Create the awareness info.
		 */
		public AwarenessInfo createAwarenessInfo()
		{
			return state.createAwarenessInfo(AwarenessInfo.STATE_ONLINE, !isMaster());
		}
		
		/**
		 *  Method to send messages.
		 */
		public void send(AwarenessInfo info)
		{
			try
			{
				byte[] data = DiscoveryState.encodeObject(info, getModel().getClassLoader());
				
				int maxsend = getChannel().socket().getSendBufferSize()/data.length;
				int sent = 0;
				
				// Send to all remote other nodes a refresh awareness
				int allowed = maxsend-sent;
				int remotes = 0;
				
				if(isMaster())
				{
					if(allowed>0)
					{
						remotes = sendToRemotes(data, allowed);
						sent += remotes;
					}
					
					// Send to all locals a refresh awareness
					sendToLocals(data);
				}
				else
				{
					sendToMaster(data);
				}
				
				// Send to possibly new ones via ip guessing
//				if(sendcount%scanfactor==0)
//				{
//					allowed = maxsend-sent;
//					int discover = 0;
//					if(allowed>0)
//					{
//						discover += sendToDiscover(data, allowed);
//						sent+= discover;
//					}
//				}

//				System.out.println(" sent:"+sent+" remotes: "+remotes);//+" discover: "+discover);
//				System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
			}
			catch(Exception e)
			{
				getLogger().warning("Could not send awareness message: "+e);
				e.printStackTrace();
			}	
			
			sendcount++;
		}
	}
}
