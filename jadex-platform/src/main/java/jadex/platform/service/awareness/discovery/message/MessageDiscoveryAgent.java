package jadex.platform.service.awareness.discovery.message;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.AbstractMessageHandler;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.ReceiveHandler;
import jadex.platform.service.awareness.discovery.SendHandler;


/**
 *  Discovery agent that is based on message receipt.
 *  
 *  The message service announces the sender of each incoming
 *  message to the IMessageAwarenessService of this agent.
 *  
 *  The agent will announce the underlying platform and uses
 *  ping messages to its rms to check if it is still present.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IMessageAwarenessService.class,
	implementation=@Implementation(expression="$pojoagent")))
@RequiredServices(@RequiredService(name="awa", type=IAwarenessManagementService.class))
@Service
//@Properties(@NameValue(name="system", value="true"))
public class MessageDiscoveryAgent extends DiscoveryAgent implements IMessageAwarenessService
{
	//-------- attributes --------
	
	/** The map of announced component identifiers. */
	protected Map<IComponentIdentifier, Long> announcements = new HashMap<IComponentIdentifier, Long>();
	
	//-------- methods --------
	
	/**
	 *  Init the agent.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void>	ret	= new Future<Void>();
		IFuture<IAwarenessManagementService>	awa	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("awa");
		awa.addResultListener(new ExceptionDelegationResultListener<IAwarenessManagementService, Void>(ret)
		{
			public void customResultAvailable(IAwarenessManagementService awa)
			{
				awa.subscribeToPlatformList(true).addResultListener(new IntermediateDefaultResultListener<DiscoveryInfo>()
				{
					public void intermediateResultAvailable(DiscoveryInfo di)
					{
//						System.out.println("discovery info: "+di);
						
						if(di.isAlive())
						{
							long	time	= di.getDelay()==-1 ? -1 : di.getTime()+di.getDelay();
							refreshComponentIdentifier(di.getComponentIdentifier(), time);
						}

						// If platform removed, force task to ping (and probably fail) on next execution.
						else if(announcements.containsKey(di.getComponentIdentifier()))
						{
//							System.out.println("set to null: "+di.getComponentIdentifier());
							announcements.put(di.getComponentIdentifier(), Long.valueOf(0));
						}
					}
					public void exceptionOccurred(Exception exception)
					{
					}
				});
				MessageDiscoveryAgent.super.agentCreated()
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	//-------- service methods --------
	
	/**
	 *  Announce a potentially new component identifier.
	 *  @param platform The component identifier.
	 */
	public IFuture<Void> announceComponentIdentifier(ITransportComponentIdentifier ccid)
	{
		// Only handle platforms
		ITransportComponentIdentifier cid = (ITransportComponentIdentifier)ccid.getRoot();
		
		// Ignore self messages
		if(!getMicroAgent().getComponentIdentifier().getRoot().equals(cid))
		{
			refreshComponentIdentifier(cid, System.currentTimeMillis());
		}
		
		return IFuture.DONE;		
	}
	
	/**
	 *  Refresh a component identifier.
	 *  @param cid The component identifier.
	 */
	public void refreshComponentIdentifier(ITransportComponentIdentifier cid, long time)
	{
		// Do not start another check if already contained
		boolean contained	= announcements.containsKey(cid);
		
		// Update time
		long	old	= announcements.containsKey(cid) ? announcements.get(cid).longValue() : Long.MIN_VALUE;
		if(old!=-1 && (time>old || time==-1))
		{
//			System.out.println("set to time: "+cid+", "+time);
			announcements.put(cid, Long.valueOf(time));
		}

		if(!contained)
		{
//			System.out.println("enter: "+cid);
			performAnnouncements(cid);
		}
	}
	
	//-------- internal methods --------
	
	/**
	 *  Perform continuous announcements until no ping answers are received any longer.
	 */
	protected void performAnnouncements(final ITransportComponentIdentifier cid)
	{
		AwarenessInfo info = new AwarenessInfo((ITransportComponentIdentifier)cid.getRoot(), AwarenessInfo.STATE_ONLINE, getDelay(), 
			null, null, null, SReflect.getInnerClassName(this.getClass()));
		announceAwareness(info);
		
		// Check alive via sending ping message before delay is due
		final long del = (long)(getDelay()-((double)getDelay())*0.1);
		refreshComponentIdentifier(cid, System.currentTimeMillis() + del);
		
		doWaitFor(del, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				long	valid	= announcements.get(cid).longValue();
				if(valid!=-1 && valid<System.currentTimeMillis())
				{
//					System.out.println("pinging: "+cid);
					Map<String, Object> msg = new HashMap<String, Object>();
					ComponentIdentifier rec = new ComponentIdentifier("rms@"+cid.getPlatformName(), cid.getAddresses());
					msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rec});
					msg.put(SFipa.CONTENT, "ping");
					msg.put(SFipa.PERFORMATIVE, SFipa.QUERY_IF);
					msg.put(SFipa.CONVERSATION_ID, SUtil.createUniqueId("msg_dis"));
					getMicroAgent().getComponentFeature(IMessageFeature.class).sendMessageAndWait(msg, SFipa.FIPA_MESSAGE_TYPE, new AbstractMessageHandler(null, 5000, true, true)
					{
						public void handleMessage(Map<String, Object> msg, MessageType type)
						{
//							System.out.println("received reply: "+msg);
							performAnnouncements(cid);
						}
						
						public void timeoutOccurred()
						{
//							System.out.println("Received no ping reply, removed: "+cid);
							announcements.remove(cid);
						}
					});
				}
				else
				{
//					System.out.println("wait: "+cid+", "+(valid-System.currentTimeMillis()));
					performAnnouncements(cid);					
				}
				
				return IFuture.DONE;
			}
		});
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
	
	/**
	 *  Announce newly arrived awareness info to management service.
	 */
	public void announceAwareness(final AwarenessInfo info)
	{
//		System.out.println("announcing: "+info);
		
		if(info.getSender()!=null)
		{
			if(info.getSender().equals(getRoot()))
				received_self	= true;
			
//			System.out.println(System.currentTimeMillis()+" "+getMicroAgent().getComponentIdentifier()+" received: "+info.getSender());
			
			IFuture<IAwarenessManagementService>	msfut	= getMicroAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("management");
			msfut.addResultListener(new DefaultResultListener<IAwarenessManagementService>(getMicroAgent().getLogger())
			{
				public void resultAvailable(IAwarenessManagementService ms)
				{
					ms.addAwarenessInfo(info).addResultListener(new DefaultResultListener<Boolean>(getMicroAgent().getLogger())
					{
						public void resultAvailable(Boolean result)
						{
							// nothing to do
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(!(exception instanceof ComponentTerminatedException))
								super.exceptionOccurred(exception);
						}
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(!(exception instanceof ComponentTerminatedException))
						super.exceptionOccurred(exception);
				}
			});
		}
	}
	
	
}