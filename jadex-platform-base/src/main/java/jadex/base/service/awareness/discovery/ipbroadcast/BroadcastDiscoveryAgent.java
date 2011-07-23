package jadex.base.service.awareness.discovery.ipbroadcast;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.DiscoveryEntry;
import jadex.base.service.awareness.discovery.DiscoveryState;
import jadex.base.service.awareness.discovery.IDiscoveryService;
import jadex.base.service.awareness.discovery.LeaseTimeHandler;
import jadex.base.service.awareness.discovery.MasterInfo;
import jadex.base.service.awareness.discovery.SendHandler;
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
	@Argument(name="port", clazz=int.class, defaultvalue="55670", description="The port used for finding other agents."),
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
public class BroadcastDiscoveryAgent extends MicroAgent implements IDiscoveryService
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
		
	/** The socket. */
	protected DatagramSocket socket;
		
	/** The root component id. */
	protected IComponentIdentifier root;
	
	/** Flag indicating that the agent has received its own discovery info. */
//	protected boolean received_self;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		this.state = new DiscoveryState(getExternalAccess());
		initArguments();
		
//		try
//		{
//			this.sendsocket = new DatagramSocket();
//			sendsocket.setBroadcast(true);
//					
////			this.reader	= JavaReader.getReader(new XMLReporter()
////			{
////				public void report(String message, String type, Object related, Location location) throws XMLStreamException
////				{
////					// Ignore XML exceptions.
//////					getLogger().warning(message);
////				}
////			});
//			
//			ret.setResult(null);
//		}
//		catch(IOException e)
//		{
//			ret.setException(new RuntimeException(e));
//		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Read arguments and set initial values. 
	 */
	protected void initArguments()
	{
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
		
		this.locals = new LeaseTimeHandler(state);
		this.remotes = new LeaseTimeHandler(state)
		{
			public void entryDeleted(DiscoveryEntry entry)
			{
				// If master is lost, try to become master
				if(entry.isMaster())
				{
//					System.out.println("Master deleted.");
					
					try
					{
						synchronized(BroadcastDiscoveryAgent.this)
						{
							socket.close();
							socket = null;
							getSocket();
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
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
				sender = new BroadcastSendHandler(state);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Send also when receiving does not work?
				state.setStarted(true);
				sender = new BroadcastSendHandler(state);
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
		synchronized(BroadcastDiscoveryAgent.this)
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
	 *  Send/forward to discover.
	 *  @param data The data to be send.
	 */
	protected void sendToDiscover(byte[] data)
	{
		try
		{
			// Global broadcast address 255.255.255.255 does not work in windows xp/7 :-(
			// http://serverfault.com/questions/72112/how-to-alter-the-global-broadcast-address-255-255-255-255-behavior-on-windows
			// Directed broadcast address = !netmask | IP
	//		InetAddress address = InetAddress.getByAddress(new byte[]{(byte)255, (byte)255, (byte)255, (byte)255,});
			
			InetAddress address = SUtil.getInet4Address();
			short prefixlen = SUtil.getNetworkPrefixLength(address);
			if(prefixlen==-1) // Guess C class if nothing can be determined.
				prefixlen = 24;
			
			getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, (short)24), port));
			getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, (short)16), port));
			getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, (short)8), port));
			
			if(prefixlen!=-1 && prefixlen!=24 && prefixlen!=16 && prefixlen!=8)
				getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, prefixlen), port));
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 *  Create broadcast address according to prefix length.
	 */
	protected InetAddress createBroadcastAddress(InetAddress address, short prefixlen)
	{
		try
		{
//			InetAddress iadr = SUtil.getInet4Address();
			byte[] byinet = address.getAddress();
			int hostbits = 32-prefixlen;
			int mask = (int)Math.pow(2, hostbits)-1;
			int iinet = SUtil.bytesToInt(byinet);
			int badr = iinet | mask;
			return InetAddress.getByAddress(SUtil.intToBytes(badr));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Send/forward to locals.
	 *  @param data The data to be send.
	 */
	protected void sendToLocals(byte[] data)
	{
		DiscoveryEntry[] locals = this.locals.getEntries();
		for(int i=0; i<locals.length; i++)
		{
			sendToLocal(data, ((Integer)locals[i].getEntry()).intValue());
		}
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
			InetAddress address = SUtil.getInet4Address();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			getSocket().send(packet);
		}
		catch(IOException e)
		{
			e.printStackTrace();
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
	//								System.out.println("received: "+getComponentIdentifier());
								}
								catch(Exception e)
								{
									// Can happen if is slave and master goes down.
									// In that case it tries to find new master.
	//								getLogger().warning("Receiving awareness info error: "+e);
									ret.setExceptionIfUndone(e);
								}
							}
						}
						catch(Exception e) 
						{
							ret.setExceptionIfUndone(e);
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
				try
				{
					socket = new DatagramSocket(port);
					socket.setBroadcast(true);
//					System.out.println("local master at: "+SDiscovery.getInet4Address()+" "+port);
				}
				catch(Exception e)
				{
					try
					{
						// In case the receiversocket cannot be opened
						// open another local socket at an arbitrary port
						// and send this port to the master.
						socket = new DatagramSocket();
						socket.setBroadcast(true);
						InetAddress address = SUtil.getInet4Address();
						AwarenessInfo info = createAwarenessInfo();
						SlaveInfo si = new SlaveInfo(info);
						byte[] data = DiscoveryState.encodeObject(si, getModel().getClassLoader());
						DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
						socket.send(packet);
//						System.out.println("local slave at: "+SDiscovery.getInet4Address()+" "+receivesocket.getLocalPort());
						
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
		AwarenessInfo info = obj instanceof AwarenessInfo? (AwarenessInfo)obj:
			obj instanceof SlaveInfo? ((SlaveInfo)obj).getAwarenessInfo(): 
			obj instanceof MasterInfo? ((MasterInfo)obj).getAwarenessInfo(): null;
		
//		System.out.println("received: "+obj+" "+pack.getAddress());
		
		if(info!=null && info.getSender()!=null)
		{
			sendToLocals(data);
			
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
			
		if(obj instanceof SlaveInfo)
		{
			// Received slaveinfo -> save slave, reply with masterinfo.
			SlaveInfo si = (SlaveInfo)obj;
			locals.addOrUpdateEntry(new DiscoveryEntry(si.getAwarenessInfo(), 
				state.getClockTime(), new Integer(pack.getPort()), false));
			AwarenessInfo myinfo = createAwarenessInfo();
			MasterInfo mi = new MasterInfo(myinfo);
			byte[] mydata = DiscoveryState.encodeObject(mi, getModel().getClassLoader());
			sendToLocal(mydata, pack.getPort());
//			System.out.println("received slave info: "+getComponentIdentifier().getLocalName()+" "+si.getAwarenessInfo().getSender());
		}
		else if(obj instanceof MasterInfo)
		{
			// Received masterinfo -> save master
			MasterInfo mi = (MasterInfo)obj;
			remotes.addOrUpdateEntry(new DiscoveryEntry(mi.getAwarenessInfo(), 
				state.getClockTime(), pack.getAddress(), true));
//			System.out.println("received master info: "+getComponentIdentifier().getLocalName()+" "+mi.getAwarenessInfo().getSender());
		}
		else if(obj instanceof AwarenessInfo)
		{
			// Received awareness info -> save known
			remotes.addOrUpdateEntry(new DiscoveryEntry(info, state.getClockTime(), pack.getAddress(), false));
//			System.out.println("received awa info: "+getComponentIdentifier().getLocalName()+" "+info.getSender());
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
	class BroadcastSendHandler extends SendHandler
	{
		/**
		 *  Create a new lease time handling object.
		 */
		public BroadcastSendHandler(DiscoveryState state)
		{
			super(state);
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
				
				// Broadcast info to lan.
				sendToDiscover(data);
				
				// Does not need to send to known components
				// as broadcast reaches all.
				
				// Send to all locals a refresh awareness
				sendToLocals(data);
				
				// Test if master is available.
//				getReceiveSocket();
		
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
	
	
//	/**
//	 *  This method version ensures that new master is selected
//   *  automatically. Problem other slaves do not recognize new master.
//	 *  Get or create a receiver socket.
//	 *  
//	 *  Note, this method has to be synchronized.
//	 *  Is called from receiver as well as component thread.
//	 */
//	protected synchronized DatagramSocket getReceiveSocket()
//	{
//		DatagramSocket ret = receivesocket;
//		
//		if(!state.isKilled())
//		{
//			if(receivesocket==null)
//			{
//				try
//				{
//					receivesocket = new DatagramSocket(port);
//					ret = receivesocket;
//					
//					// Was slave and is now master -> reset slave socket
//					if(slavereceivesocket!=null)
//					{
//						try
//						{
//							slavereceivesocket.close();
//						}
//						catch(Exception e)
//						{
//						}
//						slavereceivesocket = null;
//					}
//					
//					System.out.println("local master at: "+SDiscovery.getInet4Address()+" "+port);
//				}
//				catch(Exception e)
//				{
//					if(slavereceivesocket==null)
//					{
//						try
//						{
//							// In case the receiversocket cannot be opened
//							// open another local socket at an arbitrary port
//							// and send this port to the master.
//							slavereceivesocket = new DatagramSocket();
//							InetAddress address = SDiscovery.getInet4Address();
//							AwarenessInfo info = new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, state.getDelay(), state.getIncludes(), state.getExcludes());
//							SlaveInfo si = new SlaveInfo(info);
//							byte[] data = SDiscovery.encodeObject(si, getModel().getClassLoader());
//							DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//							slavereceivesocket.send(packet);
//		
//							System.out.println("local slave at: "+SDiscovery.getInet4Address()+" "+slavereceivesocket.getLocalPort());
//							
//	//						getLogger().warning("Running in local mode: "+e);
//						}
//						catch(Exception e2)
//						{
//							throw new RuntimeException(e2);
//						}
//					}
//					ret = slavereceivesocket;
//				}
//			}
//		}
//		
//		return ret;
//	}
}


