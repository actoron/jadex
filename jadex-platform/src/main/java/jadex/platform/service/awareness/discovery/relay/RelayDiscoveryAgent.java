package jadex.platform.service.awareness.discovery.relay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.TimeoutResultListener;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.ReceiveHandler;
import jadex.platform.service.awareness.discovery.SendHandler;
import jadex.platform.service.message.transport.httprelaymtp.SRelay;


/**
 * Control the discovery mechanism of the relay transport.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IRelayAwarenessService.class,
	implementation=@Implementation(expression="$pojoagent")))
@Service
@Properties(@NameValue(name="system", value="true"))
public class RelayDiscoveryAgent extends DiscoveryAgent	implements IRelayAwarenessService
{
	//-------- agent methods --------
	
	/**
	 *  After starting, perform initial registration at server.
	 */
	@AgentBody
	public void executeBody()
	{
		super.executeBody();

		sendInfo(false);
	}
	
	/**
	 *  Deregister when agent is killed.
	 */
	@AgentKilled
	public IFuture<Void>	agentKilled()
	{
		// Only wait 5 seconds for disconnect message.
		Future<Void>	ret	= new Future<Void>();
		
		sendInfo(true).addResultListener(new TimeoutResultListener<Void>(5000, agent.getExternalAccess(), new DelegationResultListener<Void>(ret)
		{
			public void exceptionOccurred(Exception exception)
			{
				// Ignore exception.
				super.resultAvailable(null);
			}
		}));
		return ret;
	}
	
	//-------- service methods --------
	
	/**
	 *  Let the awareness know that the transport connected to an address.
	 *  @param address	The relay address.
	 */
	public IFuture<Void>	connected(String address)
	{
		agent.getLogger().info("Awareness connected: "+address);
		sendInfo(false);
		return IFuture.DONE;
	}

	/**
	 *  Let the awareness know that the transport was disconnected from an address.
	 *  @param address	The relay address.
	 */
	public IFuture<Void>	disconnected(String address)
	{
		agent.getLogger().info("Awareness disconnected: "+address);
		// Todo: remove discovery infos received from awareness
		
		final Future<Void> ret = new Future<Void>();
		
		// Announce virtual offline info. 
		IFuture<IAwarenessManagementService>	msfut	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("management");
		msfut.addResultListener(new ExceptionDelegationResultListener<IAwarenessManagementService, Void>(ret)
		{
			public void customResultAvailable(final IAwarenessManagementService ms)
			{
				createAwarenessInfo(AwarenessInfo.STATE_ALLOFFLINE, createMasterId())
					.addResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(ret)
				{
					public void customResultAvailable(AwarenessInfo info)
					{
						ms.addAwarenessInfo(info).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
						{
							public void customResultAvailable(Boolean result)
							{
								ret.setResult(null);
							}
						});
					}
				});
			}
		});
		
		return ret;		
	
//		return IFuture.DONE;
	}

	//-------- internal methods --------
	
	/**
	 *  Send an awareness info.
	 */
	protected IFuture<Void>	sendInfo(final boolean offline)
	{
		IFuture<Void>	ret;
		if(offline || isStarted())
		{
			agent.getLogger().info("Sending awareness info to server...");
			final Future<Void>	fut	= new Future<Void>();
			IFuture<IMessageService>	msfut =	agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("ms");
			msfut.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(fut)
			{
				public void customResultAvailable(final IMessageService ms)
				{
					ms.getAddresses().addResultListener(new ExceptionDelegationResultListener<String[], Void>(fut)
					{
						public void customResultAvailable(String[] addresses)
						{
							// Send init message to all connected relay servers.
							final List<IComponentIdentifier> receivers = new ArrayList<IComponentIdentifier>();
							for(int i=0; i<addresses.length; i++)
							{
								for(int j=0; j<SRelay.ADDRESS_SCHEMES.length; j++)
								{
									if(addresses[i].startsWith(SRelay.ADDRESS_SCHEMES[j]))
									{
										receivers.add(new ComponentIdentifier("__relay"+i,
											new String[]{addresses[i].endsWith("/") ? addresses[i]+"awareness" : addresses[i]+"/awareness"}));
									}
								}
							}
							
							if(!receivers.isEmpty())
							{
								createAwarenessInfo().addResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(fut)
								{
									public void customResultAvailable(final AwarenessInfo awainfo)
									{
										awainfo.setDelay(-1);	// no delay required
										if(offline)
											awainfo.setState(AwarenessInfo.STATE_OFFLINE);
										
										final Map<String, Object> msg = new HashMap<String, Object>();
										msg.put(SFipa.FIPA_MESSAGE_TYPE.getReceiverIdentifier(), receivers);
										msg.put(SFipa.CONTENT, awainfo);
										// Send content object without inner codec.
										msg.put(SFipa.LANGUAGE, SFipa.JADEX_RAW);
										agent.getComponentFeature(IMessageFeature.class).sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE)
											.addResultListener(new DelegationResultListener<Void>(fut));
									}
								});
							}
							else
							{
								fut.setException(new RuntimeException("No relay addresses found."));
//								{
//									public void printStackTrace()
//									{
//										super.printStackTrace();
//									}
//								});
							}
						}
					});
				}
			});
			
			fut.addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					agent.getLogger().info("Sending awareness info to server...success");
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(!(exception instanceof ComponentTerminatedException))
					{
//						exception.printStackTrace();
						agent.getLogger().info("Sending awareness info to server...failed: "+exception);
					}
				}
			});
			
			ret	= fut;
		}
		else
		{
			ret	= IFuture.DONE;
		}
		
		return ret;
	}
	
	//-------- template methods (nop for relay) --------
	
	/**
	 * Create the send handler.
	 */
	public SendHandler createSendHandler()
	{
		return null;
	}

	/**
	 * Create the receive handler.
	 */
	public ReceiveHandler createReceiveHandler()
	{
		return null;
	}

	/**
	 * (Re)init sending/receiving ressource.
	 */
	protected void initNetworkRessource()
	{
	}

	/**
	 * Terminate sending/receiving ressource.
	 */
	protected void terminateNetworkRessource()
	{
	}
	
	//-------- setter methods --------
	
	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes)
	{
		super.setIncludes(includes);
		sendInfo(false);
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
		super.setExcludes(excludes);
		sendInfo(false);
	}
	
	/**
	 *  Republish the awareness info.
	 *  Called when some important property has changed, e.g. platform addresses.
	 */
	public void republish()
	{
		sendInfo(false);
	}

}