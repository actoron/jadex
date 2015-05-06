package jadex.bdiv3x.runtime;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;
import jadex.commons.future.Future;
import jadex.micro.AbstractMessageHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 *  Dummy class for loading v2 examples using v3x.
 */
public abstract class Plan
{
	/** The internal access. */
	protected IInternalAccess agent;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public abstract void body();

	/**
	 *  The passed method is called on plan success.
	 */
	public void	passed()
	{
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
	}
	
	/**
	 *  Wait for a some time.
	 *  @param duration The duration.
	 */
	public void	waitFor(int timeout)
	{
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(timeout).get();
	}
	
	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 */
	public IMessageEvent waitForMessageEvent(String type)
	{
		return waitForMessageEvent(type, -1);
	}

	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 *  @param timeout The timeout.
	 */
	public IMessageEvent waitForMessageEvent(String type, long timeout)
	{
		final Future<IMessageEvent> ret = new Future<IMessageEvent>();
		
		IMessageFeature mf = agent.getComponentFeature(IMessageFeature.class);
		mf.addMessageHandler(new AbstractMessageHandler(new IFilter<IMessageAdapter>()
		{
			public boolean filter(IMessageAdapter obj)
			{
//				IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class);
//				List<MMessageEvent> mevents = bdif.getBDIModel().getCapability().getMessageEvents();
//				for(MMessageEvent mevent: mevents)
//				{
//					if(mevent.getDirection())
//				}
				return true;
			}
		}, timeout, true, true)
		{
			public void handleMessage(final Map<String, Object> msg, final MessageType type)
			{
//				System.out.println("received reply: "+msg);
				ret.setResult(new RMessageEvent(null, msg, type));
			}
			
			public void timeoutOccurred()
			{
				ret.setException(new TimeoutException());
			}
		});
		
		return ret.get();
	}
	
	/**
	 *  Kill this agent.
	 */
	public void	killAgent()
	{
		agent.killComponent();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return agent.getLogger();
	}
	
	/**
	 *  Get the beliefbase.
	 *  @return The beliefbase.
	 */
	public IBeliefbase getBeliefbase()
	{
		return null;
	}
}
