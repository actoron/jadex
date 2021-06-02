package jadex.micro.testcases.maxresults;

import java.util.Random;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.PullIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent has methods withd different return futures and uses setMaxResultCount().
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITestService.class))
public class ProviderAgent implements ITestService
{
	@Agent
	protected IInternalAccess agent;
	
	@Override
	public IIntermediateFuture<String> getInfos() 
	{
		IntermediateFuture<String> ret = new IntermediateFuture<String>();
		
		Random rn = new Random();
		int max = Math.abs(rn.nextInt() % 100) + 1;

		ret.setMaxResultCount(max);
		
		for(int i=0; i<max; i++)
			ret.addIntermediateResult("A");
		
		return ret;
	}
	
	@Override
	public IPullIntermediateFuture<String> pullInfos() 
	{
		PullIntermediateFuture<String> ret = new PullIntermediateFuture<String>(pull -> pull.addIntermediateResult("A"));
		
		Random rn = new Random();
		int max = Math.abs(rn.nextInt() % 100) + 1;

		ret.setMaxResultCount(max);
		
		return ret;
	}
	
	@Override
	public ISubscriptionIntermediateFuture<String> subscribeToInfos() 
	{
		SubscriptionIntermediateFuture<String> ret = new SubscriptionIntermediateFuture<String>();
		
		Random rn = new Random();
		int max = Math.abs(rn.nextInt() % 100) + 1;

		ret.setMaxResultCount(max);
		
		for(int i=0; i<max; i++)
			ret.addIntermediateResult("A");
		
		return ret;
	}
}
