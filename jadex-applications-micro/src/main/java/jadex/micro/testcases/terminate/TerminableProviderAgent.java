package jadex.micro.testcases.terminate;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides a service with terminable futures.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITerminableService.class, implementation=@Implementation(expression="$pojoagent")))
@Description("Agent that provides a service with terminable future results")
public class TerminableProviderAgent implements ITerminableService
{
	//-------- attributes ---------
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	//-------- constructors ---------

	/**
	 *  Get the result.
	 *  @param delay The delay that is waited before the result is retured.
	 *  @return The result.
	 */
	public ITerminableFuture<String> getResult(long delay)
	{
		final TerminableFuture<String> ret = new TerminableFuture<String>();

//		System.out.println("getResult invoked");
		agent.waitFor(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("getResult setResult");
				ret.setResultIfUndone("result");
				return null;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the result.
	 *  @param delay The delay that is waited before the result is returned.
	 *  @param max The number of produced intermediate results.
	 *  @return The result.
	 */
	public ITerminableIntermediateFuture<String> getResults(final long delay, final int max)
	{
//		System.out.println("getResults");
		final TerminableIntermediateFuture<String> ret = new TerminableIntermediateFuture<String>();
		final int[] cnt = new int[1];
		
//		System.out.println("getResult invoked");
		agent.waitFor(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("setting intermediate result: "+cnt[0]);//+" - "+System.currentTimeMillis());
				if(ret.addIntermediateResultIfUndone("step("+(cnt[0]++)+"/"+max+")"))
				{
					if(cnt[0]==max)
					{
						ret.setFinished();
					}
					else
					{
						agent.waitFor(delay, this);
					}
				}
				return null;
			}
		});
		
		return ret;
	}
}

