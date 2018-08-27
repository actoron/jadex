package jadex.platform.service.transport;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.component.impl.RemoteExecutionComponentFeature;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.memstat.IMemstatService;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportInfoService;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.bridge.service.types.transport.PlatformData;
import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.LeaseTimeMap;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 * Base class for transports.
 * Thread-safe implementation for using transports as raw service.
 * 
 * @param <Con> A custom object type to hold connection information as required by the concrete transport.
 */
@Agent
@ProvidedServices({
	@ProvidedService(scope=RequiredService.SCOPE_PLATFORM, type=ITransportService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(scope=RequiredService.SCOPE_PLATFORM, type=ITransportInfoService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(scope=RequiredService.SCOPE_PLATFORM, type=IMemstatService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW))
})
public abstract class AbstractTransportAgent<Con> implements ITransportService, ITransportInfoService, IMemstatService,	IInternalService, ITransportHandler<Con>
{
	// -------- arguments --------

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

	/** The transport implementation. */
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
	
//	/** The cms (cached for speed). */
//	protected IComponentManagementService	cms;
	
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
				deliverRemoteMessage(agent, secser, codec, source, header, body);
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
		this.codec = MessageComponentFeature.getSerializationServices(agent.getId().getRoot());
		this.secser	= ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(ISecurityService.class);
//		this.cms	= ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(IComponentManagementService.class);
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
						IComponentIdentifier platformid = agent.getId().getRoot();
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
						
						agent.getLogger().info("Platform "+agent.getId().getPlatformName()+" listening to port " + port + " for " + impl.getProtocolName() + " transport.");

//						TransportAddressBook tab = TransportAddressBook.getAddressBook(agent);
//						tab.addPlatformAddresses(agent.getComponentIdentifier(), impl.getProtocolName(), saddresses);
						ITransportAddressService tas = ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(ITransportAddressService.class);
						
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
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param bheader Message header already encoded and encrypted for sending.
	 *  @param body Message body.
	 *  @return Transport priority, when sent. Failure does not need to be returned as message feature uses its own timeouts.
	 *  	Future is terminated by message feature, when another transport has sent the message.
	 */
	public ITerminableFuture<Integer> sendMessage(IMsgHeader header, byte[] bheader, byte[] body)
	{
		
		final TerminableFuture<Integer> ret = new TerminableFuture<>();
		final IComponentIdentifier	target	= (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER);
		assert target!=null; // Message feature should disallow sending without receiver.
		
//		System.out.println(agent+".sendMessage to "+target);

		// Check if connection handler exists, else create...
		VirtualConnection	handler;
		boolean	create	= false;
		synchronized(this)
		{
			handler = getVirtualConnection(target.getRoot());
			if(handler==null)
			{
				handler	= createVirtualConnection(target.getRoot());
				create	= true;
			}
		}
		
		// If no existing connection -> start creation of actual connections in background. 
		if(create)
		{
			handler.createConnections();
			handler.notifySubscribers();
		}

		// add message to handler -> will be sent when ready or otherwise remembered for sending later, if not terminated in mean time.
		handler.sendMessage(bheader, body, ret);
		
		return ret;
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
					ret.addIntermediateResult(entry.getValue().getPlatformdata());
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
					ret.addIntermediateResult(entry.getValue().getPlatformdata());
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
			if(virtuals!=null)
			{
				Map<IComponentIdentifier, String>	stringvirtuals	= new LinkedHashMap<>();
				for(Map.Entry<IComponentIdentifier, VirtualConnection> entry: virtuals.entrySet())
				{
					stringvirtuals.put(entry.getKey(), entry.getValue().toString());
				}
				ret.put("virtuals", stringvirtuals);
			}
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
		if(created)
		{
//			System.out.println(agent +(clientcon ? " connected to " : " accepted connection ") + con + ". Starting handshake...");
			agent.getLogger().info((clientcon ? "Connected to " : "Accepted connection ") + con + ". Starting handshake...");
			impl.sendMessage(con, new byte[0], agent.getId().getPlatformName().getBytes(SUtil.UTF8));
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
			if(vircon!=null)	// Can null, when connection removed after lease timeout
			{
				vircon.removeConnection(cand);
			}
		}
		else
		{
			agent.getLogger().info("Closed connection: " + cand.getConnection()+(e!=null? ", "+e:""));
		}
	}
	
	/**
	 *  Get the connection handler, if any.
	 *  @param target	The target platform id.
	 */
	protected VirtualConnection getVirtualConnection(IComponentIdentifier target)
	{
		// Should only be called for platforms.
		assert target.equals(target.getRoot());
		
		synchronized(this)
		{
			return virtuals!=null ? virtuals.get(target) : null;
		}
	}

	/**
	 *  Create a virtual connection.
	 *  @param target	The target platform id.
	 */
	protected synchronized VirtualConnection createVirtualConnection(IComponentIdentifier target)
	{
		// Should only be called for platforms.
		assert target.equals(target.getRoot());
		
		VirtualConnection vircon = new VirtualConnection(target);
		if(virtuals==null)
		{
			// Use twice the default timeout to avoid potential oscillations due to always hitting default timeout
			virtuals = new LeaseTimeMap<>(Starter.getScaledDefaultTimeout(agent.getId(), 2), new  ICommand<Tuple2<Entry<IComponentIdentifier, VirtualConnection>, Long>>()
			{
				@Override
				public void execute(Tuple2<Entry<IComponentIdentifier, AbstractTransportAgent<Con>.VirtualConnection>, Long> arg)
				{
					System.out.println(agent+" outdated connection to: "+arg.getFirstEntity().getKey()+" val: "+arg.getSecondEntity());
					arg.getFirstEntity().getValue().cleanup();
				}
			}, true, true, true);
		}
		VirtualConnection prev = virtuals.put(target, vircon);
		assert prev == null;
			
		return vircon;
	}
	
	/**
	 *  Remove a virtual connection if it is still the current connection for the target.
	 */
	protected void	removeVirtualConnection(IComponentIdentifier target, VirtualConnection con)
	{
		// Should only be called for platforms.
		assert target.equals(target.getRoot());

		boolean	notify	= false;
		synchronized(this)
		{
			if(getVirtualConnection(target)==con)
			{
				virtuals.remove(target);
				notify	= true;
			}
		}
		
		if(notify)
		{
			con.notifySubscribers();
		}
	}

	/**
	 * Get the target addresses for a message.
	 * 
	 * @param The message header.
	 * @return The addresses, if any.
	 */
	protected IIntermediateFuture<String> getAddresses(IComponentIdentifier target)
	{
		ITransportAddressService tas = ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(ITransportAddressService.class);
		
		final IntermediateFuture<String> ret = new IntermediateFuture<String>();
		tas.resolveAddresses(target, impl.getProtocolName()).addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, Collection<String>>(ret)
		{
			public void customResultAvailable(List<TransportAddress> addrs) throws Exception
			{
//				System.out.println(agent + " found " + addrs + " for pf " + target);
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
	public static final void deliverRemoteMessage(final IInternalAccess agent, ISecurityService secser, final ISerializationServices serser, final IComponentIdentifier source, byte[] header, final byte[] body)
	{
		final Logger logger = agent.getLogger();
		// First decrypt.
		secser.decryptAndAuth(source, header).addResultListener(new IResultListener<Tuple2<ISecurityInfo, byte[]>>()
		{
			@Override
			public void resultAvailable(Tuple2<ISecurityInfo, byte[]> tup)
			{
				if(tup.getSecondEntity() != null)
				{
					// Then decode header and deliver to receiver agent.
					final IMsgHeader header = (IMsgHeader)serser.decode(null, agent, tup.getSecondEntity());
					final IComponentIdentifier rec = (IComponentIdentifier)header.getProperty(IMsgHeader.RECEIVER);
					
					// Cannot use agent/cms.getExternalAccess(cid) because when remote call
					// is in init the call will be delayed after init has finished (deadlock)
					SComponentManagementService.scheduleStep(rec, new IComponentStep<Void>()
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
//													System.out.println("Sent error message: "+header.getProperty(IMsgHeader.SENDER) + ", "+exception);
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
				System.out.println("Could not deliver message from platform " + source + ": " + exception);
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
		@SuppressWarnings("unused")
		public void setTarget(IComponentIdentifier target)
		{
			// Hack to allow ConnectionCandidate to be used as transfer bean w/o outer object.
			// Claimed as unused by eclipse, grrr.
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
		 *  Mark the connection as unpreferred.
		 *  May lead to disconnection (if client connection).
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
				(clientcon ? 1 : -1) * agent.getId().getPlatformName().compareTo(target.getName());
		}
	}

	/**
	 * Object to summarize state of connection(s) to a given platform.
	 */
	public class VirtualConnection
	{
		// -------- attributes --------

		/** The target platform of this connection. */
		protected IComponentIdentifier	target;
		
		/** The future, if any, when sendMessage() was called but there is no connection yet. */
		protected Future<ConnectionCandidate>	fut;

		/** The connection (if any). */
		protected ConnectionCandidate	con;
		
		//-------- constructors --------
		
		/**
		 *  Create a virtual connection to a given target platform.
		 */
		public VirtualConnection(IComponentIdentifier target)
		{
			// Should only be called for platforms.
			assert target.equals(target.getRoot());
			
			this.target	= target;
		}
		
		// -------- methods --------
		
		/**
		 *  Get the target platform id.
		 */
		public IComponentIdentifier	getTarget()
		{
			return target;
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
			assert target.equals(cand.getTarget());
			
			ConnectionCandidate	unprefer	= null;
			Future<ConnectionCandidate>	fut	= null;
			boolean log	= false;
			boolean notify	= false;
			
			synchronized(this)
			{
				// Is the new the preferred connection?
				if(con==null || con.compareTo(cand)<0)
				{
					unprefer	= con;
					con	= cand;
					log	= true;	// tell logger to info handshake.
					
					// Inform listener, if any.
					if(this.fut!=null)
					{
						fut	= this.fut;
						this.fut = null;
					}
					notify	= true;
				}
				
		
				// Else unprefer, to cause abort, if on client side.
				else
				{
					unprefer	= cand;
				}
			}
			
			if(log)
			{
//				System.out.println("Completed handshake for connection " + cand.getConnection() + " to: "+ cand.getTarget());
				agent.getLogger().info("Completed handshake for connection " + cand.getConnection() + " to: "+ cand.getTarget());
			}

			if(fut!=null)
			{
				fut.setResult(con);
			}
			
			if(unprefer!=null)
			{
				unprefer.unprefer();
			}
			
			if(notify)
			{
				notifySubscribers();
			}
		}

		/**
		 *  Transferrable info about the connection.
		 *  @param target The target to this connection.
		 */
		protected PlatformData getPlatformdata()
		{
			boolean	contained;
			synchronized(AbstractTransportAgent.this)
			{
				// Not contained? removed connection -> ready=null.
				contained	= virtuals.containsKey(target);
			}
			return new PlatformData(target, impl.getProtocolName(), contained ? con!=null : null);
		}

		/**
		 * Remove a connection.
		 * 
		 * @param cand The connection to remove.
		 */
		protected void removeConnection(ConnectionCandidate cand)
		{
			assert target.equals(cand.getTarget());
			boolean	dofail	= false;
			synchronized(this)
			{
				if(con==cand)
				{
					dofail	= true;
					con	= null;
				}
			}
			
			if(dofail)
			{
				removeVirtualConnection(target, this);
			}
		}

		/**
		 *  Send a message.
		 *  
		 *  @param header Message header.
		 *  @param bheader Message header already encoded and encrypted for sending.
		 *  @param body Message body.
		 *  @return Transport priority, when sent. Failure does not need to be returned as message feature uses its own timeouts.
		 *  	Future is terminated by message feature, when another transport has sent the message.
		 */
		protected void	sendMessage(byte[] header, byte[] body, TerminableFuture<Integer> ret)
		{
			ConnectionCandidate	con	= null;
			IFuture<ConnectionCandidate>	fut	= null;
			synchronized(this)
			{
				if(this.con!=null)
				{
					con	= this.con;
				}
				else
				{
					if(this.fut==null)
					{
						this.fut = new Future<ConnectionCandidate>();
					}
					fut	= this.fut;
				}
			}
			
			assert con!=null || fut!=null;
			if(con!=null)
			{
				ConnectionCandidate	fcon	= con;
				impl.sendMessage(con.getConnection(), header, body)
					.addResultListener(new DelegationResultListener<Integer>(ret)
				{
					// On failure of old connection -> retry finding a new connection.
					@Override
					public void	exceptionOccurred(Exception e)
					{
						boolean	create	= false;
						synchronized(this)
						{
							// No new connection in mean time.
							if(VirtualConnection.this.con==fcon)
							{
								VirtualConnection.this.con	= null;
								create	= true;
							}
						}
						
						if(create)
						{
							createConnections();
						}
						
						sendMessage(header, body, ret);
					}
				});
			}
			else
			{
				fut.addResultListener(fcon -> impl.sendMessage(fcon.getConnection(), header, body)
					.addResultListener(new DelegationResultListener<>(ret)));
			}
		}
		
		/**
		 *  Create actual connections based on known/discovered addresses of target platform.
		 */
		protected void	createConnections()
		{
//			System.out.println(agent+" searching addresses for " + getTarget());
			getAddresses(getTarget()).addResultListener(addresses ->
			{
				for(final String address : addresses)
				{
//					System.out.println(agent+" attempting connection to " + getTarget() + " using address: " + address);
					agent.getLogger().info("Attempting connection to " + getTarget() + " using address: " + address);
					impl.createConnection(address, getTarget())
						.addResultListener(con ->
					{
						createConnectionCandidate(con, true);
					});
				}
			});
		}
		
		/**
		 *  Notify subscribers (if any) when the connection has changed.
		 */
		protected void	notifySubscribers()
		{
			SubscriptionIntermediateFuture<PlatformData>[]	notify;
			synchronized(AbstractTransportAgent.this)
			{
				@SuppressWarnings("unchecked")
				SubscriptionIntermediateFuture<PlatformData>[]	tmp
					= infosubscribers!=null ? infosubscribers.toArray(new SubscriptionIntermediateFuture[infosubscribers.size()]) : null;
				notify	= tmp;
			}
			if(notify!=null)
			{
				// Newly created connection -> ready=false.
				PlatformData	info	= getPlatformdata();
				for(SubscriptionIntermediateFuture<PlatformData> fut: notify)
				{
					fut.addIntermediateResult(info);
				}
			}
		}
		
		/**
		 *  Cleanup the connection after removal.
		 */
		protected void	cleanup()
		{
			if(con!=null)
			{
				assert con.getConnection()!=null;
				impl.closeConnection(con.getConnection());
			}
		}

		/**
		 *  Readable info about this virtual connection.
		 */
		@Override
		public String toString()
		{
			return "VirtualConnection(candidates="+candidates+")";
		}
	}

	//-------- IInternalService interface -------- 
	
	private IServiceIdentifier sid;
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getId()
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
