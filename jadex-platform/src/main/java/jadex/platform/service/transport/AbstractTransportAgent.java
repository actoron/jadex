package jadex.platform.service.transport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jadex.base.PlatformConfiguration;
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
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;

/**
 *  Base class for transports.
 * 	@param <Con> A custom object type to hold connection information as required by the concrete transport.
 */
@Agent(autoprovide=Boolean3.TRUE)
public abstract class AbstractTransportAgent<Con> implements ITransportService
{
	//-------- arguments --------
	
	/** The default priority, when choosing a transport to communicate with a specific platform. */
	@AgentArgument
	protected int	priority	= 1000;
	
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
	
	/** The open connections (target platform -> connection). */
	protected Map<IComponentIdentifier, Con>	connections	= new HashMap<IComponentIdentifier, Con>();

	/** The open connections (target platform -> connection). */
	protected Map<IComponentIdentifier, Future<Con>>	fconnections	= new HashMap<IComponentIdentifier, Future<Con>>();

	//-------- abstract methods to be provided by concrete transport --------
	
	/**
	 *  Get the protocol name.
	 */
	public abstract String	getProtocolName();
	
	/**
	 *  Create a connection to a given address.
	 *  @param	address	The target platform's address.
	 *  @param target	The target identifier to maybe perform authentication of the connection.
	 */
	protected abstract IFuture<Con>	createConnection(String address, IComponentIdentifier target);
	
	/**
	 *  Close a previously opened connection.
	 */
	protected abstract void	closeConnection(Con con);
	
	/**
	 *  Send a message over a given transport.
	 */
	protected abstract IFuture<Void>	doSendMessage(Con con, byte[] header, byte[] body);
	
	//-------- methods to be called by concrete transport --------
	
	/**
	 *  Announce current local addresses, e.g. when the transport successfully opened a port.
	 *  @param addresses	The address part of transport addresses, i.e., without protocol scheme.
	 */
	public void	announceAddresses(String[] addresses)
	{
		TransportAddressBook	tab	= TransportAddressBook.getAddressBook(agent);
		tab.addPlatformAddresses(agent.getComponentIdentifier(), getProtocolName(), addresses);
	}
	
	/**
	 *  Call when an unrequested connection is established, e.g. back channel. 
	 */
	public void	connectionAdded(IComponentIdentifier target, Con con)
	{
		
	}
	
