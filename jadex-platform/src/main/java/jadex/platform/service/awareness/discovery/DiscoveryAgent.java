package jadex.platform.service.awareness.discovery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.bridge.service.types.message.IBinaryCodec;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.ISerializer;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.binaryserializer.SBinarySerializer;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.message.MapSendTask;

/**
 *  Base class for different kinds of discovery agents.
 */
@Agent
@Arguments({
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="fast", clazz=boolean.class, defaultvalue="false", description="Flag for enabling fast startup awareness (pingpong send behavior)."),
	@Argument(name="includes", clazz=String[].class, description="A list of platforms/IPs/hostnames to include. Matches start of platform/IP/hostname."),
	@Argument(name="excludes", clazz=String[].class, description="A list of platforms/IPs/hostnames to exclude. Matches start of platform/IP/hostname.")
})
/*@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000")),
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000")),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"))
})*/
@ProvidedServices(
	@ProvidedService(type=IDiscoveryService.class)
)
@RequiredServices(
{
	@RequiredService(name="ms", type=IMessageService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="tas", type=ITransportAddressService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="threadpool", type=IDaemonThreadPoolService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="management", type=IAwarenessManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public abstract class DiscoveryAgent	implements IDiscoveryService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The send (remotes) delay. */
	@AgentArgument
	protected long delay;
	
	/** Flag for enabling fast startup awareness (pingpong send behavior). */
	@AgentArgument
	protected boolean fast;
	
	/** The includes list. */
	@AgentArgument
	protected String[] includes;
	
	/** The excludes list. */
	@AgentArgument
	protected String[] excludes;

	/** Flag indicating that the agent is started and the send behavior may be activated. */
	protected boolean started;

	/** Flag indicating agent killed. */
	protected boolean killed;

	/** The timer. */
	protected Timer	timer;

	/** The root component id. */
	protected IComponentIdentifier root;
	
	
	/** The send handler. */
	protected SendHandler sender;
	
	/** The receive handler. */
	protected ReceiveHandler receiver;
	
	/** Flag indicating that the agent has received its own discovery info. */
	protected boolean received_self;
	
//	/** The classloader. */
//	protected ClassLoader classloader;
	
	/** The map of all serializers. */
	protected Map<Byte, ISerializer> allserializers;
	
	/** The default codecs. */
	protected IBinaryCodec[] defaultcodecs;
	
	/** The map of all codecs. */
	protected Map<Byte, IBinaryCodec> allcodecs;
	
	//-------- methods --------
	
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();
	
//		System.out.println("fast: "+fast);
		
//		System.out.println(agent.getComponentIdentifier()+" includes: "+SUtil.arrayToString(includes));
//		System.out.println(agent.getComponentIdentifier()+" excludes: "+SUtil.arrayToString(excludes));
		
//		System.out.println(getMicroAgent().getChildrenIdentifiers()+" delay: "+delay);
		
		final IMessageService msgser = SServiceProvider.getLocalService(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		msgser.getDefaultCodecs().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IBinaryCodec[], Void>(ret)
		{
			public void customResultAvailable(IBinaryCodec[] result)
			{
				defaultcodecs = result;
				msgser.getAllSerializersAndCodecs().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Tuple2<Map<Byte, ISerializer>,Map<Byte, IBinaryCodec>>, Void>(ret)
				{
					public void customResultAvailable(Tuple2<Map<Byte, ISerializer>,Map<Byte, IBinaryCodec>> result)
					{
						allserializers = result.getFirstEntity();
						allcodecs = result.getSecondEntity();
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
		// Wait before starting send behavior to not miss fast awareness pingpong replies,
		// because receiver thread is not yet running. (hack???)
		
		this.sender = createSendHandler();
		this.receiver = createReceiveHandler();
		if(receiver!=null)
		{
			receiver.startReceiving().addResultListener(getMicroAgent().getComponentFeature(IExecutionFeature.class)
				.createResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					setStarted(true);
					if(sender!=null)
					{
						sender.startSendBehavior();
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Send also when receiving does not work?
					setStarted(true);
					if(sender!=null)
					{
						sender.startSendBehavior();
					}
				}
			}));
		}
		else
		{
			setStarted(true);			
			if(sender!=null)
			{
				sender.startSendBehavior();
			}
		}
	}
	
	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	@AgentKilled
	public IFuture<Void> agentKilled()
	{
		final Future<Void> ret = new Future<Void>();
		setKilled(true);
		
		if(timer!=null)
		{
//			System.out.println("cancel timer: "+this);
			timer.cancel();
			timer	= null;
		}
		
		if(sender!=null)
		{
			createAwarenessInfo(AwarenessInfo.STATE_OFFLINE, createMasterId())
				.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(ret)
			{
				public void customResultAvailable(AwarenessInfo info)
				{
					sender.send(info);
					terminateNetworkRessource();
					ret.setResult(null);
				}
			}));
		}
		else
		{
			ret.setResult(null);
		}
		
//		System.out.println("killed set to true: "+getComponentIdentifier());
		
		return ret;
	}
	
	/**
	 *  Create the master id.
	 */
	protected String createMasterId()
	{
		return null;
	}
	
	/**
	 *  Create the send handler.
	 */
	public abstract SendHandler createSendHandler();
	
	/**
	 *  Create the receive handler.
	 */
	public abstract ReceiveHandler createReceiveHandler();
	
	/**
	 *  Get the includes.
	 *  @return the includes.
	 */
	public String[] getIncludes()
	{
		return includes;
	}

	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes)
	{
		this.includes = includes.clone();
	}
	
	/**
	 *  Get the excludes.
	 *  @return the excludes.
	 */
	public String[] getExcludes()
	{
		return excludes;
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
		this.excludes = excludes.clone();
	}

	/**
	 *  Get the started.
	 *  @return the started.
	 */
	public boolean isStarted()
	{
		return started;
	}

	/**
	 *  Set the started.
	 *  @param started The started to set.
	 */
	public void setStarted(boolean started)
	{
		this.started = started;
	}

	/**
	 *  Get the killed.
	 *  @return the killed.
	 */
	public boolean isKilled()
	{
		return killed;
	}

	/**
	 *  Set the killed.
	 *  @param killed The killed to set.
	 */
	public void setKilled(boolean killed)
	{
		this.killed = killed;
	}

	/**
	 *  Get the root.
	 *  @return the root.
	 */
	public IComponentIdentifier getRoot()
	{
		if(root==null)
			this.root = agent.getComponentIdentifier().getRoot();
		return root;
	}
	
	/**
	 *  Set the root.
	 *  @param root The root to set.
	 */
	public void setRoot(IComponentIdentifier root)
	{
		this.root = root;
	}
	
//	/**
//	 *  Get the access.
//	 *  @return the access.
//	 */
//	public IExternalAccess getExternalAccess()
//	{
//		return access;
//	}
	
	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public long getDelay()
	{
		return delay;
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
		if(getDelay()!=delay)
		{
			this.delay = delay;
			if(sender!=null)
			{
				sender.startSendBehavior();
			}
		}
	}

	/**
	 *  Set the fast startup awareness flag
	 */
	public void setFast(boolean fast)
	{
		this.fast = fast;
	}
	
	/**
	 *  Get the fast startup awareness flag.
	 *  @return The fast flag.
	 */
	public boolean isFast()
	{
		return this.fast;
	}
	
	/**
	 *  Republish the awareness info.
	 *  Called when some important property has changed, e.g. platform addresses.
	 */
	public void republish()
	{
		// Empty default implementation.
	}
	
	/**
	 *  Get the current time.
	 */
	public long getClockTime()
	{
//		return clock.getTime();
		return System.currentTimeMillis();
	}
	
	/**
	 *  Overriden wait for to not use platform clock.
	 */
	public void	doWaitFor(long delay, final IComponentStep<?> step)
	{
//		waitFor(delay, step);
		
		if(timer==null)
		{
//			System.out.println("new timer: "+this);
			timer	= new Timer(true);
		}
		
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				try
				{
					agent.getComponentFeature(IExecutionFeature.class).scheduleStep(step);
				}
				catch(ComponentTerminatedException e)
				{
					// ignore
				}
			}
		}, delay);
	}
	
	/**
	 *  Encode an object.
	 *  @param object The object.
	 *  @return The byte array.
	 */
	public static byte[] encodeObject(Object object, ClassLoader classloader)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SBinarySerializer.writeObjectToStream(baos, object, classloader);
		return baos.toByteArray();
		// TODO: Hack? The encoding context probably needs to be target-based
