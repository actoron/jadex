package jadex.base.service.awareness.discovery.registry;

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
import jadex.xml.annotation.XMLClassname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
//	@Argument(name="address", clazz=String.class, defaultvalue="\"192.168.56.1\"", description="The ip address of registry."),
	@Argument(name="address", clazz=String.class, defaultvalue="\"134.100.11.217\"", description="The ip address of registry."),
	@Argument(name="port", clazz=int.class, defaultvalue="55699", description="The port used for finding other agents."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds).")
//	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior)."),
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
public class RegistryDiscoveryAgent extends MicroAgent implements IDiscoveryService
{
	//-------- attributes --------
	
	/** The agent state. */
	protected DiscoveryState state;

	/** The send handler. */
	protected SendHandler sender;
	
	/** The known platforms. */
	protected LeaseTimeHandler knowns;


	/** The registry internet address. */
	protected InetAddress address;

	/** The receiver port. */
	protected int port;
		
	/** The socket to send/receive. */
	protected DatagramSocket socket;		
	
	/** The root component id. */
	protected IComponentIdentifier root;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		this.state = new DiscoveryState(getExternalAccess());
		initArguments();
			
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
		state.setDelay(((Number)getArgument("delay")).longValue());
//		this.fast = ((Boolean)getArgument("fast")).booleanValue();
	}
	
	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
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
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		root = getComponentIdentifier().getRoot();
		
		this.knowns = new LeaseTimeHandler(state)
		{
			public void entryDeleted(DiscoveryEntry entry)
			{
				System.out.println("Entry deleted: "+entry.getInfo().getSender());
				
				Object[] tmp = (Object[])entry.getEntry();
				if(isRegistry((InetAddress)tmp[0], ((Integer)tmp[1]).intValue()))
				{
					try
					{
						if(socket!=null)
							socket.close();
					}
					catch(Exception e)
					{
					}
					socket = null;
					getSocket();
				}
			}
		};
		
		// Wait before starting send behavior to not miss fast awareness pingpong replies,
		// because receiver thread is not yet running. (hack???)
		startReceiving().addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				state.setStarted(true);
				sender = new RegistrySendHandler(state);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Send also when receiving does not work?
				state.setStarted(true);
				sender = new RegistrySendHandler(state);
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
		
		DatagramSocket socket = getSocket();
		if(socket!=null)
		{
			sender.send(state.createAwarenessInfo(AwarenessInfo.STATE_OFFLINE, false));
		}
		
