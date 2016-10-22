package jadex.micro.examples.lottery;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IResultCommand;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class PlayerAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentBody
	public void body()
	{
		final ILotteryService ls = SServiceProvider.getService(agent, ILotteryService.class, RequiredServiceInfo.SCOPE_GLOBAL).get();
		
//		ILotteryService ls = SServiceProvider.waitForService(agent, new IResultCommand<IFuture<ILotteryService>, Void>()
//		{
//			public IFuture<ILotteryService> execute(Void args)
//			{
//				return SServiceProvider.getService(agent, ILotteryService.class, RequiredServiceInfo.SCOPE_GLOBAL);
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
				
				System.out.println(agent.getComponentIdentifier()+": "+(ls.claimItem(item).get()? "I won item: ": "I did not win item: ")+item);
			}
			
			public void finished()
			{
				System.out.println("Lottery ended.");
			}
		});
	}
}
