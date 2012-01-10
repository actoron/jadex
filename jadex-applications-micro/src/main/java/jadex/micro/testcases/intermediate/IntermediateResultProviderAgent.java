package jadex.micro.testcases.intermediate;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides a service with intermediate results.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IIntermediateResultService.class, implementation=@Implementation(expression="$pojoagent")))
public class IntermediateResultProviderAgent implements IIntermediateResultService
{
	@Agent
	protected MicroAgent agent;
	
	protected int cnt;
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IIntermediateFuture<String> getResults(final long delay, final int max)
	{
		final IntermediateFuture<String> ret = new IntermediateFuture<String>();

		cnt = 0;
//		final int max = 5;
//		final long delay = 200;
		
//		System.out.println("start: "+System.currentTimeMillis());
		agent.waitFor(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("setting intermediate result: "+cnt);//+" - "+System.currentTimeMillis());
				ret.addIntermediateResult("step("+(cnt++)+"/"+max+")");
				if(cnt==max)
				{
					ret.setFinished();
				}
				else
				{
					agent.waitFor(delay, this);
				}
				return null;
			}
		});
		
		return ret;
	}
}
