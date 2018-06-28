package jadex.micro.testcases.recfutures;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=IAService.class))
public class AAgent implements IAService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	public IFuture<IFuture<String>> methodA()
	{
		final Future<IFuture<String>> ret1 = new Future<IFuture<String>>();
		
		IComponentStep<Void> step1 = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<String> ret2 = new Future<String>();
				ret1.setResult(ret2);
				
				IComponentStep<Void> step2 = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret2.setResult("hello");
						return IFuture.DONE;
					}
				};
				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(100, step2);
				return IFuture.DONE;
			}
		};
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(100, step1);
		
		return ret1;
	}
	
	/**
	 *  Test method with intermediate future in future.
	 */
	public IFuture<IIntermediateFuture<String>> methodB()
	{
		final Future<IIntermediateFuture<String>> ret1 = new Future<IIntermediateFuture<String>>();
		
		IComponentStep<Void> step1 = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IntermediateFuture<String> ret2 = new IntermediateFuture<String>();
				ret1.setResult(ret2);
				
				final int[] cnt = new int[1];
				IComponentStep<Void> step2 = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret2.addIntermediateResult(""+cnt[0]++);
						if(cnt[0]<3)
						{
							agent.getComponentFeature(IExecutionFeature.class).waitForDelay(100, this);
						}
						else
						{
							ret2.setFinished();
						}
						return IFuture.DONE;
					}
				};
				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(100, step2);
				return IFuture.DONE;
			}
		};
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(100, step1);
		
		return ret1;
	}
}
