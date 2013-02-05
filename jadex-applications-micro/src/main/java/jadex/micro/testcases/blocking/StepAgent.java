package jadex.micro.testcases.blocking;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  An agent that provides the stepped service.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IStepService.class,
	implementation=@Implementation(expression="$pojoagent")))
@RequiredServices(@RequiredService(name="block", type=IBlockService.class))
public class StepAgent	implements	IStepService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The blocking service. */
	@AgentService
	protected IBlockService	block;
	
	//-------- IIntermediateBlockingService interface --------
	
	/**
	 *  Perform some steps and block some milliseconds in between.
	 */
	public IIntermediateFuture<Integer>	performSteps(final int steps, final long millis)
	{
		final IntermediateFuture<Integer>	ret	= new IntermediateFuture<Integer>();
		
		agent.waitForDelay(0, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				for(int i=1; i<=steps; i++)
				{
					ia.waitForDelay(millis).get();
					ret.addIntermediateResult(new Integer(i));
				}
				ret.setFinished();
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Perform periodical steps and block some milliseconds in between.
	 */
	public ISubscriptionIntermediateFuture<Integer>	subscribeToSteps(final long millis)
	{
		final SubscriptionIntermediateFuture<Integer>	ret	= new SubscriptionIntermediateFuture<Integer>();
		
		agent.waitForDelay(0, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				for(int i=1; !ret.isDone(); i++)
				{
					ia.waitForDelay(millis).get();
					ret.addIntermediateResult(new Integer(i));
				}
				return IFuture.DONE;
			}
		});
		
		return ret;		
	}
}
