package jadex.micro.examples.lottery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;

@Agent(autoprovide=Boolean3.TRUE)
@Service
public class LotteryAgent implements ILotteryService
{
	@Agent
	protected IInternalAccess agent;
	
	protected ArrayList<String> openitems = new ArrayList<String>();
	
	protected Collection<SubscriptionIntermediateFuture<String>> subscriptions = new ArrayList<SubscriptionIntermediateFuture<String>>();
	
	@AgentBody
	public void body()
	{
		Random r = new Random();
		while(true)
		{
			int delay = r.nextInt(5)*1000;
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay).get();
			
			String item = SUtil.createUniqueId("Item");
			
			System.out.println("Offering new item: "+item);
			openitems.add(item);
			
			for(SubscriptionIntermediateFuture<String> sub: subscriptions)
			{
				sub.addIntermediateResultIfUndone(item);
//				if(!sub.addIntermediateResultIfUndone(item))
//					subscriptions.remove(sub);
			}
		}
	}
	
	@AgentKilled
	public void killed()
	{
		for(TerminableIntermediateFuture<String> sub: subscriptions)
		{
			sub.setFinishedIfUndone();
		}
	}
	
	public ISubscriptionIntermediateFuture<String> subscribeToLottery()
	{
		final SubscriptionIntermediateFuture<String> ret = new SubscriptionIntermediateFuture<String>();
//		final TerminableIntermediateFuture<String> ret = (TerminableIntermediateFuture<String>)SFuture.getNoTimeoutFuture(TerminableIntermediateFuture.class, agent);
	
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				subscriptions.remove(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		
		subscriptions.add(ret);
		
		return ret;
	}
	
	public IFuture<Boolean> claimItem(String item)
	{
		Future<Boolean> ret = new Future<Boolean>();
		ret.setResult(openitems.remove(item));
		return ret;
	}
}
