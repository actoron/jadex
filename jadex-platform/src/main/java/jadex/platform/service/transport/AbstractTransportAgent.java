package jadex.platform.service.transport;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.base.PlatformConfiguration;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.platformstate.IPlatformStateService;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.Boolean3;
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

/**
 *  Base class for transports.
 * 	@param <Con> A custom object type to hold connection information as required by the concrete transport.
 */
@Agent(autoprovide=Boolean3.TRUE)
public abstract class AbstractTransportAgent<Con> implements ITransportService, ITransportHandler<Con>
{
	//-------- arguments --------
	
	/** The default priority, when choosing a transport to communicate with a specific platform. */
	@AgentArgument
	protected int	priority	= 1000;
	
	/** The port, the transport should listen to (&lt;0: don't listen, 0: choose random port, >0: use given port). */
	@AgentArgument
	protected int	port	= 0;
	
	/**
	 *  The keep-alive (group), i.e. an address, to which the transport should stay connected
	 *  or a group of addresses (comma separated), where the transport should stay connected one of the group.
	 *  If the connection fails, the transport will try to reconnect, possibly after a timeout. 
	 */
	@AgentArgument
	// TODO: not yet implemented... required e.g. for message relaying
	protected String	keepalivegroup	= null;
	
	//-------- internal attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The encoder/decoder. */
	protected ISerializationServices	codec;
	
	/** The transport implementaion. */
	protected ITransport<Con>	impl;
	
	/** The connections currently in use (target platform -> virtual connection).
	 *  Used e.g. for sendMessage(). */
	protected Map<IComponentIdentifier, VirtualConnection>	virtuals;

	/** The connections currently in handshake or in use (impl connection -> connection candidate object).
	 *  Used also for messageReceived(). */
	protected Map<Con, ConnectionCandidate>	candidates;

	//-------- abstract methods to be provided by concrete transport --------
	
	/**
	 *  Get the transport implementation
	 */
	public abstract ITransport<Con>	createTransportImpl();
	
	//-------- ITransportHandler, i.e.methods to be called by concrete transport --------
	
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess	getAccess()
	{
		return agent;
	}
	
