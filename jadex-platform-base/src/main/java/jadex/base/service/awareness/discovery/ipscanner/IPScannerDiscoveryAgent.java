package jadex.base.service.awareness.discovery.ipscanner;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.IDiscoveryService;
import jadex.base.service.awareness.discovery.SDiscovery;
import jadex.base.service.awareness.discovery.SlaveInfo;
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
import jadex.xml.annotation.XMLClassname;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.Reader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
	@Argument(name="scanfactor", clazz=long.class, defaultvalue="5", description="The delay between scanning as factor of delay time, e.g. 1=10000, 2=20000."),
//	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior)."),
	@Argument(name="buffersize", clazz=int.class, defaultvalue="1024*1024", description="The size of the send buffer (determines the number of messages that can be sent at once)."),
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
public class IPScannerDiscoveryAgent extends MicroAgent implements IDiscoveryService
{
	//-------- attributes --------
	
	/** The includes list. */
	protected String[] includes;
	
	/** The excludes list. */
	protected String[] excludes;

	
	/** The receiver port. */
	protected int port;
	
//	/** Flag for enabling fast startup awareness (pingpong send behavior). */
//	protected boolean fast;
	
	
	/** The socket to send. */
//	protected DatagramSocket sendsocket;
	protected DatagramChannel sendchannel;
	
	/** The send (remotes) delay. */
	protected long delay;
		
	/** The current send id. */
	protected String sendid;
	
	/** The current ip to send probes to. */
	protected int currentip;
	
	
	/** The socket to receive. */
	protected DatagramSocket receivesocket;
	
	/** Flag indicating agent killed. */
	protected boolean killed;
	
	/** The timer. */
	protected Timer	timer;
	
	/** The root component id. */
	protected IComponentIdentifier root;
	
	/** The java reader for parsing received awareness infos. */
	protected Reader reader;
	
	/** Flag indicating that the agent is started and the send behavior may be activated. */
	protected boolean started;
	
	/** Flag indicating that the agent has received its own discovery info. */
	protected boolean received_self;
	
	
	/** The local discovery services. */
	protected List locals;
	
	/** The local send socket. */
	protected DatagramSocket localsocket;
	
	/** The remotes discovery services. */
	protected Set remotes;
	
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
		
		initArguments();
		
		try
		{
//			this.sendsocket = new DatagramSocket();
			sendchannel = DatagramChannel.open();
			sendchannel.configureBlocking(false);
			sendchannel.socket().setSendBufferSize(((Integer)getArgument("buffersize")).intValue());
					
			this.reader	= JavaReader.getReader(new XMLReporter()
			{
				public void report(String message, String type, Object related, Location location) throws XMLStreamException
				{
					// Ignore XML exceptions.
//					getLogger().warning(message);
				}
			});
			
			ret.setResult(null);
		}
		catch(IOException e)
		{
			ret.setException(new RuntimeException(e));
		}
		
