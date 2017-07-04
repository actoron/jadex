package jadex.micro.testcases.terminate;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;
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
	protected IInternalAccess agent;
	
	/** A future to indicate that termination was called successfully. */
	protected IntermediateFuture<Void>	termfut;	
	
	//-------- constructors ---------

	/**
	 *  Get the result.
	 *  @param delay The delay that is waited before the result is retured.
	 *  @return The result.
	 */
	public ITerminableFuture<String> getResult(long delay)
	{
		System.out.println(agent.getComponentIdentifier()+": getResult1 "+delay);
		final TerminableFuture<String> ret = new TerminableFuture<String>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				System.out.println(agent.getComponentIdentifier()+": getResult2 "+termfut);
				if(termfut!=null)
				{
					if(!agent.getComponentFeature(IExecutionFeature.class).isComponentThread())
					{
						System.err.println("adapter0: "+agent.getComponentIdentifier());
						System.err.println("adapter0a: "+IInternalExecutionFeature.LOCAL.get());
						Thread.dumpStack();
						termfut.setException(new RuntimeException("Terminate called on wrong thread."));
					}
					else
					{
						termfut.setFinished();
					}
					termfut	= null;
				}
			}
		});

		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println(agent.getComponentIdentifier()+": getResult3");
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
		System.out.println("getResults");
		final TerminableIntermediateFuture<String> ret = new TerminableIntermediateFuture<String>(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				System.out.println("termination command called2: "+termfut);
				if(termfut!=null)
				{
					if(!agent.getComponentFeature(IExecutionFeature.class).isComponentThread())
					{
						System.err.println("adapter1: "+agent.getComponentIdentifier());
						System.err.println("adapter1a: "+IInternalExecutionFeature.LOCAL.get());
						Thread.dumpStack();
						termfut.setException(new RuntimeException("Terminate called on wrong thread."));
					}
					else
					{
						termfut.setFinished();
					}
					termfut	= null;
				}
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		final int[] cnt = new int[1];
		
//		System.out.println("getResult invoked");
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("setting intermediate result: "+cnt[0]);//+" - "+System.currentTimeMillis());
				if(ret.addIntermediateResultIfUndone("step("+(cnt[0]++)+"/"+max+")"))
				{
					if(cnt[0]==max)
					{
						ret.setFinished();
					}
					else
					{
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, this);
					}
				}
				return null;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Be informed when one of the other methods futures is terminated.
	 *  Returns an initial result when this future is registered.
	 *  Is finished, when the terminate action of the other future was called.
	 */
	public IIntermediateFuture<Void>	terminateCalled()
	{
		IntermediateFuture<Void>	ret	= new IntermediateFuture<Void>();
		if(termfut!=null)
		{
			ret.setException(new RuntimeException("Must not be called twice before result is available"));
		}
		else
		{
			ret.addIntermediateResult(null);
			termfut	= ret;
		}
		return ret;
	}
}

