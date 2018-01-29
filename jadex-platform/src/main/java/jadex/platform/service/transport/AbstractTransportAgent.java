package jadex.platform.service.transport;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.component.impl.RemoteExecutionComponentFeature;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
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
@ProvidedServices(@ProvidedService(scope=Binding.SCOPE_PLATFORM, type=ITransportService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)))
public abstract class AbstractTransportAgent<Con> implements ITransportService, IInternalService, ITransportHandler<Con>
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

	// -------- abstract methods to be provided by concrete transport --------

	/**
	 * Get the transport implementation
	 */
	public abstract ITransport<Con> createTransportImpl();

	// -------- ITransportHandler, i.e.methods to be called by concrete
	// transport --------

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
			if (IComponentDescription.STATE_ACTIVE.equals(agent.getComponentDescription().getState()))
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
		this.codec = MessageComponentFeature.getSerializationServices(agent.getComponentIdentifier().getRoot());
		this.secser	= SServiceProvider.getLocalService(agent, ISecurityService.class, Binding.SCOPE_PLATFORM, false);
		this.cms	= SServiceProvider.getLocalService(agent, IComponentManagementService.class, Binding.SCOPE_PLATFORM, false);
		this.impl = createTransportImpl();
		impl.init(this);

		// Set up server, if port given.
		// If port==0 -> any free port
		if(port >= 0)
		{
			final Future<Void>	ret	= new Future<Void>();
			impl.openPort(port).addResultListener(new ExceptionDelegationResultListener<Integer, Void>(ret)
			{
				@Override
				public void customResultAvailable(Integer port)
				{
					try
					{
						// Announce connection addresses.
						InetAddress[] addresses = SUtil.getNetworkAddresses();
//						String[] saddresses = new String[addresses.length];
						IComponentIdentifier platformid = agent.getComponentIdentifier().getRoot();
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
						
						agent.getLogger().info("Platform "+agent.getComponentIdentifier().getPlatformName()+" listening to port " + port + " for " + impl.getProtocolName() + " transport.");

//						TransportAddressBook tab = TransportAddressBook.getAddressBook(agent);
//						tab.addPlatformAddresses(agent.getComponentIdentifier(), impl.getProtocolName(), saddresses);
						ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class, Binding.SCOPE_PLATFORM, false);
						
//						System.out.println("Transport addresses: "+agent+", "+saddresses);
						tas.addLocalAddresses(saddresses).addResultListener(new DelegationResultListener<Void>(ret));
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			});
			
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
		final Future<Integer>	ret	= new Future<Integer>();
		VirtualConnection	handler;
		final IComponentIdentifier	target	= getTarget(header);
		
		synchronized(this)
		{
			handler = getVirtualConnection(target);
		}
		if(handler==null)
		{
//			agent.getLogger().severe("isReady no handler");
			getAddresses(header).addResultListener(new ExceptionDelegationResultListener<String[], Integer>(ret)
			{
				public void customResultAvailable(String[] addresses) throws Exception
				{
//					agent.getLogger().severe("isReady got addresses: "+addresses);
					VirtualConnection handler;
					boolean	create	= false;
					synchronized(AbstractTransportAgent.this)
					{
						handler = getVirtualConnection(target);
						if(handler==null && addresses!=null && addresses.length>0)
						{
							handler	= createVirtualConnection(target);
							create	= true;
						}
					}
					
					if(create)
						createConnections(handler, target, addresses);
					
					if(handler!=null)
					{
						handler.isReady().addResultListener(new DelegationResultListener<Integer>(ret));
					}
					else
					{
						ret.setException(new RuntimeException("No addresses found for " + impl.getProtocolName() + ": " + header));
					}
				}
			});
			
		}
		else
		{
//			agent.getLogger().severe("isReady handler: "+handler);
			handler.isReady().addResultListener(new DelegationResultListener<Integer>(ret));;
		}
		
		return ret;
	}
	/*public IFuture<Integer> isReady(final IMsgHeader header)
	{
		IFuture<Integer>	ret	= null;
		boolean	create	= false;
		String[]	addresses	= null;
		VirtualConnection	handler;
		IComponentIdentifier	target	= getTarget(header);
		
		synchronized(this)
		{
			handler = getVirtualConnection(target);
			if(handler==null)
			{
				addresses = getAddresses(header);
				if(addresses!=null && addresses.length>0)
				{
					handler	= createVirtualConnection(target);
					create	= true;
				}
				else
				{
					ret	= new Future<Integer>(new RuntimeException("No addresses found for " + impl.getProtocolName() + ": " + header));
				}
			}
		}
		
		if(ret==null)
		{
			ret	= handler.isReady();
		}
		
		if(create)
		{
			createConnections(handler, target, addresses);
		}
		
		return ret;
	}*/
	
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
					Con con	= handler.getConnection();
					if(handler==null || con==null)
					{
						ret.setException(new RuntimeException("No connection to " + getTarget(header)));
					}
					else
					{
						trySend(con, handler, ebheader);
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
			}
		}

		// Start handshake by sending id.
		agent.getLogger().info((clientcon ? "Connected to " : "Accepted connection ") + con + ". Starting handshake...");
		impl.sendMessage(con, new byte[0], agent.getComponentIdentifier().getPlatformName().getBytes(SUtil.UTF8));
		
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
		synchronized(this)
		{
			if(virtuals==null)
			{
				virtuals = new HashMap<IComponentIdentifier, VirtualConnection>();
			}
			VirtualConnection vircon = new VirtualConnection();
			VirtualConnection prev = virtuals.put(target, vircon);
			assert prev == null;
			return vircon;
		}
	}
	
	/**
	 *  Get or create a virtual connection.
	 */
	protected VirtualConnection getOrCreateVirtualConnection(IComponentIdentifier target)
	{
		synchronized(this)
		{
			VirtualConnection	ret	= getVirtualConnection(target);
			if(ret==null)
			{
				ret	= createVirtualConnection(target);
			}
			return ret;
		}
	}
	
	/**
	 *  Remove a virtual connection if it is still the current connection for the target.
	 */
	protected void	removeVirtualConnection(IComponentIdentifier target, VirtualConnection con)
	{
		synchronized(this)
		{
			VirtualConnection	vircon	= getVirtualConnection(target);
			if(vircon==con)
			{
				virtuals.remove(target);
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
	protected IFuture<String[]> getAddresses(IMsgHeader header)
	{
		IComponentIdentifier target = getTarget(header).getRoot();
//		TransportAddressBook book = (TransportAddressBook)PlatformConfiguration.getPlatformValue(agent.getComponentIdentifier(), PlatformConfiguration.DATA_ADDRESSBOOK);
//		String[] ret = book.getPlatformAddresses(target, impl.getProtocolName());
		ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class, Binding.SCOPE_PLATFORM, false);
//		String[] ret = null;
//		try
//		{
//			List<TransportAddress> addrs = tas.resolveAddresses(target, impl.getProtocolName()).get();
//			if (addrs != null)
//			{
//				ret = new String[addrs.size()];
//				int i = 0;
//				for (TransportAddress addr : addrs)
//				{
//					ret[i] = addr.getAddress();
//					++i;
//				}
//			}
//		}
//		catch (Exception e)
//		{
//		}
		
		final Future<String[]> ret = new Future<String[]>();
		tas.resolveAddresses(target, impl.getProtocolName()).addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, String[]>(ret)
		{
			public void customResultAvailable(List<TransportAddress> addrs) throws Exception
			{
				String[] straddrs = null;
				if (addrs != null && addrs.size() > 0)
				{
					straddrs = new String[addrs.size()];
					int i = 0;
					for (TransportAddress addr : addrs)
					{
						straddrs[i] = addr.getAddress();
						++i;
					}
				}
				
				ret.setResult(straddrs);
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
									IMessageFeature mf = ia.getComponentFeature0(IMessageFeature.class);
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
										ia.getComponentFeature(IMessageFeature.class)
											.sendMessage((IComponentIdentifier)header.getProperty(IMsgHeader.SENDER), null, addheaderfields)
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
				(clientcon ? 1 : -1) * agent.getComponentIdentifier().getPlatformName().compareTo(target.getName());
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
	public void createServiceIdentifier(String name, Class<?> implclazz, IResourceIdentifier rid, Class<?> type, String scope, boolean unrestricted)
	{
		this.sid = BasicService.createServiceIdentifier(agent.getComponentIdentifier(), name, type, implclazz, rid, scope, unrestricted);
	}
}
