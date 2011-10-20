package jadex.base.service.awareness.discovery;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.xml.annotation.XMLClassname;

import java.util.Timer;

/**
 *  Automatically reinvokes send method in intervals
 *  determined by the delay (in state).
 *  
 *  Subclasses should override send to perform
 *  specific actions.
 */
public abstract class SendHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected DiscoveryAgent agent;

	/** The timer. */
	protected Timer	timer;

	/** The current send id. */
	protected String sendid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public SendHandler(DiscoveryAgent agent)
	{
		this.agent = agent;
//		startSendBehavior();
	}
	
	//-------- methods --------
	
	/**
	 *  Start sending awareness infos.
	 *  (Ends automatically when a new send behaviour is started).
	 */
	public void startSendBehavior()
	{
		final String sendid = SUtil.createUniqueId(agent.getMicroAgent().getComponentIdentifier().getLocalName());
		this.sendid = sendid;	
		
		agent.getMicroAgent().scheduleStep(new IComponentStep<Void>()
		{
			@XMLClassname("send")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!agent.isKilled() && sendid.equals(getSendId()))
				{
//						System.out.println(System.currentTimeMillis()+" sending: "+getComponentIdentifier());
					send(createAwarenessInfo());
					
					if(agent.getDelay()>0)
						agent.doWaitFor(agent.getDelay(), this);
				}
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Get the sendid.
	 *  @return the sendid.
	 */
	public String getSendId()
	{
		return sendid;
	}

	/**
	 *  Set the sendid.
	 *  @param sendid The sendid to set.
	 */
	public void setSendId(String sendid)
	{
		this.sendid = sendid;
	}
	
	/**
	 *  Create the awareness info.
	 */
	public AwarenessInfo createAwarenessInfo()
	{
		return agent.createAwarenessInfo();
	}
	
	/**
	 *  Method to send messages.
	 */
	public abstract void send(AwarenessInfo info);

}
