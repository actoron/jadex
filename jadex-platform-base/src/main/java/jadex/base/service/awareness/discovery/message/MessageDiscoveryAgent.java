package jadex.base.service.awareness.discovery.message;

import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.ReceiveHandler;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.base.service.message.MessageService.SendManager;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.AbstractMessageHandler;
import jadex.micro.IMessageHandler;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.HashMap;
import java.util.Map;


/**
 */
@Agent
@ProvidedServices(@ProvidedService(type=IMessageAwarenessService.class,
	implementation=@Implementation(expression="$component.getPojoAgent()")))
@Service
public class MessageDiscoveryAgent extends DiscoveryAgent implements IMessageAwarenessService
{
	//-------- attributes --------
	
	/** The map of announced component identifiers. */
	protected Map<IComponentIdentifier, Long> announcements = new HashMap<IComponentIdentifier, Long>();
	
	//-------- service methods --------
	
	/**
	 *  Announce a potentially new component identifier.
	 *  @param cid The component identifier.
	 */
	public IFuture<Void> announceComponentIdentifier(final IComponentIdentifier cid)
	{
		performAnnouncements(cid);
		
		return IFuture.DONE;
	}
	
	//-------- internal methods --------
	
	/**
	 *  Perform continuous announcements until no ping answers are received any longer.
	 */
	protected void performAnnouncements(final IComponentIdentifier cid)
	{
		AwarenessInfo info = new AwarenessInfo(cid.getRoot(), AwarenessInfo.STATE_ONLINE, getDelay(), null, null, null);
		announcements.put(cid, System.currentTimeMillis());
		announceAwareness(info);
		
		// Check alive via sending ping message before delay is due
		doWaitFor((long)(getDelay()-getDelay()*0.1), new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Map<String, Object> msg = new HashMap<String, Object>();
				ComponentIdentifier rec = new ComponentIdentifier("rms"+cid.getPlatformName(), cid.getAddresses());
				msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rec});
				msg.put(SFipa.CONTENT, "ping");
				msg.put(SFipa.PERFORMATIVE, SFipa.QUERY_IF);
				msg.put(SFipa.CONVERSATION_ID, SUtil.createUniqueId("msg_dis"));
				getMicroAgent().sendMessageAndWait(msg, SFipa.FIPA_MESSAGE_TYPE, new AbstractMessageHandler(5000, true)
				{
					public void handleMessage(Map msg, MessageType type)
					{
						performAnnouncements(cid);
					}
					
					public void timeoutOccurred()
					{
						System.out.println("Received no ping reply, removed: "+cid);
						announcements.remove(cid);
					}
				});
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
			
//			System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());
			
			IFuture<IAwarenessManagementService>	msfut	= getMicroAgent().getRequiredService("management");
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