	/**
	 *  Deliver a received message.
	 *  @param source	The sending platform.
	 */
	public void	deliverMessage(final IComponentIdentifier source, final byte[] bheader, final byte[] body)
	{
		agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ISecurityService	secser	= SServiceProvider.getLocalService(agent, ISecurityService.class, Binding.SCOPE_PLATFORM);
				secser.decryptAndAuth(source, bheader)
					.addResultListener(new IResultListener<Tuple2<IMsgSecurityInfos, byte[]>>()
				{
					@Override
					public void resultAvailable(Tuple2<IMsgSecurityInfos, byte[]> tup)
					{
//						if(tup.getFirstEntity().isTrustedPlatform() && tup.getSecondEntity()!=null)
						if(tup.getSecondEntity()!=null)
						{
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
				getLogger().warning("Could not deliver message from platform "+source+": "+exception);
			}
		});
	}
	
	/**
	 *  Convenience method.
	 */
	public Logger	getLogger()
	{
		return agent.getLogger();
	}
	
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess getAccess()
	{
		return agent;
	}
	
	//-------- life cycle --------
	
	/**
	 *  Agent initialization.
	 */
	@AgentCreated
	protected void	init() throws Exception
	{
		IPlatformStateService	plast	= SServiceProvider.getLocalService(agent, IPlatformStateService.class, Binding.SCOPE_PLATFORM);
		codec	= plast.getSerializationServices();
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
		final Future<Integer>	ret	= new Future<Integer>();
		if(getConnection0(header)!=null)
		{
			ret.setResult(priority);
		}
		else
		{
			createConnections(header).addResultListener(new ExceptionDelegationResultListener<Con, Integer>(ret)
			{
				@Override
				public void customResultAvailable(Con con) throws Exception
				{
					if(con!=null)
					{
						ret.setResult(priority);
					}
				}
			});
		}
		return ret;
	}
	
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Done, when sent, failure otherwise.
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> header, final byte[] body)
	{
		final Future<Void>	ret	= new Future<Void>();
		final Con	con	= getConnection0(header);
		if(con!=null)
		{
			// Try with existing connection. When failed -> retry fresh.
			encodeAndSendMessage(con, header, body)
				.addResultListener(new DelegationResultListener<Void>(ret)
			{
				@Override
				public void exceptionOccurred(Exception exception)
				{
					// Remove the connection to force reconnect.
					// If new connection in mean time -> no reconnect, just retry with new connection.
					removeConnection(header, con);
					
					// Retry.
					sendMessage(header, body)
						.addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			// No connection -> create connection before send.
			createConnections(header).addResultListener(new ExceptionDelegationResultListener<Con, Void>(ret)
			{
				@Override
				public void customResultAvailable(Con con) throws Exception
				{
					// Try once and fail otherwise.
					encodeAndSendMessage(con, header, body)
						.addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		return ret;
	}
	
	//-------- helper methods --------
			
	/**
	 *  Create a connection to a given platform.
	 *  Tries all available addresses in parallel.
	 *  Fails when no connection can be established.
	 */
	protected IFuture<Con>	createConnections(final Map<String, Object> header)
	{
		Future<Con>	ret;
		
		// Check existing connection.
		Con	con	= getConnection0(header);
		if(con!=null)
		{
			ret	= new Future<Con>(con);
		}
		else
		{
			// Check connection in progress.
			ret	= getFConnection0(header);
			
			// No valid connection and no connecting in progress.
			if(ret==null)
			{
				ret	= new Future<Con>();
				final Future<Con>	fret	= ret;
				
				IComponentIdentifier	rec	= ((IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER)).getRoot();
				
				final String[] addresses = getAddresses(header);
				if(addresses!=null && addresses.length>0)
				{
					// Counter for failed connections to know when all are failed.
					final int[]	failed	= new int[]{0};
					
					for(String address: addresses)
					{
						IFuture<Con>	fcon	= createConnection(address, rec);
						fcon.addResultListener(new IResultListener<Con>()
						{
							@Override
							public void resultAvailable(Con con)
							{
								if(fret.setResultIfUndone(con))
								{
									setConnection(header, con);
								}
								else
								{
									closeConnection(con);
								}
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								// All tries failed?
								if((failed[0]++) == addresses.length)
								{
									// Use if undone in case of parallel reverse connection established.
									fret.setExceptionIfUndone(new RuntimeException("No connection to any address possible for "+getProtocolName()+": "+header+", "+Arrays.toString(addresses)));
								}
							}							
						});
					}
				}
				else
				{
					ret.setException(new RuntimeException("No addresses found for "+getProtocolName()+": "+header));
				}
			}
		}

		return ret;
	}
	
	/**
	 *  Encrypt Send a message using the given connection.
	 */
	protected IFuture<Void>	encodeAndSendMessage(final Con con, Map<String, Object> header, final byte[] body)
	{
		final Future<Void>	ret	= new Future<Void>();
		byte[]	bheader	= codec.encode(header, agent.getClassLoader(), header);
		
		ISecurityService	secser	= SServiceProvider.getLocalService(agent, ISecurityService.class, Binding.SCOPE_PLATFORM);
		secser.encryptAndSign(header, bheader)
			.addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
		{
			@Override
			public void customResultAvailable(byte[] result) throws Exception
			{
				doSendMessage(con, result, body)
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		
		return ret;
	}

	//-------- cache management --------
	
	/**
	 *  Check for an existing connection.
	 */
	protected Con	getConnection0(Map<String, Object> header)
	{
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		return connections.get(rec);
	}
	
	/**
	 *  Set an existing connection.
	 */
	protected void	setConnection(Map<String, Object> header, Con con)
	{
		boolean	ret;
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		synchronized(connections)
		{
			Con	prev	= connections.put(rec, con);
		}
	}

	/**
	 *  Remove an existing connection to the given target from the cache (if still recent).
	 */
	protected void	removeConnection(Map<String, Object> header, Con con)
	{
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		if(connections.get(rec)==con)
		{
			connections.remove(rec);
		}
	}
	
	/**
	 *  Check for an existing connection in progress.
	 */
	protected Future<Con>	getFConnection0(Map<String, Object> header)
	{
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		return fconnections.get(rec);
	}
	
	/**
	 *  Set an existing connection in progress.
	 */
	protected void	setFConnection(Map<String, Object> header, Future<Con> fcon)
	{
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		assert !fconnections.containsKey(rec);
		fconnections.put(rec, fcon);
	}

	/**
	 *  Get the target addresses for a message.
	 *  @param The message header.
	 *  @return	The addresses, if any.
	 */
	protected String[] getAddresses(Map<String, Object> header)
	{
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		TransportAddressBook	book	= (TransportAddressBook)PlatformConfiguration
			.getPlatformValue(agent.getComponentIdentifier(), PlatformConfiguration.DATA_ADDRESSBOOK);
		return book.getPlatformAddresses(rec, getProtocolName());
	}
}