//		return MapSendTask.encodeMessage(object, null, null, codecs, classloader);
//		return GZIPCodec.encodeBytes(JavaWriter.objectToByteArray(object, 
//			classloader), classloader);
	}
	
	/**
	 *  Decode an object.
	 *  @param data The byte array.
	 *  @return The object.
	 */
	public static Object decodeObject(byte[] data, ClassLoader classloader)
	{
		return SBinarySerializer.readObjectFromStream(new ByteArrayInputStream(data), null, null, classloader, null, null);
//		System.out.println("size: "+data.length);
//		return MapSendTask.decodeMessage(data, null, serializers, codecs, classloader, IErrorReporter.IGNORE);
//		return JavaReader.objectFromByteArray(GZIPCodec.decodeBytes(data, 
//			classloader), classloader);
//		return Reader.objectFromByteArray(reader, GZIPCodec.decodeBytes(data, 
//			classloader), classloader);
	}
	
//	/**
//	 *  Decode a datagram packet.
//	 *  @param sent The byte array.
//	 *  @return The object.
//	 */
//	public static Object decodePacket(DatagramPacket pack, ClassLoader classloader)
//	{
//		byte[] data = new byte[pack.getLength()];
//		System.arraycopy(pack.getData(), 0, data, 0, pack.getLength());
//		return decodeObject(data, classloader);
//	}
	
	/**
	 *  Get the allcodecs.
	 *  @return the allcodecs.
	 */
	public Map<Byte, ISerializer> getAllSerializers()
	{
		return allserializers;
	}
	
	/**
	 *  Get the allcodecs.
	 *  @return the allcodecs.
	 */
	public Map<Byte, IBinaryCodec> getAllCodecs()
	{
		return allcodecs;
	}
	
	/**
	 *  Get the defaultcodecs.
	 *  @return the defaultcodecs.
	 */
	public IBinaryCodec[] getDefaultCodecs()
	{
		return defaultcodecs;
	}

	/**
	 *  Create awareness info of myself.
	 */
	public IFuture<AwarenessInfo> createAwarenessInfo()
	{
		final Future<AwarenessInfo> ret = new Future<AwarenessInfo>();
		final String awa = SReflect.getInnerClassName(this.getClass());
//		System.out.println("awa: "+awa);
		IFuture<ITransportAddressService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("tas");
		fut.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<ITransportAddressService, AwarenessInfo>(ret)
		{
			public void customResultAvailable(ITransportAddressService tas)
			{
				tas.getTransportComponentIdentifier(getRoot()).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
					new ExceptionDelegationResultListener<ITransportComponentIdentifier, AwarenessInfo>(ret)
				{
					public void customResultAvailable(ITransportComponentIdentifier root)
					{
						AwarenessInfo info = new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, getDelay(), getIncludes(), 
							getExcludes(), null, awa);
						ret.setResult(info);
					}
				}));
			}
		}));
		return ret;
	}
	
	/**
	 *  Create awareness info of myself.
	 */
	public IFuture<AwarenessInfo> createAwarenessInfo(final String state, final String masterid)
	{
		final Future<AwarenessInfo> ret = new Future<AwarenessInfo>();
		final String awa = SReflect.getInnerClassName(this.getClass());
		IFuture<ITransportAddressService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("tas");
		fut.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<ITransportAddressService, AwarenessInfo>(ret)
		{
			public void customResultAvailable(ITransportAddressService tas)
			{
				tas.getTransportComponentIdentifier(getRoot()).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
					new ExceptionDelegationResultListener<ITransportComponentIdentifier, AwarenessInfo>(ret)
				{
					public void customResultAvailable(ITransportComponentIdentifier root)
					{
						AwarenessInfo info = new AwarenessInfo(root, state, getDelay(), getIncludes(), 
							getExcludes(), masterid, awa);
						ret.setResult(info);
					}
				}));
			}
		}));
		return ret;
	}

	/**
	 *  Get the agent.
	 *  @return the agent.
	 */
	public IInternalAccess getMicroAgent()
	{
		return agent;
	}
	
	/**
	 *  Get the sender.
	 *  @return the sender.
	 */
	public SendHandler getSender()
	{
		return sender;
	}

	/**
	 *  Get the receiver.
	 *  @return the receiver.
	 */
	public ReceiveHandler getReceiver()
	{
		return receiver;
	}

//	/**
//	 *  Get the classloader.
//	 *  @return the classloader.
//	 */
//	public ClassLoader getMyClassLoader()
//	{
//		return classloader;
//	}

	/**
	 *  (Re)init sending/receiving ressource.
	 */
	protected abstract void initNetworkRessource();
	
	/**
	 *  Terminate sending/receiving ressource.
	 */
	protected abstract void terminateNetworkRessource();
}
