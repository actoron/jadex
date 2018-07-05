package jadex.platform.service.transport;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.component.impl.RemoteExecutionComponentFeature;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.memstat.IMemstatService;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportInfoService;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.bridge.service.types.transport.PlatformData;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * Base class for transports.
 * Thread-safe implementation for using transports as raw service.
 * 
 * @param <Con> A custom object type to hold connection information as required by the concrete transport.
 */
@Agent
@ProvidedServices({
	@ProvidedService(scope=Binding.SCOPE_PLATFORM, type=ITransportService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(scope=Binding.SCOPE_PLATFORM, type=ITransportInfoService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(scope=Binding.SCOPE_PLATFORM, type=IMemstatService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW))
})
public abstract class AbstractTransportAgent<Con> implements ITransportService, ITransportInfoService, IMemstatService,	IInternalService, ITransportHandler<Con>
{
	// -------- arguments --------

	/** The default priority, when choosing a transport to communicate with specific platform. */
	@AgentArgument
	protected int	priority	= 1000;

	/** The port, the transport should listen to (&lt;0: don't listen, 0: choose random port, >0: use given port). */
	@AgentArgument
	protected int	port	= 0;
	
	/** Maximum size a message is allowed to have (including header). */
	@AgentArgument
	protected int maxmsgsize = 100*1024*1024;

	// -------- internal attributes --------

	/** The agent. */
	@Agent
	protected IInternalAccess	agent;

	/** The encoder/decoder. */
	protected ISerializationServices	codec;

	/** The transport implementaion. */
	protected ITransport<Con>	impl;

	/**
	 * The connections currently in use (target platform -> virtual connection).
	 * Used e.g. for sendMessage().
	 */
	protected Map<IComponentIdentifier, VirtualConnection>	virtuals;

	/**
	 * The connections currently in handshake or in use (impl connection ->
	 * connection candidate object). Used also for messageReceived().
	 */
	protected Map<Con, ConnectionCandidate>	candidates;
	
	/** The security service (cached for speed). */
	protected ISecurityService	secser;
	
	/** The cms (cached for speed). */
	protected IComponentManagementService	cms;
	
	/** Listeners from transport info service. */
	protected Collection<SubscriptionIntermediateFuture<PlatformData>>	infosubscribers;
	

	// -------- abstract methods to be provided by concrete transport --------

	/**
	 * Get the transport implementation
	 */
	public abstract ITransport<Con> createTransportImpl();

	// -------- ITransportHandler, i.e.methods to be called by concrete transport --------

	/**
	 * Get the internal access.
	 */
	public IInternalAccess getAccess()
	{
		return agent;
	}

	/**
	 * Deliver a received message.
	 * 
	 * @param con The connection.
	 * @param header The message header.
	 * @param body The message body.
	 */
	public void messageReceived(final Con con, final byte[] header, final byte[] body)
	{
		ConnectionCandidate cand = getConnectionCandidate(con);
		
		// Race condition between con result future in handleConnect and received CID from server.
		if(cand==null)
		{
			// thread safe create -> only create if not exists and if client con (shouldn't happen for server con).
			cand	= createConnectionCandidate(con, true);
		}
		
		final IComponentIdentifier source = cand.getTarget();

		// First msg is CID from handshake.
		if(source == null)
		{
			assert header.length == 0;
			String name = new String(body, SUtil.UTF8);
			cand.setTarget(new BasicComponentIdentifier(name));
		}
		else
		{
			if (IComponentDescription.STATE_ACTIVE.equals(agent.getDescription().getState()))
				deliverRemoteMessage(agent, secser, cms, codec, source, header, body);
		}
	}

	/**
	 * Called when a server connection is established.
	 * 
	 * @param con The connection.
	 */
	public void connectionEstablished(final Con con)
	{
		createConnectionCandidate(con, false);
	}

	/**
	 * Called when a connection is closed.
	 * 
	 * @param con The connection.
	 * @param e The exception, if any.
	 */
	public void connectionClosed(Con con, Exception e)
	{
//		System.out.println("Close connection called: " + System.identityHashCode(con) + " " + con + " " + e);
		ConnectionCandidate cand = getConnectionCandidate(con);
		
		// TODO: Check if cand can/may actually _be_ null
		//		 at this point (multiple invocations from
		//		 simultaneous read/write errors?)
//		assert cand != null : e;		
		if (cand != null)
			removeConnectionCandidate(cand, e);
	}

	// -------- life cycle --------

	/**
	 * Agent initialization.
	 */
	@AgentCreated
	protected IFuture<Void>	init()
	{
		this.codec = MessageComponentFeature.getSerializationServices(agent.getIdentifier().getRoot());
		this.secser	= ((AbstractComponentFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(ISecurityService.class);
		this.cms	= ((AbstractComponentFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(IComponentManagementService.class);
		this.impl = createTransportImpl();
		impl.init(this);

		// Set up server, if port given.
		// If port==0 -> any free port
		if(port >= 0)
		{
			final Future<Void>	ret	= new Future<Void>();
			impl.openPort(port)
				.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
			{
				@Override
				public void customResultAvailable(Integer port)
				{
					try
					{
						// Announce connection addresses.
						InetAddress[] addresses = SUtil.getNetworkAddresses();
//						String[] saddresses = new String[addresses.length];
						IComponentIdentifier platformid = agent.getIdentifier().getRoot();
						List<TransportAddress> saddresses = new ArrayList<TransportAddress>();
						for(int i = 0; i < addresses.length; i++)
						{
							String addrstr = null;
							if(addresses[i] instanceof Inet6Address)
							{
//								saddresses[i] = "[" + addresses[i].getHostAddress() + "]:" + port;
								addrstr = "[" + addresses[i].getHostAddress() + "]:" + port;
							}
							else // if (address instanceof Inet4Address)
							{
//								saddresses[i] = addresses[i].getHostAddress() + ":" + port;
								addrstr = addresses[i].getHostAddress() + ":" + port;
							}
							saddresses.add(new TransportAddress(platformid, impl.getProtocolName(), addrstr));
						}
						
						agent.getLogger().info("Platform "+agent.getIdentifier().getPlatformName()+" listening to port " + port + " for " + impl.getProtocolName() + " transport.");

//						TransportAddressBook tab = TransportAddressBook.getAddressBook(agent);
//						tab.addPlatformAddresses(agent.getComponentIdentifier(), impl.getProtocolName(), saddresses);
						ITransportAddressService tas = ((AbstractComponentFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(ITransportAddressService.class);
						
//						System.out.println("Transport addresses: "+agent+", "+saddresses);
						tas.addLocalAddresses(saddresses).addResultListener(new DelegationResultListener<Void>(ret));
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			}));
			
			return ret;
		}
		else
		{
			return IFuture.DONE;
		}
	}

	/**
	 * Agent shutdown.
	 */
	@AgentKilled
	protected void shutdown()
	{
		impl.shutdown();
	}

	// -------- ITransportService interface --------

	/**
	 * Checks if the transport is ready.
	 * 
	 * @param header Message header.
	 * @return Transport priority, when ready
	 */
	public IFuture<Integer> isReady(final IMsgHeader header)
	{
//		agent.getLogger().severe("isReady");
		VirtualConnection	handler;
		final IComponentIdentifier	target	= getTarget(header);
		
		boolean	create	= false;
		synchronized(this)
		{
			handler = getVirtualConnection(target);
			if(handler==null)
			{
				handler	= createVirtualConnection(target);
				create	= true;
			}
		}
		
		if(create)
		{
			final VirtualConnection	fhandler	= handler;
//			agent.getLogger().severe("isReady no handler");
			getAddresses(header).addResultListener(new IResultListener<Collection<String>>()
			{
				public void resultAvailable(Collection<String> addresses)
				{
//					agent.getLogger().severe("isReady got addresses: "+addresses);
					if(addresses!=null && !addresses.isEmpty())
						createConnections(fhandler, target, addresses.toArray(new String[addresses.size()]));
					
					else
						fhandler.fail(new RuntimeException("No addresses found for " + impl.getProtocolName() + ": " + header));
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					fhandler.fail(exception);
				}
			});
		}

		return handler.isReady();
	}
	
	/**
	 * Send a message. Fail fast implementation. Retry should be handled in
	 * message feature.
	 * 
	 * @param header Message header.
	 * @param body Message body.
	 * @return Done, when sent, failure otherwise.
	 */
	public IFuture<Void> sendMessage(final IMsgHeader header, final byte[] body)
	{
		VirtualConnection handler = getVirtualConnection(getTarget(header));
		if(handler == null || handler.getConnection() == null)
		{
			return new Future<Void>(new RuntimeException("No connection to " + getTarget(header)));
		}
		else
		{
			final Future<Void> ret = new Future<Void>();
			byte[] bheader = codec.encode(header, agent, header);

			secser.encryptAndSign(header, bheader).addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
			{
				@Override
				public void customResultAvailable(final byte[] ebheader) throws Exception
				{
					VirtualConnection handler = getVirtualConnection(getTarget(header));
					if(handler==null ||  handler.getConnection()==null)
					{
						ret.setException(new RuntimeException("No connection to " + getTarget(header)));
					}
					else
					{
						trySend(handler.getConnection(), handler, ebheader);
					}
				}

				protected void trySend(final Con con, final VirtualConnection handler, final byte[] ebheader)
				{
					// Try with existing connection. When failed -> check if new
					// connection available,
					// e.g. if current connection terminated due to multiple
					// open connections.
					impl.sendMessage(con, ebheader, body).addResultListener(new DelegationResultListener<Void>(ret)
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							Con con2	= handler.getConnection();
							if(con2!=null && con2!=con)
							{
								trySend(con2, handler, ebheader);
							}
							else
							{
								super.exceptionOccurred(exception);
							}
						}
					});
				}
			});

			return ret;
		}
	}
	
	//-------- ITransportInfoService interface --------
	
	/**
	 *  Get events about established connections.
	 *  @return Events for connections specified by
	 *  	1: platform id,
	 *  	2: protocol name,
	 *  	3: ready flag (false=connecting, true=connected, null=disconnected).
	 */
	public ISubscriptionIntermediateFuture<PlatformData>	subscribeToConnections()
	{
		final SubscriptionIntermediateFuture<PlatformData>	ret	= new SubscriptionIntermediateFuture<PlatformData>(null, true);
		SFuture.avoidCallTimeouts(ret, agent);
		ret.setTerminationCommand(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				synchronized(AbstractTransportAgent.this)
				{
					infosubscribers.remove(ret);
				}
			}
		});
	
		synchronized(this)
		{
			if(infosubscribers==null)
			{
				infosubscribers	= new ArrayList<SubscriptionIntermediateFuture<PlatformData>>();
			}
			infosubscribers.add(ret);
			
			// Add initial data
			if(virtuals!=null)
			{
				for(Map.Entry<IComponentIdentifier, VirtualConnection> entry: virtuals.entrySet())
				{ 
					ret.addIntermediateResult(entry.getValue().getPlatformdata(entry.getKey()));
				}
			}
		}
	
		return ret;
	}
	
	/**
	 *  Get the established connections.
	 *  @return A list of connections specified by
	 *  	1: platform id,
	 *  	2: protocol name,
	 *  	3: ready flag (false=connecting, true=connected).
	 */
	public IIntermediateFuture<PlatformData>	getConnections()
	{
		final IntermediateFuture<PlatformData>	ret	= new IntermediateFuture<PlatformData>();
	
		synchronized(this)
		{
			// Add initial data
			if(virtuals!=null)
			{
				for(Map.Entry<IComponentIdentifier, VirtualConnection> entry: virtuals.entrySet())
				{ 
					ret.addIntermediateResult(entry.getValue().getPlatformdata(entry.getKey()));
				}
			}
		}
		
		ret.setFinished();
	
		return ret;
	}
	
	/**
	 *  Get info about stored data like connections and listeners.
	 */
	// For detecting/debugging memory leaks
	public IFuture<Map<String, Object>>	getMemInfo()
	{
		synchronized(this)
		{
			Map<String, Object>	ret	= new LinkedHashMap<String, Object>();
			ret.put("transport", impl.getProtocolName());
			ret.put("subscribercnt", infosubscribers!=null ? infosubscribers.size() : 0);
			ret.put("cons", candidates!=null ? candidates.values() : null);
			ret.put("virtuals", virtuals!=null ? virtuals.keySet() : null);
			return new Future<Map<String,Object>>(ret);
		}
	}
	
	// -------- helper methods --------

	/**
	 * Convenience method.
	 */
	protected Logger getLogger()
	{
		return agent.getLogger();
	}

	/**
	 * Create a connection to a given platform. Tries all available addresses in
	 * parallel. Fails when no connection can be established.
	 */
	protected void	createConnections(final VirtualConnection handler, final IComponentIdentifier target, final String[] addresses)
	{
		// Counter for failed connections to know when all are failed.
		final int[] failed = new int[]{0};

		for(final String address : addresses)
		{
			agent.getLogger().info("Attempting connection to " + target + " using address: " + address);
			IFuture<Con> fcon = impl.createConnection(address, target);
			fcon.addResultListener(new IResultListener<Con>()
			{
				@Override
				public void resultAvailable(Con con)
				{
					createConnectionCandidate(con, true);
				}

				@Override
				public void exceptionOccurred(Exception exception)
				{
					agent.getLogger().info("Failed connection to " + target + " using address: " + address + ": " + exception);

					// All tries failed?
					int cnt;
					synchronized(this)
					{
						cnt	= ++failed[0];
					}
					if(cnt == addresses.length)
					{
						handler.fail(new RuntimeException("No connection to any address possible for " + impl.getProtocolName() + ": " + target + ", " + Arrays.toString(addresses)));
						removeVirtualConnection(target, handler);
					}
				}
			});
		}
	}

	// -------- connection management methods --------
	
	/**
	 * Get the connection handler, if any.
	 */
	protected ConnectionCandidate getConnectionCandidate(Con con)
	{
		synchronized(this)
		{
			return candidates != null ? candidates.get(con) : null;
		}
	}
	
	/**
	 * Create a connection candidate.
	 */
	protected ConnectionCandidate	createConnectionCandidate(final Con con, final boolean clientcon)
	{
		ConnectionCandidate cand;
		boolean	created	= false;
		synchronized(this)
		{
			if(candidates==null)
			{
				candidates = new HashMap<Con, ConnectionCandidate>();
			}
			
			if(candidates.containsKey(con))
			{
				// eager creation hack only for client con.
				assert clientcon;
				cand	= candidates.get(con);
			}
			else
			{
				cand = new ConnectionCandidate(con, clientcon);
				candidates.put(con, cand);
				created = true;
			}
		}

		// Start handshake by sending id.
		if (created)
		{
			agent.getLogger().info((clientcon ? "Connected to " : "Accepted connection ") + con + ". Starting handshake...");
			impl.sendMessage(con, new byte[0], agent.getIdentifier().getPlatformName().getBytes(SUtil.UTF8));
		}
		
		return cand;
	}
	
	/**
	 * Remove a connection candidate.
	 */
	protected void removeConnectionCandidate(ConnectionCandidate cand, Exception e)
	{
		synchronized(this)
		{
			ConnectionCandidate prev = candidates.remove(cand.getConnection());
			assert prev==cand;
		}

		if(cand.getTarget()!=null)
		{
//			handler.getAccess().getLogger().info("Error on connection: "+((SocketChannel)sc).socket().getRemoteSocketAddress()+", "+e);
			agent.getLogger().info("Closed connection " + cand.getConnection() + " to: "+cand.getTarget()+(e!=null? ", "+e:""));
			VirtualConnection vircon = getVirtualConnection(cand.getTarget());
			assert vircon!=null;
			vircon.removeConnection(cand);
		}
		else
		{
			agent.getLogger().info("Closed connection: " + cand.getConnection()+(e!=null? ", "+e:""));
		}
	}
	
	/**
	 * Get the connection handler, if any.
	 */
	protected VirtualConnection getVirtualConnection(IComponentIdentifier target)
	{
		synchronized(this)
		{
			return virtuals!=null ? virtuals.get(target) : null;
		}
	}

	/**
	 * Create a virtual connection.
	 */
	protected VirtualConnection createVirtualConnection(IComponentIdentifier target)
	{
		VirtualConnection vircon = new VirtualConnection();
		SubscriptionIntermediateFuture<PlatformData>[]	notify;
		synchronized(this)
		{
			if(virtuals==null)
			{
				virtuals = new HashMap<IComponentIdentifier, VirtualConnection>();
			}
			VirtualConnection prev = virtuals.put(target, vircon);
			assert prev == null;
			
			@SuppressWarnings("unchecked")
			SubscriptionIntermediateFuture<PlatformData>[]	tmp
				= infosubscribers!=null ? infosubscribers.toArray(new SubscriptionIntermediateFuture[infosubscribers.size()]) : null;
			notify	= tmp;
		}
		
		if(notify!=null)
		{
			// Newly created connection -> ready=false.
			PlatformData	info	= vircon.getPlatformdata(target);
			for(SubscriptionIntermediateFuture<PlatformData> fut: notify)
			{
				fut.addIntermediateResult(info);
			}
		}
		
		return vircon;
	}
	
//	/**
//	 *  Get or create a virtual connection.
//	 */
//	protected VirtualConnection getOrCreateVirtualConnection(IComponentIdentifier target)
//	{
//		synchronized(this)
//		{
//			VirtualConnection	ret	= getVirtualConnection(target);
//			if(ret==null)
//			{
//				ret	= createVirtualConnection(target);
//			}
//			return ret;
//		}
//	}
	
	/**
	 *  Remove a virtual connection if it is still the current connection for the target.
	 */
	protected void	removeVirtualConnection(IComponentIdentifier target, VirtualConnection con)
	{
		SubscriptionIntermediateFuture<PlatformData>[]	notify;
		VirtualConnection	vircon;
		synchronized(this)
		{
			vircon	= getVirtualConnection(target);
			if(vircon==con)
			{
				virtuals.remove(target);
			}
			
			@SuppressWarnings("unchecked")
			SubscriptionIntermediateFuture<PlatformData>[]	tmp
				= infosubscribers!=null ? infosubscribers.toArray(new SubscriptionIntermediateFuture[infosubscribers.size()]) : null;
			notify	= tmp;
		}

		if(notify!=null)
		{
			// Removed connection -> ready=null.
			PlatformData	info	= vircon.getPlatformdata(target);
			for(SubscriptionIntermediateFuture<PlatformData> fut: notify)
			{
				fut.addIntermediateResult(info);
			}
		}
	}



	/**
	 * Get the target platform for a message.
	 */
	protected IComponentIdentifier getTarget(IMsgHeader header)
	{
		IComponentIdentifier rec = (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER);
		assert rec != null; // Message feature should disallow sending without receiver.
		return rec.getRoot();
	}
	
	/**
	 * Get the target addresses for a message.
	 * 
	 * @param The message header.
	 * @return The addresses, if any.
	 */
	protected IIntermediateFuture<String> getAddresses(IMsgHeader header)
	{
		IComponentIdentifier target = getTarget(header).getRoot();
		ITransportAddressService tas = ((AbstractComponentFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(ITransportAddressService.class);
		
		final IntermediateFuture<String> ret = new IntermediateFuture<String>();
		tas.resolveAddresses(target, impl.getProtocolName()).addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, Collection<String>>(ret)
		{
			public void customResultAvailable(List<TransportAddress> addrs) throws Exception
			{
				if (addrs != null && addrs.size() > 0)
				{
					for (TransportAddress addr : addrs)
					{
						ret.addIntermediateResult(addr.getAddress());
					}
				}
				
				ret.setFinished();
			}
		});
		
//		System.out.println("Found " + Arrays.toString(ret) + " for pf " + target);
		return ret;
	}
	
	/**
	 *  Delivers a remote message to a component.
	 * 
	 *  @param agent Agent performing the delivery.
	 *  @param secser The security service.
	 *  @param cms The component management service.
	 *  @param serser The serialization services.
	 *  @param source Source ID of the message.
	 *  @param header The header of the message.
	 *  @param body The body of the message.
	 */
	public static final void deliverRemoteMessage(final IInternalAccess agent, ISecurityService secser, final IComponentManagementService cms, final ISerializationServices serser, final IComponentIdentifier source, byte[] header, final byte[] body)
	{
		final Logger logger = agent.getLogger();
		// First decrypt.
		secser.decryptAndAuth(source, header).addResultListener(new IResultListener<Tuple2<IMsgSecurityInfos, byte[]>>()
		{
			@Override
			public void resultAvailable(Tuple2<IMsgSecurityInfos, byte[]> tup)
			{
				if(tup.getSecondEntity() != null)
				{
					// Then decode header and deliver to receiver agent.
					final IMsgHeader header = (IMsgHeader)serser.decode(null, agent, tup.getSecondEntity());
					final IComponentIdentifier rec = (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER);
					
					cms.getExternalAccess(rec).addResultListener(new IResultListener<IExternalAccess>()
					{
						@Override
						public void resultAvailable(IExternalAccess exta)
						{
							exta.scheduleStep(new IComponentStep<Void>()
							{
								@Override
								public IFuture<Void> execute(IInternalAccess ia)
								{
									IMessageFeature mf = ia.getFeature0(IMessageFeature.class);
									if(mf instanceof IInternalMessageFeature)
									{
										((IInternalMessageFeature)mf).messageArrived(header, body);
									}
									return IFuture.DONE;
								}
							}).addResultListener(new IResultListener<Void>()
							{
								@Override
								public void resultAvailable(Void result)
								{
									// NOP
								}

								@Override
								public void exceptionOccurred(Exception exception)
								{
									logger.warning("Could not deliver message from platform " + source + " to " + rec + ": " + exception);
								}
							});
						}

						@Override
						public void exceptionOccurred(final Exception exception)
						{
							logger.warning("Could not deliver message from platform " + source + " to " + rec + ": " + exception);
							
							// For undeliverable conversation messages -> send error reply (only for non-error messages). 
							if((header.getProperty(IMsgHeader.CONVERSATION_ID)!=null || header.getProperty(RemoteExecutionComponentFeature.RX_ID)!=null)
								&& header.getProperty(MessageComponentFeature.EXCEPTION)==null)
							{
								agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
								{
									@Override
									public IFuture<Void> execute(IInternalAccess ia)
									{
										Map<String, Object>	addheaderfields	= ((MsgHeader)header).getProperties();
										addheaderfields.put(MessageComponentFeature.EXCEPTION, exception);
										ia.getFeature(IMessageFeature.class)
											.sendMessage(null, addheaderfields, (IComponentIdentifier)header.getProperty(IMsgHeader.SENDER))
											.addResultListener(new IResultListener<Void>()
											{
												@Override
												public void exceptionOccurred(Exception exception)
												{
													logger.warning("Could send error message to " + header.getProperty(IMsgHeader.SENDER) + ": " + exception);
												}
												
												@Override
												public void resultAvailable(Void result)
												{
													// OK -> ignore
//																System.out.println("Sent error message: "+header.getProperty(IMsgHeader.SENDER) + ", "+exception);
												}
											});
										return IFuture.DONE;
									}
								});
							}
						}
					});
				}
			}

			@Override
			public void exceptionOccurred(Exception exception)
			{
				logger.warning("Could not deliver message from platform " + source + ": " + exception);
			}
		});
	}

	// -------- helper classes --------

	/**
	 * A connection candidate stores state about a connection that is not (yet)
	 * selected as connection to be used for communication with a remote
	 * platform, i.e. before the handshake is complete.
	 */
	public class ConnectionCandidate implements Comparable<ConnectionCandidate>
	{
		/**
		 * The type of connection, when open. Used to replace connections, if
		 * necessary.
		 */
		protected boolean				clientcon;

		/** The confirmed target platform, if known. */
		protected IComponentIdentifier	target;

		/** The impl connection. */
		protected Con					con;

		/** Flag to indicate a closing connection. */
		// just for checking.
		protected boolean				closing;

		// -------- constructors --------

		/**
		 * Create a connection candidate
		 */
		public ConnectionCandidate(Con con, boolean clientcon)
		{
			this.con = con;
			this.clientcon = clientcon;
		}

		// -------- methods --------

		/**
		 * Get the impl connection.
		 */
		public Con getConnection()
		{
			return con;
		}

		/**
		 * Get the target platform, if known.
		 */
		public IComponentIdentifier getTarget()
		{
			return target;
		}

		/**
		 * Set the target platform.
		 */
		public void setTarget(IComponentIdentifier target)
		{
			// Hack to allow ConnectionCandidate to be used as transfer bean w/o outer object.
			if(AbstractTransportAgent.this==null)
			{
				this.target = target;
			}
			
			else
			{
				synchronized(AbstractTransportAgent.this)
				{
					assert this.target == null;
					this.target = target;
	
					VirtualConnection virt = getVirtualConnection(target);
					if(virt == null)
					{
						virt = createVirtualConnection(target);
					}
					virt.addConnection(this);
				}
			}
		}

		/**
		 * Mark the connection as unpreferred. May lead to disconnection (if
		 * client connection).
		 */
		public void unprefer()
		{
			boolean	close	= false;
			synchronized(this)
			{
				if(clientcon && !closing)
				{
					close	= true;
					this.closing	= true;
				}
			}
			if(close)
			{
				agent.getLogger().info("Closing duplicate connection " + con + " to: "+ getTarget());
				impl.closeConnection(con);
			}
		}

		// -------- Comparable interface --------

		/**
		 * Check which connection should have preference. Conflicts are resolved
		 * by dropping the client connection of the smaller platform name or the
		 * server connection of the larger platform name (by String.compareTo).
		 */
		@Override
		public int compareTo(ConnectionCandidate o)
		{
			assert target != null;
			// When same type -> no difference (i.e. keep previous, drop new)
			return clientcon == o.clientcon ? 0 :
			// the current is client then name<target or the current is server
			// then name>target
				(clientcon ? 1 : -1) * agent.getIdentifier().getPlatformName().compareTo(target.getName());
		}
	}

	/**
	 * Object to summarize state of connection(s) to a given platform.
	 */
	public class VirtualConnection
	{
		// -------- attributes --------

		/** The future, if any, when isReady() was called but not yet confirmed. */
		protected Future<Integer>			fut;

		/** The available connection candidates. */
		protected List<ConnectionCandidate>	cons;
		
		/** Flag, when the connection failed and should be removed. */
		protected boolean failed;
		
		// -------- methods --------

		/**
		 * Get an impl connection for message sending, if any.
		 */
		public Con getConnection()
		{
			synchronized(this)
			{
				return cons != null && !cons.isEmpty() ? cons.get(0).getConnection() : null;
			}
		}

		/**
		 * Add a connection. Conflicts are resolved by dropping the client
		 * connection of the smaller platform name. The server cand replaces,
		 * but doesn't drop.
		 * 
		 * @param connection The (new) connection to be set.
		 */
		protected void addConnection(ConnectionCandidate cand)
		{
			ConnectionCandidate	unprefer	= null;
			Future<Integer>	fut	= null;
			boolean log	= false;
			boolean	close	= false;
			boolean notify	= false;
			
			synchronized(this)
			{
				assert this.cons==null || !this.cons.contains(cand);
				
				if(failed)
				{
					close	= true;
				}
				else
				{
					if(cons==null)
					{
						cons = new ArrayList<ConnectionCandidate>();
					}
		
					// Is the new the preferred connection?
					if(cons.isEmpty() || cons.get(0).compareTo(cand)<0)
					{
						cons.add(0, cand);
						log	= true;	// tell logger to info handshake.
						
						// Inform listener, if any.
						if(this.fut!=null)
						{
							fut	= this.fut;
							this.fut = null;
						}
		
						// Unprefer previous connection, if any
						if(cons.size() > 1)
						{
							unprefer	= cons.get(1);
						}
						
						notify	= true;
					}
		
					// Keep connection but unprefer, to cause abort, if on client side.
					else
					{
						cons.add(cand);
						unprefer	= cand;
					}
				}
			}
			
			if(log)
			{
//				System.out.println("Completed handshake for connection " + cand.getConnection() + " to: "+ cand.getTarget());
				agent.getLogger().info("Completed handshake for connection " + cand.getConnection() + " to: "+ cand.getTarget());
			}

			if(fut!=null)
			{
				fut.setResult(priority);
			}
			
			if(unprefer!=null)
			{
				unprefer.unprefer();
			}
			
			if(close)
			{
				impl.closeConnection(cand.getConnection());
			}
			
			if(notify)
			{
				IComponentIdentifier	mytarget	= null;
				SubscriptionIntermediateFuture<PlatformData>[]	subs	= null;
				synchronized(AbstractTransportAgent.this)
				{
					// (new) connection established -> notify listeners, if any
					if(infosubscribers!=null)
					{
						@SuppressWarnings("unchecked")
						SubscriptionIntermediateFuture<PlatformData>[]	tmp
							= infosubscribers.toArray(new SubscriptionIntermediateFuture[infosubscribers.size()]);
						subs	= tmp;
						mytarget	= SUtil.findKeyForValue(virtuals, this);
					}
				}

				if(subs!=null)
				{
					PlatformData info = getPlatformdata(mytarget);
					for(SubscriptionIntermediateFuture<PlatformData> subfut: subs)
					{
						subfut.addIntermediateResult(info);
					}
				}
			}
		}

		/**
		 *  Transferrable info about the connection.
		 *  @param target The target to this connection.
		 */
		protected PlatformData getPlatformdata(IComponentIdentifier target)
		{
			boolean	contained;
			synchronized(AbstractTransportAgent.this)
			{
				// Not contained? removed connection -> ready=null.
				contained	= virtuals.containsKey(target);
			}
			return new PlatformData(target, impl.getProtocolName(), contained ? isReady().isDone() : null);
		}

		/**
		 * Remove a connection.
		 * 
		 * @param cand The connection to remove.
		 */
		protected void removeConnection(ConnectionCandidate cand)
		{
			synchronized(this)
			{
				assert this.cons != null && this.cons.contains(cand);
				cons.remove(cand);
				if(cons.isEmpty())
				{
					failed	= true;
				}
			}
			
			if(failed)
			{
				fail(new RuntimeException("No more "+impl.getProtocolName()+" connections to "+cand.getTarget()));
				removeVirtualConnection(cand.getTarget(), this);
			}
		}

		/**
		 * Future to indicate that the connection is ready to send messages.
		 */
		protected IFuture<Integer> isReady()
		{
			synchronized(this)
			{
				if(cons==null || cons.isEmpty())
				{
					if(fut==null)
					{
						fut = new Future<Integer>();
					}
					return fut;
				}
				else
				{
					return new Future<Integer>(priority);
				}
			}
		}


		/**
		 * Indicate that all attempts to open a client connection have failed.
		 */
		protected void fail(Exception e)
		{
			Future<Integer>	fut	= null;

			synchronized(this)
			{
				// Only fail, when no backward connection in mean time.
				if(failed || (cons==null || cons.isEmpty()) && this.fut!=null)
				{
					fut	= this.fut;
					this.fut = null;
					this.failed	= true;
				}
			}
			
			if(fut!=null)
			{
				fut.setExceptionIfUndone(e);
			}
		}
	}

	//-------- IInternalService interface -------- 
	
	private IServiceIdentifier sid;
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public IFuture<Boolean> isValid()
	{
		return new Future<Boolean>(true);
	}
		
	/**
	 *  Get the map of properties (considered as constant).
	 *  @return The service property map (if any).
	 */
	public Map<String, Object> getPropertyMap()
	{
		return new HashMap<String, Object>();
	}
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	startService() {return IFuture.DONE;}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void>	shutdownService() {return IFuture.DONE;}
	
	/**
	 *  Sets the access for the component.
	 *  @param access Component access.
	 */
	public IFuture<Void> setComponentAccess(@Reference IInternalAccess access) {return IFuture.DONE;}	

	/**
	 *  Set the service identifier.
	 */
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
}
