package jadex.micro.testcases.servicevalue;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * 
 */
@Agent(autoprovide=true)
public class NewsProviderAgent implements INewsService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The subscriptions. */
	protected List<SubscriptionIntermediateFuture<String>> subscriptions = new ArrayList<SubscriptionIntermediateFuture<String>>();
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		for(int i=0; i<100; i++)
		{
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(2000).get();
			for(SubscriptionIntermediateFuture<String> sub: subscriptions)
			{
				if(!sub.addIntermediateResultIfUndone("News "+i))
				{
					subscriptions.remove(sub);
				}
			}
		}
	}
	
	/**
	 *  Subscribe to the newsprovider.
	 */
	public ISubscriptionIntermediateFuture<String> subscribeToNews()
	{
		final SubscriptionIntermediateFuture<String> ret = (SubscriptionIntermediateFuture<String>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent.getExternalAccess());
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				subscriptions.remove(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		subscriptions.add(ret);
		
		return ret;
	}
}
