package jadex.micro.testcases.blocking;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  An agent that provides the stepped service.
 */
@Agent(predecessors="jadex.micro.testcases.blocking.BlockAgent")
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
	@AgentServiceSearch
	protected IBlockService	block;
	
	//-------- IIntermediateBlockingService interface --------
	
	/**
	 *  Perform some steps and block some milliseconds in between.
	 */
	public IIntermediateFuture<Integer>	performSteps(final int steps, final long millis)
	{
//		System.out.println("Perform steps called: "+agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class)).getTime());
		final IntermediateFuture<Integer>	ret	= new IntermediateFuture<Integer>();
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(0, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				for(int i=1; i<=steps; i++)
				{
//					System.out.println("Perform steps before wait for step "+i+": "+agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class)).getTime());
					ia.getFeature(IExecutionFeature.class).waitForDelay(millis).get();
					ret.addIntermediateResult(Integer.valueOf(i));
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
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(0, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				for(int i=1; !ret.isDone(); i++)
				{
					ia.getFeature(IExecutionFeature.class).waitForDelay(millis).get();
					ret.addIntermediateResult(Integer.valueOf(i));
				}
				return IFuture.DONE;
			}
		});
		
		return ret;		
	}
}