//		System.out.println("killed set to true: "+getComponentIdentifier());
		synchronized(RegistryDiscoveryAgent.this)
		{
			if(socket!=null)
			{
				try
				{
					socket.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return IFuture.DONE;
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
						
						try
						{
							// Init receive socket
							getSocket();
							ret.setResultIfUndone(null);
							
							while(!state.isKilled())
							{
								try
								{
									final DatagramPacket pack = new DatagramPacket(buf, buf.length);
									getSocket().receive(pack);
									scheduleStep(new IComponentStep()
									{
										public Object execute(IInternalAccess ia)
										{
											handleReceivedPacket(pack);
											return null;
										}
									});
//									System.out.println("received: "+getComponentIdentifier());
								}
								catch(Exception e)
								{
									// Can happen if is slave and master goes down.
									// In that case it tries to find new master.
//									getLogger().warning("Receiving awareness info error: "+e);
									ret.setExceptionIfUndone(e);
								}
							}
						}
						catch(Exception e) 
						{
							ret.setException(e);
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
	 *  
	 *  Note, this method has to be synchronized.
	 *  Is called from receiver as well as component thread.
	 */
	protected synchronized DatagramSocket getSocket()
	{
		if(!state.isKilled())
		{
			if(socket==null)
			{
				// Try to become registry
				if(address.equals(SUtil.getInet4Address()))
				{
					try
					{
						// First one on dest ip becomes registry.
						socket = new DatagramSocket(port);
//						System.out.println("registry: "+getComponentIdentifier());
//						System.out.println("local master at: "+SDiscovery.getInet4Address()+" "+port);
					}
					catch(Exception e)
					{
						// If not first it will be client and use any port.
					}
				}
				
				if(socket==null)
				{
					try
					{
						socket = new DatagramSocket();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		else if(socket==null)
		{
			throw new RuntimeException("No creation of socket in killed state.");
		}
		
		return socket;
	}
	
	/**
	 *  Handle a received packet.
	 */
	protected void handleReceivedPacket(DatagramPacket pack)
	{
		byte[] data = new byte[pack.getLength()];
		System.arraycopy(pack.getData(), 0, data, 0, pack.getLength());
		Object obj = DiscoveryState.decodeObject(data, getModel().getClassLoader());
		AwarenessInfo info = obj instanceof AwarenessInfo? (AwarenessInfo)obj: null;
		
//		System.out.println("received: "+obj+" "+pack.getAddress()+" "+pack.getPort());
		
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
			
		knowns.addOrUpdateEntry(new DiscoveryEntry(info, state.getClockTime(), new Object[]{pack.getAddress(), pack.getPort()}, false));
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
	 *  Get the registry.
	 *  @return the registry.
	 */
	public boolean isRegistry()
	{
		return isRegistry(address, port);
	}
	
	/**
	 *  Get the registry.
	 *  @return the registry.
	 */
	public boolean isRegistry(InetAddress address, int port)
	{
		boolean ret = false;
		try
		{
			DatagramSocket s = getSocket();
			if(s!=null)
			{
	//			System.out.println("a: "+s.getLocalPort()+" "+port+" "+address+" "+SUtil.getInet4Address());
				ret = s.getLocalPort()== port && address.equals(SUtil.getInet4Address()); 
			}
		}
		catch(Exception e) 
		{
		}
		return ret;
//		return registry;
	}

	/**
	 *  Send to registry.
	 */
	protected void sendToRegistry(byte[] data)
	{
		try
		{
			getSocket().send(new DatagramPacket(data, data.length, address, port));
//			System.out.println("sent to registry");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Send info to all knowns.
	 *  @param data The data to be send.
	 */
	protected void sendToKnowns(byte[] data)
	{
		try
		{
			DiscoveryEntry[] rems = knowns.getEntries();
			for(int i=0; i<rems.length; i++)
			{
				Object[] tmp = (Object[])rems[i].getEntry();
//				System.out.println("to: "+tmp[0]+" "+tmp[1]);
				getSocket().send(new DatagramPacket(data, data.length, (InetAddress)tmp[0], ((Integer)tmp[1]).intValue()));
			}
//			System.out.println("sent to knwons: "+rems.length);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	protected AwarenessInfo createAwarenessInfo()
	{
		return new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, state.getDelay(), state.getIncludes(), state.getExcludes(), false);
	}
	
	/**
	 *  Handle sending.
	 */
	class RegistrySendHandler extends SendHandler
	{
		/**
		 *  Create a new lease time handling object.
		 */
		public RegistrySendHandler(DiscoveryState state)
		{
			super(state);
		}
		
		/**
		 *  Start sending awareness infos.
		 *  (Ends automatically when a new send behaviour is started).
		 */
		public void startSendBehavior()
		{
			if(state.isStarted())
			{
				final String sendid = SUtil.createUniqueId(state.getExternalAccess().getComponentIdentifier().getLocalName());
				this.sendid = sendid;	
				
				state.getExternalAccess().scheduleStep(new IComponentStep()
				{
					@XMLClassname("send")
					public Object execute(IInternalAccess ia)
					{
						if(!state.isKilled() && sendid.equals(getSendId()))
						{
//							System.out.println(System.currentTimeMillis()+" sending: "+getComponentIdentifier());
							send(createAwarenessInfo());
							
							// Additionally send knowns to knowns
							if(isRegistry())
							{
								DiscoveryEntry[] kns = knowns.getEntries();
								for(int i=0; i<kns.length; i++)
								{
									send(kns[i].getInfo());
								}
							}
							
							if(state.getDelay()>0)
								state.doWaitFor(state.getDelay(), this);
						}
						return null;
					}
				});
			}
		}
		
		/**
		 *  Method to send messages.
		 */
		public void send(AwarenessInfo info)
		{
			try
			{
				byte[] data = DiscoveryState.encodeObject(info, getModel().getClassLoader());
		
//				System.out.println("packet size: "+data.length);

				// Send always to registry.
				if(isRegistry())
				{
					sendToKnowns(data);
				}
				else
				{
					sendToRegistry(data);
				}
				
		//		System.out.println("sent: "+address);
		//		System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
			}
			catch(Exception e)
			{
				getLogger().warning("Could not send awareness message: "+e);
				e.printStackTrace();
			}	
		}
	}
}


