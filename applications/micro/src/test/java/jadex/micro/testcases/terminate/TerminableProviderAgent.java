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
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides a service with terminable futures.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITerminableService.class))
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
		System.out.println(agent.getId()+": getResult1 "+delay);
		final TerminableFuture<String> ret = new TerminableFuture<String>(new TerminationTestCommand());

//		agent.getFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				System.out.println(agent.getId()+": getResult2");
//				ret.setResultIfUndone("result");
//				return null;
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Get three results (one initial, one after half of the time has passed and one directly before finished).
	 *  @param delay The delay that is waited before the future is set to finished.
	 *  @return The results.
	 */
	public ITerminableIntermediateFuture<String> getResults(long delay)
	{
		final TerminableIntermediateFuture<String> ret = new TerminableIntermediateFuture<String>(new TerminationTestCommand());
		
		System.out.println("getResults invoked, waiting 2x for "+delay/2);
		new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				int	cnt	= ret.getIntermediateResults().size()+1;
				String	result	= "step "+cnt+" of 3";
				System.out.println("adding: "+result);
				if(ret.addIntermediateResultIfUndone(result))
				{
					if(cnt==3)
					{
//						ret.setFinished();
					}
					else
					{
						// emit intermediate results after half of delay time.
						agent.getFeature(IExecutionFeature.class).waitForDelay(delay/2, this);
					}
				}
				return IFuture.DONE;
			}
		}.execute(agent);
		
		return ret;
	}
	
	/**
	 *  Be informed when one of the other methods futures is terminated.
	 *  Returns an initial result when this future is registered.
	 *  Is finished, when the terminate action of the other future was called.
	 */
	public IIntermediateFuture<Void>	isTerminateCalled()
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
	
	//-------- helper classes --------
	
	/**
	 *  Check that future termination is correctly announced.
	 */
	class TerminationTestCommand extends TerminationCommand
	{
		public void terminated(Exception reason)
		{
			System.out.println(agent.getId()+": getResult2 "+termfut);
			if(termfut!=null)
			{
				if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
				{
					System.err.println("adapter0: "+agent.getId());
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
	}
}