	/**
	 *  Deliver a received message.
	 *  @param con	The connection.
	 *  @param header	The message header.
	 *  @param body	The message body.
	 */
	public void	messageReceived(final Con con, final byte[] header, final byte[] body)
	{
		if(agent.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			ConnectionCandidate	cand	= getConnectionCandidate(con);
			final IComponentIdentifier	source	= cand.getTarget();

			// First msg is CID from handshake.
			if(source==null)
			{
				assert header.length==0;
				String	name	= (String)codec.decode(agent.getClassLoader(), body);
				cand.setTarget(new BasicComponentIdentifier(name));
			}
			else
			{
				// First decrypt.
				ISecurityService	secser	= SServiceProvider.getLocalService(agent, ISecurityService.class, Binding.SCOPE_PLATFORM);
				secser.decryptAndAuth(source, header)
					.addResultListener(new IResultListener<Tuple2<IMsgSecurityInfos, byte[]>>()
				{
					@Override
					public void resultAvailable(Tuple2<IMsgSecurityInfos, byte[]> tup)
					{
						if(tup.getSecondEntity()!=null)
						{
							// Then decode header and deliver to receiver agent.
							final Map<String, Object>	header	= (Map<String, Object>)codec.decode(agent.getClassLoader(), tup.getSecondEntity());
							final IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
														
							IComponentManagementService	cms	= SServiceProvider.getLocalService(agent, IComponentManagementService.class, Binding.SCOPE_PLATFORM);
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
											IMessageFeature	mf	= ia.getComponentFeature0(IMessageFeature.class);
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
											getLogger().warning("Could not deliver message from platform "+source+" to "+rec+": "+exception);
										}
									});
								}
								
								@Override
								public void exceptionOccurred(Exception exception)
								{
									getLogger().warning("Could not deliver message from platform "+source+" to "+rec+": "+exception);
								}
							});
						}
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						getLogger().warning("Could not deliver message from platform "+source+": "+exception);
					}
				});
			}
		}
		else
		{
			agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					messageReceived(con, header, body);
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Called when a server connection is established.
	 *  @param con	The connection.
	 */
	public void	connectionEstablished(final Con con)
	{
		if(agent.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			createConnectionCandidate(con, false);
			
			// Start handshake by sending id.
			impl.sendMessage(con, new byte[0], agent.getComponentIdentifier().getPlatformName().getBytes(SUtil.UTF8));
		}
		else
		{
			agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					connectionEstablished(con);
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Called when a connection is closed.
	 *  @param con	The connection.
	 *  @param e	The exception, if any.
	 */
	public void	connectionClosed(final Con con, final Exception e)
	{
		if(agent.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			ConnectionCandidate	cand	= getConnectionCandidate(con);
			assert cand!=null;
			removeConnectionCandidate(cand);
		}
		else
		{
			agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					connectionClosed(con, e);
					return IFuture.DONE;
				}
			});
		}
	}
	
	//-------- life cycle --------
	
	/**
	 *  Agent initialization.
	 */
	@AgentCreated
	protected void	init() throws Exception
	{
		IPlatformStateService	plast	= SServiceProvider.getLocalService(agent, IPlatformStateService.class, Binding.SCOPE_PLATFORM);
		this.codec	= plast.getSerializationServices();
		this.impl	= createTransportImpl();
		
		// Set up server, if port given.
		// If port==0 -> any free port
		if(port>=0)
		{
			impl.openPort(port).addResultListener(new IResultListener<Integer>()
			{
				@Override
				public void resultAvailable(Integer result)
				{
					try
					{
						// Announce connection addresses.
						InetAddress[]	addresses	= SUtil.getNetworkAddresses();
						String[]	saddresses	= new String[addresses.length];
						for(int i=0; i<addresses.length; i++)
						{
							if(addresses[i] instanceof Inet6Address)
							{
								saddresses[i]	= "[" + addresses[i].getHostAddress() + "]:" + port;
							}
							else // if (address instanceof Inet4Address)
							{
								saddresses[i]	= addresses[i].getHostAddress() + ":" + port;
							}
						}

						TransportAddressBook	tab	= TransportAddressBook.getAddressBook(agent);
						tab.addPlatformAddresses(agent.getComponentIdentifier(), impl.getProtocolName(), saddresses);
					}
					catch(Exception e)
					{
						exceptionOccurred(e);
					}
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					agent.getLogger().warning("Problem opening port "+port+" for "+impl.getProtocolName()+" transport: "+exception);
				}
			});
		}
	}
	
	/**
	 *  Agent shutdown.
	 */
	@AgentKilled
	protected void	shutdown()
	{
		impl.shutdown();
	}
	
	//-------- ITransportService interface --------
	
	/**
	 *  Checks if the transport is ready.
	 * 
	 *  @param header Message header.
	 *  @return Transport priority, when ready
	 */
	public IFuture<Integer> isReady(Map<String, Object> header)
	{
		VirtualConnection	handler	= getVirtualConnection(getTarget(header));
		if(handler!=null)
		{
			return handler.isReady();
		}
		else
		{
			return createConnections(header);
		}
	}
	
	/**
	 *  Send a message.
	 *  Fail fast implementation. Retry should be handled in message feature.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Done, when sent, failure otherwise.
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> header, final byte[] body)
	{
		VirtualConnection	handler	= getVirtualConnection(getTarget(header));
		if(handler==null || handler.getConnection()==null)
		{
			return new Future<Void>(new RuntimeException("No connection to "+getTarget(header)));
		}
		else
		{
			final Future<Void>	ret	= new Future<Void>();
			byte[]	bheader	= codec.encode(header, agent.getClassLoader(), header);
			
			ISecurityService	secser	= SServiceProvider.getLocalService(agent, ISecurityService.class, Binding.SCOPE_PLATFORM);
			secser.encryptAndSign(header, bheader)
				.addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
			{
				@Override
				public void customResultAvailable(final byte[] ebheader) throws Exception
				{
					VirtualConnection	handler	= getVirtualConnection(getTarget(header));
					if(handler==null || handler.getConnection()==null)
					{
						ret.setException(new RuntimeException("No connection to "+getTarget(header)));
					}
					else
					{
						trySend(handler, ebheader);
					}
				}
				
				protected	void	trySend(final VirtualConnection handler, final byte[] ebheader)
				{
					// Try with existing connection. When failed -> check if new connection available,
					// e.g. if current connection terminated due to multiple open connections.
					final Con	con	= handler.getConnection();
					impl.sendMessage(con, ebheader, body)
						.addResultListener(new DelegationResultListener<Void>(ret)
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							if(handler.getConnection()!=null && handler.getConnection()!=con)
							{
								trySend(handler, ebheader);
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
	
	//-------- helper methods --------
		
	/**
	 *  Convenience method.
	 */
	protected Logger	getLogger()
	{
		return agent.getLogger();
	}
	
	/**
	 *  Create a connection to a given platform.
	 *  Tries all available addresses in parallel.
	 *  Fails when no connection can be established.
	 */
	protected IFuture<Integer>	createConnections(final Map<String, Object> header)
	{
		final String[] addresses = getAddresses(header);
		if(addresses!=null && addresses.length>0)
		{
			final VirtualConnection	handler	= createVirtualConnection(getTarget(header));
			// Counter for failed connections to know when all are failed.
			final int[]	failed	= new int[]{0};
			
			for(String address: addresses)
			{
				IFuture<Con>	fcon	= impl.createConnection(address, getTarget(header));
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
						// All tries failed?
						if((failed[0]++) == addresses.length)
						{
							handler.fail(new RuntimeException("No connection to any address possible for "+impl.getProtocolName()+": "+header+", "+Arrays.toString(addresses)));
						}
					}							
				});
			}
			
			return handler.isReady();
		}
		else
		{
			return new Future<Integer>(new RuntimeException("No addresses found for "+impl.getProtocolName()+": "+header));
		}
	}
	
	//-------- connection management methods --------
	
	/**
	 *  Get the connection handler, if any.
	 */
	protected VirtualConnection	getVirtualConnection(IComponentIdentifier target)
	{
		assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
		return virtuals!=null ? virtuals.get(target) : null;
	}
	
	/**
	 *  Get the connection handler, if any.
	 */
	protected ConnectionCandidate	getConnectionCandidate(Con con)
	{
		assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
		return candidates!=null ? candidates.get(con) : null;
	}
	
	/**
	 *  Create a virtual connection.
	 */
	protected VirtualConnection	createVirtualConnection(IComponentIdentifier target)
	{
		assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
		if(virtuals==null)
		{
			virtuals	= new HashMap<IComponentIdentifier, VirtualConnection>();
		}
		VirtualConnection	vircon	= new VirtualConnection();
		VirtualConnection	prev	= virtuals.put(target, vircon);
		assert prev==null;
		return vircon;
	}
	
	/**
	 *  Create a connection candidate.
	 */
	protected ConnectionCandidate	createConnectionCandidate(Con con, boolean clientcon)
	{
		assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
		if(candidates==null)
		{
			candidates	= new HashMap<Con, ConnectionCandidate>();
		}
		ConnectionCandidate	cand	= new ConnectionCandidate(con, clientcon);
		ConnectionCandidate	prev	= candidates.put(con, cand);
		assert prev==null;
		return cand;
	}
	
	/**
	 *  Remove a connection candidate.
	 */
	protected void	removeConnectionCandidate(ConnectionCandidate cand)
	{
		assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
		ConnectionCandidate	prev	= candidates.remove(cand.getConnection());
		assert prev==cand;
		
		if(cand.getTarget()!=null)
		{
			VirtualConnection	vircon	= getVirtualConnection(cand.getTarget());
			assert vircon!=null;
			vircon.removeConnection(cand);
		}
	}
	
	/**
	 *  Get the target platform for a message.
	 */
	protected IComponentIdentifier	getTarget(Map<String, Object> header)
	{
		assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		assert rec!=null;	// Message feature should disallow sending without receiver.
		return rec.getRoot();
	}
	
