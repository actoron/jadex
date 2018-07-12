package jadex.micro.examples.lottery;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@RequiredServices(@RequiredService(name="ls", type=ILotteryService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL)))
public class PlayerAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentBody
	public void body()
	{
//		final ILotteryService ls = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ILotteryService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		final ILotteryService ls = (ILotteryService)SFuture.getFirstResultAndTerminate(
			agent.getFeature(IRequiredServicesFeature.class).addQuery("ls"));
		
//		ILotteryService ls = SServiceProvider.waitForService(agent, new IResultCommand<IFuture<ILotteryService>, Void>()
//		{
//			public IFuture<ILotteryService> execute(Void args)
//			{
//				return agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ILotteryService.class, RequiredServiceInfo.SCOPE_GLOBAL));
//			}
//		}, 10, 3000).get();
		
		ITerminableIntermediateFuture<String> sub = ls.subscribeToLottery();
		
		sub.addIntermediateResultListener(new IIntermediateResultListener<String>()
		{
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Exception: "+exception);
			}
			
			public void resultAvailable(Collection<String> result)
			{
				System.out.println("Lottery ended.");
			}
			
			public void intermediateResultAvailable(String item)
			{
//				System.out.println("Item offered: "+item);
				
				System.out.println(agent.getId()+": "+(ls.claimItem(item).get()? "I won item: ": "I did not win item: ")+item);
			}
			
			public void finished()
			{
				System.out.println("Lottery ended.");
			}
		});
	}
}
