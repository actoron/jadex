package jadex.base.service.awareness.discovery.ipmulticast;

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
import java.net.MulticastSocket;
import java.net.UnknownHostException;
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
	@Argument(name="address", clazz=String.class, defaultvalue="\"224.0.0.0\"", description="The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255)."),
	@Argument(name="port", clazz=int.class, defaultvalue="55667", description="The port used for finding other agents."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior).")
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
public class MulticastDiscoveryAgent extends MicroAgent implements IDiscoveryService
{
	//-------- attributes --------
	
	/** The includes list. */
	protected String[] includes;
	
	/** The excludes list. */
	protected String[] excludes;
	
	
	/** The multicast internet address. */
	protected InetAddress address;
	
	/** The receiver port. */
	protected int port;
	
	/** Flag for enabling fast startup awareness (pingpong send behavior). */
	protected boolean fast;
	
	
	/** The socket to send. */
	protected MulticastSocket sendsocket;
	
	/** The send delay. */
	protected long delay;
		
	/** The current send id. */
	protected String sendid;
	
	
	/** The socket to send. */
	protected MulticastSocket receivesocket;
	
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
	
	/** The current receive address. */
	protected InetAddress myaddress;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		initArguments();
		
		try
		{
			this.sendsocket = new MulticastSocket();
			this.sendsocket.setLoopbackMode(true);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		this.reader	= JavaReader.getReader(new XMLReporter()
		{
			public void report(String message, String type, Object related, Location location) throws XMLStreamException
			{
				// Ignore XML exceptions.
//				getLogger().warning(message);
			}
		});
		
		return IFuture.DONE;
	}
	
	/**
	 *  Read arguments and set initial values. 
	 */
	protected void initArguments()
	{
		try
		{
			this.address = InetAddress.getByName((String)getArgument("address"));			
		}
		catch(UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
		if(address==null)
			throw new NullPointerException("Cannot get address: "+getArgument("address"));
		this.port = ((Number)getArgument("port")).intValue();
		this.delay = ((Number)getArgument("delay")).longValue();
		this.fast = ((Boolean)getArgument("fast")).booleanValue();
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
		
		if(sendsocket!=null)
		{
			send(new AwarenessInfo(root, AwarenessInfo.STATE_OFFLINE, delay));
		}
		
//		System.out.println("killed set to true: "+getComponentIdentifier());
		synchronized(MulticastDiscoveryAgent.this)
		{
			if(sendsocket!=null)
			{
				sendsocket.close();
			}
			if(receivesocket!=null)
			{
				try
				{
					receivesocket.leaveGroup(address);
				}
				catch(Exception e)
				{
				}
				finally
				{
					receivesocket.close();
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
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			sendsocket.send(packet);
//			System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
		}
		catch(Exception e)
		{
			getLogger().warning("Could not send awareness message: "+e);
//			e.printStackTrace();
		}	
	}
	
	/**
	 *  Get the address.
	 *  @return the address.
	 */
	public synchronized Object[] getAddressInfo()
	{
		return new Object[]{address, new Integer(port)};
	}

	/**
	 *  Set the address.
	 *  @param address The address to set.
	 */
	public synchronized void setAddressInfo(InetAddress address, int port)
	{
//		System.out.println("setAddress: "+address+" "+port);
		this.address = address;
		this.port = port;
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
	
	/**
	 *  Set the fast startup awareness flag
	 */
	public void setFastAwareness(boolean fast)
	{
		this.fast = fast;
	}
	
	/**
	 *  Get the fast startup awareness flag.
	 *  @return The fast flag.
	 */
	public boolean isFastAwareness()
	{
		return this.fast;
	}
	
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
						
//						InetAddress myaddress = null;
						
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
						
						synchronized(MulticastDiscoveryAgent.this)
						{
							if(receivesocket!=null)
							{
								try
								{
									receivesocket.leaveGroup(address);
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
			Object[] ai = getAddressInfo();
			InetAddress curaddress = (InetAddress)ai[0];
			int curport = ((Integer)ai[1]).intValue();
			
			if(receivesocket!=null && (receivesocket.getPort()!=curport || !SUtil.equals(curaddress, myaddress)))
			{
				try
				{
					receivesocket.leaveGroup(myaddress);
					receivesocket.close();
				}
				catch(Exception e)
				{
				}
				receivesocket = null;
			}
			if(receivesocket==null)
			{
				try
				{
					receivesocket = new MulticastSocket(curport);
					receivesocket.joinGroup(curaddress);
					myaddress = curaddress;
				}
				catch(Exception e)
				{
					receivesocket	= null;
					getLogger().warning("Awareness error when joining mutlicast group: "+e);
					throw new RuntimeException(e);
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

		if(obj instanceof AwarenessInfo)
		{
			final AwarenessInfo info = (AwarenessInfo)obj;
			
			if(info.getSender()!=null)
			{
				if(info.getSender().equals(root))
					received_self	= true;
				
	//			System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());
				
				getRequiredService("management").addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IManagementService ms = (IManagementService)result;
						ms.addAwarenessInfo(info);
						
	//					if(initial && fast && started && !killed)
	//					{
	////						System.out.println(System.currentTimeMillis()+" fast discovery: "+getComponentIdentifier()+", "+sender);
	//						received_self	= false;
	//						waitFor((long)(Math.random()*500), new IComponentStep()
	//						{
	//							int	cnt;
	//							public Object execute(IInternalAccess ia)
	//							{
	//								if(!received_self)
	//								{
	//									cnt++;
	////									System.out.println("CSMACD try #"+(++cnt));
	//									send(new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, delay, includes, excludes));
	//									waitFor((long)(Math.random()*500*cnt), this);
	//								}
	//								return null;
	//							}
	//						});
	//					}
					}
				});
			}
		}
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