//	/**
//	 *  Set an existing connection.
//	 */
//	protected void	setConnection(Map<String, Object> header, Con con)
//	{
//		boolean	ret;
//		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
//		synchronized(connections)
//		{
//			Con	prev	= connections.put(rec, con);
//			if(prev!=null)
//			{
//				if(agent.getComponentIdentifier().getPlatformName().compareTo(rec.getPlatformName())>0)
//				{
//					
//				}
//			}
//		}
//	}
//
//	/**
//	 *  Remove an existing connection to the given target from the cache (if still recent).
//	 */
//	protected void	removeConnection(Map<String, Object> header, Con con)
//	{
//		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
//		if(connections.get(rec)==con)
//		{
//			connections.remove(rec);
//		}
//	}
//	
//	/**
//	 *  Check for an existing connection in progress.
//	 */
//	protected Future<Con>	getFConnection0(Map<String, Object> header)
//	{
//		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
//		return fconnections.get(rec);
//	}
//	
//	/**
//	 *  Set an existing connection in progress.
//	 */
//	protected void	setFConnection(Map<String, Object> header, Future<Con> fcon)
//	{
//		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
//		assert !fconnections.containsKey(rec);
//		fconnections.put(rec, fcon);
//	}

	/**
	 *  Get the target addresses for a message.
	 *  @param The message header.
	 *  @return	The addresses, if any.
	 */
	protected String[] getAddresses(Map<String, Object> header)
	{
		IComponentIdentifier	target	= getTarget(header);
		TransportAddressBook	book	= (TransportAddressBook)PlatformConfiguration
			.getPlatformValue(agent.getComponentIdentifier(), PlatformConfiguration.DATA_ADDRESSBOOK);
		return book.getPlatformAddresses(target, impl.getProtocolName());
	}
	
	//-------- helper classes --------
	
	/**
	 *  A connection candidate stores state about a connection that is not (yet)
	 *  selected as connection to be used for communication with a remote platform,
	 *  i.e. before the handshake is complete.
	 */
	public class ConnectionCandidate implements Comparable<ConnectionCandidate>
	{
		/** The type of connection, when open. Used to replace connections, if necessary. */
		protected boolean	clientcon;
		
		/** The confirmed target platform, if known. */
		protected IComponentIdentifier	target;
		
		/** The impl connection. */
		protected Con	con;
		
		/** Flag to indicate a closing connection. */
		// just for checking.
		protected boolean closing;
		
		//-------- constructors --------
		
		/**
		 *  Create a connection candidate
		 */
		public ConnectionCandidate(Con con, boolean clientcon)
		{
			this.con	= con;
			this.clientcon	= clientcon;
		}
		
		//-------- methods --------
		
		/**
		 *  Get the impl connection.
		 */
		public Con	getConnection()
		{
			return con;
		}
		
		/**
		 *  Get the target platform, if known.
		 */
		public IComponentIdentifier getTarget()
		{
			return target;
		}
		
		/**
		 *  Set the target platform.
		 */
		public void	setTarget(IComponentIdentifier target)
		{
			assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
			assert this.target==null;
			
			this.target	= target;
			
			VirtualConnection	virt	= getVirtualConnection(target);
			if(virt==null)
			{
				virt	= createVirtualConnection(target);
			}
			virt.addConnection(this);
		}
		
		/**
		 *  Mark the connection as unpreferred.
		 *  May lead to disconnection (if client connection).
		 */
		public void	unprefer()
		{
			assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
			if(clientcon && !closing)
			{
				this.closing	= true;
				impl.closeConnection(con);
			}
		}
		 
		//-------- Comparable interface --------
		
		/**
		 *  Check which connection should have preference.
		 *  Conflicts are resolved by dropping the client connection of the smaller platform name
		 *  or the server connection of the larger platform name (by String.compareTo).
		 */
		@Override
		public int compareTo(ConnectionCandidate o)
		{
			assert target!=null;
			// When same type -> no difference (i.e. keep previous, drop new)
			return clientcon==o.clientcon ? 0 :
				// the current is client then name<target or the current is server then name>target
				(clientcon ? 1 : -1) * agent.getComponentIdentifier().getPlatformName().compareTo(target.getName());
		}
	}
	
	/**
	 *  Object to summarize state of connection(s) to a given platform.
	 */
	public class VirtualConnection
	{
		//-------- attributes --------
		
		/** The future, if any, when isReady() was called but not yet confirmed. */
		protected Future<Integer>	fut;
		
		/** The available connection candidates. */
		protected List<ConnectionCandidate>	cons;
				
		//-------- methods --------
		
		/**
		 *  Get an impl connection for message sending, if any.
		 */
		public Con	getConnection()
		{
			return cons!=null && !cons.isEmpty() ? cons.get(0).getConnection() : null;
		}
		
		/**
		 *  Add a connection.
		 *  Conflicts are resolved by dropping the client connection of the smaller platform name.
		 *  The server cand replaces, but doesn't drop.
		 *  @param connection	The (new) connection to be set.
		 */
		protected void	addConnection(ConnectionCandidate cand)
		{
			assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
			assert this.cons.isEmpty() || !this.cons.contains(cand);
			
			if(cons==null)
			{
				cons	= new ArrayList<ConnectionCandidate>();
			}
			
			// Is the new the preferred connection?
			if(cons.isEmpty() || cons.get(0).compareTo(cand)<0)
			{
				cons.add(0, cand);
								
				// Inform listener, if any.
				if(fut!=null)
				{
					fut.setResult(priority);
					fut	= null;
				}

				// Unprefer previous connection, if any
				if(cons.size()>1)
				{
					cons.get(1).unprefer();
				}
			}
			
			// Keep connection but unprefer, to cause abort on client side.
			else
			{
				cons.add(cand);
				cand.unprefer();
			}
		}

		/**
		 *  Remove a connection.
		 *  @param cand	The connection to remove.
		 */
		protected void	removeConnection(ConnectionCandidate cand)
		{
			assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
			assert this.cons!=null && this.cons.contains(cand);
			
			cons.remove(cand);
		}

		/**
		 *  Future to indicate that the connection is ready to send messages.
		 */
		protected IFuture<Integer>	isReady()
		{
			assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
			if((cons==null || cons.isEmpty()))
			{
				if(fut==null)
				{
					fut	= new Future<Integer>();
				}
				return fut;
			}
			else
			{
				return new Future<Integer>(priority);
			}
		}
		
		
		/**
		 *  Indicate that all attempts to open a client connection have failed.
		 */
		protected void	fail(Exception e)
		{
			assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
			if((cons==null || cons.isEmpty()) && fut!=null)
			{
				fut.setException(e);
				fut	= null;
			}
		}
	}
}
