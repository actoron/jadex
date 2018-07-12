package jadex.platform.service.awareness.discovery;

import java.util.Timer;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

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
		final String sendid = SUtil.createUniqueId(agent.getMicroAgent().getId().getLocalName());
		this.sendid = sendid;	
		
		agent.getMicroAgent().getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			@Classname("send")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> step = this;
				final Future<Void> ret = new Future<Void>();
				if(!agent.isKilled() && sendid.equals(getSendId()))
				{
					createAwarenessInfo().addResultListener(agent.getMicroAgent().getFeature(IExecutionFeature.class)
						.createResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(ret)
					{
						public void customResultAvailable(AwarenessInfo info)
						{
							send(info);
							if(agent.getDelay()>0)
								agent.doWaitFor(agent.getDelay(), step);
							ret.setResult(null);
						}
					}));
					
				}
				return ret;
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
	public IFuture<AwarenessInfo> createAwarenessInfo()
	{
		return agent.createAwarenessInfo();
	}
	
	/**
	 *  Method to send messages.
	 */
	public abstract void send(AwarenessInfo info);

}
