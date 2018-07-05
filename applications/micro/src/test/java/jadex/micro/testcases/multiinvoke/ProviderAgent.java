package jadex.micro.testcases.multiinvoke;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=IExampleService.class, 
	implementation=@Implementation(expression="$pojoagent")))
//@Results(@Result(name="testcases", clazz=List.class))
@Service(IExampleService.class)
@Agent
public class ProviderAgent implements IExampleService
{
	@ServiceComponent
	protected IInternalAccess agent;
	
	/**
	 *  Get an item.
	 */
	public IFuture<String> getItem()
	{
		return new Future<String>("item: "+agent.getIdentifier().getName());
	}
	
	/**
	 *  Get the items.
	 */
	public IIntermediateFuture<String> getItems(final int num)
	{
		final IntermediateFuture<String> ret = new IntermediateFuture<String>();

		final int cnt[] = new int[1];
		final long delay = 1000;
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(cnt[0]++<num)
				{
					ret.addIntermediateResult("item: "+agent.getIdentifier().getName()+" "+cnt[0]);
					agent.getFeature(IExecutionFeature.class).waitForDelay(delay, this);	
				}
				else
				{
					ret.setFinished();
				}
				return IFuture.DONE;
			}
		};
		agent.getFeature(IExecutionFeature.class).waitForDelay(delay, step);
		
		return ret;
	}
	
	/**
	 *  Add two ints.
	 */
	public IFuture<Integer> add(int a, int b)
	{
		return new Future<Integer>(Integer.valueOf(a+b));
	}
}