		return ret;
	}
	
	/**
	 *  Read arguments and set initial values. 
	 */
	protected void initArguments()
	{
		this.port = ((Number)getArgument("port")).intValue();
		this.delay = ((Number)getArgument("delay")).longValue();
		this.scanfactor = ((Number)getArgument("scanfactor")).intValue();
//		this.fast = ((Boolean)getArgument("fast")).booleanValue();
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		root = getComponentIdentifier().getRoot();
		
		// Wait before starting send behavior to not miss fast awareness pingpong replies,
		// because receiver thread is not yet running. (hack???)
		startReceiving().addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				started	= true;
				startSendBehaviour();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Send also when receiving does not work?
				started	= true;
				startSendBehaviour();
			}
		}));
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture	agentKilled()
	{
		killed = true;
		
		if(sendchannel!=null)
		{
			send(new AwarenessInfo(root, AwarenessInfo.STATE_OFFLINE, delay));
		}
		
//		System.out.println("killed set to true: "+getComponentIdentifier());
		synchronized(IPScannerDiscoveryAgent.this)
		{
			if(sendchannel!=null)
			{
				try
				{
					sendchannel.close();
				}
				catch(Exception e)
				{
				}
			}
			if(receivesocket!=null)
			{
				try
				{
					receivesocket.close();
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
		this.includes = includes;
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
		this.excludes = excludes;
	}
	
	/**
	 *  Start sending of message
	 */
	public void send(final AwarenessInfo info)
	{
		try
		{
			byte[] data = SDiscovery.encodeObject(info, getModel().getClassLoader());
			
			int maxsend = sendchannel.socket().getSendBufferSize()/data.length;
			int sent = 0;
			
			// Send to all remote other nodes a refresh awareness
			int allowed = maxsend-sent;
			int remotes = 0;
			if(allowed>0)
			{
				remotes = sendToRemotes(data, allowed);
				sent += remotes;
			}
			
			// Send to possibly new ones via ip guessing
			if(sendcount%scanfactor==0)
			{
				allowed = maxsend-sent;
				int discover = 0;
				if(allowed>0)
				{
					discover += sendToDiscover(data, allowed);
					sent+= discover;
				}
			}
			
			// Send to all locals a refresh awareness
			sendToLocals(data);

//			System.out.println(" sent:"+sent+" remotes: "+remotes+" discover: "+discover);
//			System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
		}
		catch(Exception e)
		{
			getLogger().warning("Could not send awareness message: "+e);
//			e.printStackTrace();
		}	
		
		sendcount++;
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
			InetAddress[] remotes = getRemotes();
			for(; ret<remotes.length && ret<maxsend; ret++)
			{
				ByteBuffer buf = ByteBuffer.allocate(data.length);
				buf.clear();
				buf.put(data);
				buf.flip();
				int	bytes = sendchannel.send(buf, new InetSocketAddress(remotes[ret], port));
				if(bytes!=data.length)
					break;
			}
			
//			System.out.println("sent to remotes: "+ret+" "+SUtil.arrayToString(remotes));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
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
			InetAddress iadr = SDiscovery.getInet4Address();
			short sublen = SDiscovery.getNetworkPrefixLength(iadr);
			if(sublen==-1) // Guess C class if nothing can be determined.
				sublen = 24;
			byte[] byinet = SDiscovery.getInet4Address().getAddress();
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
				ByteBuffer buf = ByteBuffer.allocate(data.length);
				buf.clear();
				buf.put(data);
				buf.flip();
				try
				{
					int	bytes = sendchannel.send(buf, new InetSocketAddress(address, port));
					if(bytes!=data.length)
						break;
				}
				catch(Exception e)
				{
					// Can happen in case of specific reserved ips, e.g. 0 or 255=broacast.
//					System.out.println("ex: "+address);
				}
				ipnum = (ipnum+1)%numips;
			}
			currentip = ipnum;
			
//			System.out.println("sent to discover: "+ret+" "+currentip);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Forward to locals.
	 *  @param data The data to be send.
	 *  @param port The port the local slave listens.
	 */
	protected void sendToLocal(byte[] data, int port)
	{
		try
		{
			if(localsocket==null)
				localsocket = new DatagramSocket();
			InetAddress address = SDiscovery.getInet4Address();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			localsocket.send(packet);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Send/forward to locals.
	 *  @param data The data to be send.
	 */
	protected void sendToLocals(byte[] data)
	{
		Integer[] locals = getlocals();
		for(int i=0; i<locals.length; i++)
		{
			sendToLocal(data, locals[i].intValue());
		}
	}
	
	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public synchronized long getDelay()
	{
		return delay;
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
		if(this.delay!=delay)
		{
			this.delay = delay;
			startSendBehaviour();
		}
	}
	
//	/**
//	 *  Set the fast startup awareness flag
//	 */
//	public void setFastAwareness(boolean fast)
//	{
//		this.fast = fast;
//	}
//	
//	/**
//	 *  Get the fast startup awareness flag.
//	 *  @return The fast flag.
//	 */
//	public boolean isFastAwareness()
//	{
//		return this.fast;
//	}
	
	/**
	 *  Get the sendid.
	 *  @return the sendid.
	 */
	public String getSendId()
	{
		return sendid;
	}

	/**
	 *  Set the sendid.
	 *  @param sendid The sendid to set.
	 */
	public void setSendId(String sendid)
	{
		this.sendid = sendid;
	}
	
	/**
	 *  Start sending awareness infos.
	 *  (Ends automatically when a new send behaviour is started).
	 */
	protected void startSendBehaviour()
	{
		if(started)
		{
			final String sendid = SUtil.createUniqueId(getAgentName());
			this.sendid = sendid;	
			
			scheduleStep(new IComponentStep()
			{
				@XMLClassname("send")
				public Object execute(IInternalAccess ia)
				{
					if(!killed && sendid.equals(getSendId()))
					{
//						System.out.println(System.currentTimeMillis()+" sending: "+getComponentIdentifier());
						send(new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, delay, includes, excludes));
						
						if(delay>0)
							doWaitFor(delay, this);
					}
					return null;
				}
			});
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
						byte buf[] = new byte[65535];
						
						while(!killed)
						{
							try
							{
								// Init receive socket
								getReceiveSocket();
								
								ret.setResultIfUndone(null);
								
								DatagramPacket pack = new DatagramPacket(buf, buf.length);
								getReceiveSocket().receive(pack);
//								System.out.println("received: "+getComponentIdentifier());
								
								handleReceivedPacket(pack);
							}
							catch(Exception e)
							{
//								getLogger().warning("Receiving awareness info error: "+e);
								ret.setExceptionIfUndone(e);
							}
						}
						
						synchronized(IPScannerDiscoveryAgent.this)
						{
							if(receivesocket!=null)
							{
								try
								{
									receivesocket.close();
								}
								catch(Exception e)
								{
//									getLogger().warning("Receiving socket closing error: "+e);
								}
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
	 *  Get or create a receiver socket.
	 */
	protected synchronized DatagramSocket getReceiveSocket()
	{
		if(!killed)
		{
			if(receivesocket==null)
			{
				try
				{
					receivesocket = new DatagramSocket(port);
					System.out.println("local master at: "+SDiscovery.getInet4Address()+" "+port);
				}
				catch(Exception e)
				{
					try
					{
						// In case the receiversocket cannot be opened
						// open another local socket at an arbitrary port
						// and send this port to the master.
						receivesocket = new DatagramSocket();
						InetAddress address = SDiscovery.getInet4Address();
						AwarenessInfo info = new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, delay, includes, excludes);
						SlaveInfo si = new SlaveInfo(info);
						byte[] data = SDiscovery.encodeObject(si, getModel().getClassLoader());
						DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
						receivesocket.send(packet);
	
						System.out.println("local slave at: "+SDiscovery.getInet4Address()+" "+receivesocket.getLocalPort());
						
	//						getLogger().warning("Running in local mode: "+e);
					}
					catch(Exception e2)
					{
						throw new RuntimeException(e2);
					}
				}
			}
		}
		
		return receivesocket;
	}
	
	/**
	 *  Handle a received packet.
	 */
	protected void handleReceivedPacket(DatagramPacket pack)
	{
		byte[] data = new byte[pack.getLength()];
		System.arraycopy(pack.getData(), 0, data, 0, pack.getLength());
		Object obj = SDiscovery.decodeObject(data, getModel().getClassLoader());
//		Object obj = decodePacket(pack);
		
		if(obj instanceof SlaveInfo)
		{
			// Received local port -> forward all awareness package to local.
			SlaveInfo si = (SlaveInfo)obj;
			addLocal(pack.getPort());
			AwarenessInfo info = new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, delay, includes, excludes);
			byte[] mydata = SDiscovery.encodeObject(info, getModel().getClassLoader());
			sendToLocal(mydata, pack.getPort());
			announceAwareness(si.getAwarenessInfo());
		}
		else if(obj instanceof AwarenessInfo)
		{
//			System.out.println("rec awa: "+obj);
			final AwarenessInfo info = (AwarenessInfo)obj;
			if(info.getSender()!=null)
			{
				sendToLocals(data);
				
				if(info.getSender().equals(root))
				{
					received_self	= true;
				}
				else
				{
					announceAwareness(info);
					addRemote(pack.getAddress());
				}
//				System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());
			}
		}
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
////												System.out.println(System.currentTimeMillis()+" fast discovery: "+getComponentIdentifier()+", "+sender);
//					received_self	= false;
//					waitFor((long)(Math.random()*500), new IComponentStep()
//					{
//						int	cnt;
//						public Object execute(IInternalAccess ia)
//						{
//							if(!received_self)
//							{
//								cnt++;
////															System.out.println("CSMACD try #"+(++cnt));
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
	 *  Add a remote discovery service ip of another platform.
	 *  @param address The remote ip address.
	 */
	protected synchronized void addRemote(InetAddress address)
	{
		try
		{
			if(!SDiscovery.getInet4Address().equals(address))
			{
				if(remotes==null)
					remotes = new LinkedHashSet();
				remotes.add(address);
				
				System.out.println("remotes: "+remotes);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Get the remotes other discovery services (at most one per ip).
	 *  @return All known remote platforms.
	 */
	protected synchronized InetAddress[] getRemotes()
	{
		return remotes==null? new InetAddress[0]: (InetAddress[])remotes.toArray(new InetAddress[remotes.size()]);
	}
	
	/**
	 *  Add a local platform.
	 *  @param port The port.
	 */
	protected synchronized void addLocal(int port)
	{
		if(locals==null)
			locals = new ArrayList();
		locals.add(new Integer(port));
		
		System.out.println("locals: "+locals);
	}
	
	/**
	 *  Get all locals.
	 *  @return All local slave platforms.
	 */
	protected synchronized Integer[] getlocals()
	{
		return locals==null? new Integer[0]: (Integer[])locals.toArray(new Integer[locals.size()]);
	}
	
	/**
	 *  Get the current time.
	 */
	protected long getClockTime()
	{
//		return clock.getTime();
		return System.currentTimeMillis();
	}
	
	/**
	 *  Overriden wait for to not use platform clock.
	 */
	protected void	doWaitFor(long delay, final IComponentStep step)
	{
//		waitFor(delay, step);
		
		if(timer==null)
			timer	= new Timer(true);
		
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				scheduleStep(step);
			}
		}, delay);
	}
}
