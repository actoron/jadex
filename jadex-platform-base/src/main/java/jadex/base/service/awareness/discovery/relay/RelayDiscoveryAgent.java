package jadex.base.service.awareness.discovery.relay;

import jadex.base.fipa.SFipa;
import jadex.base.service.message.transport.codecs.JadexXMLCodec;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Control the discovery mechanism of the relay transport.
 */
@Agent
@RequiredServices(@RequiredService(name=RelayDiscoveryAgent.MS, type=IMessageService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class RelayDiscoveryAgent
{
	//-------- constants --------
	
	/** The message service name. */
	
	public static final String	MS	= "ms";
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent	agent;
	
	//-------- methods --------
	
	/**
	 *  Called when the agent is started.
	 */
	@AgentCreated
	public IFuture<Void>	startAgent()
	{
		final Future<Void>	ret	= new Future<Void>();
		IFuture<IMessageService>	fut	= agent.getServiceContainer().getRequiredService(MS);
		fut.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(final IMessageService ms)
			{
				ms.getAddresses().addResultListener(new ExceptionDelegationResultListener<String[], Void>(ret)
				{
					public void customResultAvailable(String[] addresses)
					{
						// Send init message to all connected relay servers.
						List<IComponentIdentifier>	receivers	= new ArrayList<IComponentIdentifier>();
						for(int i=0; i<addresses.length; i++)
						{
							if(addresses[i].startsWith(SRelay.ADDRESS_SCHEME))
							{
								receivers.add(new ComponentIdentifier("__relay"+i, new String[]{addresses[i].endsWith("/") ? addresses[i]+"awareness" : addresses[i]+"/awareness"}));
							}
						}
						Map<String, Object>	msg	= new HashMap<String, Object>();
						msg.put(SFipa.FIPA_MESSAGE_TYPE.getReceiverIdentifier(), receivers);
						ms.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, agent.getComponentIdentifier(), agent.getModel().getResourceIdentifier(),
							new byte[]{JadexXMLCodec.CODEC_ID})	// Use fixed codec to avoid complex codec handling in servlet
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		return ret;
	}
}
