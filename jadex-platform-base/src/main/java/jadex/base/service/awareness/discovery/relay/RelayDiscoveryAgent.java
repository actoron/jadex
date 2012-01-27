package jadex.base.service.awareness.discovery.relay;

import jadex.base.fipa.SFipa;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.ReceiveHandler;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.base.service.message.transport.codecs.GZIPCodec;
import jadex.base.service.message.transport.codecs.JadexXMLCodec;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Control the discovery mechanism of the relay transport.
 */
@Agent
public class RelayDiscoveryAgent extends DiscoveryAgent
{
	//-------- attributes --------
	
	/** True, if currently sending an info. */
	protected boolean	sending;
	
	//-------- methods --------
	
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
	public IFuture<Void>	agentkilled()
	{
		return sendInfo(true);
	}
	
	/**
	 *  Send an awareness info.
	 */
	protected IFuture<Void>	sendInfo(final boolean offline)
	{
		IFuture<Void>	ret;
		if(offline || (!sending && isStarted()))
		{
			sending	= true;
			
			agent.getLogger().info("Sending awareness info to server...");
			final Future<Void>	fut	= new Future<Void>();
			IFuture<IMessageService>	msfut =	agent.getServiceContainer().getRequiredService("ms");
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
								if(addresses[i].startsWith(SRelay.ADDRESS_SCHEME))
								{
									receivers.add(new ComponentIdentifier("__relay"+i,
										new String[]{addresses[i].endsWith("/") ? addresses[i]+"awareness" : addresses[i]+"/awareness"}));
								}
							}
							
							createAwarenessInfo().addResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(fut)
							{
								public void customResultAvailable(final AwarenessInfo awainfo)
								{
									sending	= false;	// Subsequent updates trigger new send.
									awainfo.setDelay(-1);	// no delay required
									if(offline)
										awainfo.setState(AwarenessInfo.STATE_OFFLINE);
									
									Map<String, Object> msg = new HashMap<String, Object>();
									msg.put(SFipa.FIPA_MESSAGE_TYPE.getReceiverIdentifier(), receivers);
									msg.put(SFipa.CONTENT, awainfo);
									msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);	// Todo: remove need for nested codecs!?
									
									ms.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE,
										agent.getComponentIdentifier(), agent.getModel().getResourceIdentifier(),
										new byte[]{JadexXMLCodec.CODEC_ID, GZIPCodec.CODEC_ID}) // Use fixed codecs to avoid complex	codec handling in servlet
										.addResultListener(new DelegationResultListener<Void>(fut));
								}
							});
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
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					agent.getLogger().info("Sending awareness info to server...failed: "+exception);
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
}