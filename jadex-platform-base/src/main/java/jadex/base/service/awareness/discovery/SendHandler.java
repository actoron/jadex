package jadex.base.service.awareness.discovery;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
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
	protected DiscoveryState state;

	/** The root component identifier. */
	protected IComponentIdentifier root;
	
	/** The timer. */
	protected Timer	timer;

	/** The current send id. */
	protected String sendid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public SendHandler(DiscoveryState state)
	{
		this.state = state;
		this.root = state.getExternalAccess().getComponentIdentifier().getRoot();
		startSendBehavior();
	}
	
	//-------- methods --------
	
	/**
	 *  Start sending awareness infos.
	 *  (Ends automatically when a new send behaviour is started).
	 */
	public void startSendBehavior()
	{
		if(state.isStarted())
		{
			final String sendid = SUtil.createUniqueId(state.getExternalAccess().getComponentIdentifier().getLocalName());
			this.sendid = sendid;	
			
			state.getExternalAccess().scheduleStep(new IComponentStep()
			{
				@XMLClassname("send")
				public Object execute(IInternalAccess ia)
				{
					if(!state.isKilled() && sendid.equals(getSendId()))
					{
//						System.out.println(System.currentTimeMillis()+" sending: "+getComponentIdentifier());
						send(createAwarenessInfo());
						
						if(state.getDelay()>0)
							state.doWaitFor(state.getDelay(), this);
					}
					return null;
				}
			});
		}
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
		return state.createAwarenessInfo(AwarenessInfo.STATE_ONLINE, false);
	}
	
	/**
	 *  Method to send messages.
	 */
	public abstract void send(AwarenessInfo info);

}
