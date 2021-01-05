package jadex.micro.testcases.maxresults;

import java.util.Random;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
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
		int max = Math.abs(rn.nextInt() % 100);

		ret.setMaxResultCount(max);
		
		for(int i=0; i<max; i++)
			ret.addIntermediateResult("A");
		
		return ret;
	}
	
	@Override
	public ISubscriptionIntermediateFuture<String> subscribeToInfos() 
	{
		SubscriptionIntermediateFuture<String> ret = new SubscriptionIntermediateFuture<String>();
		
		Random rn = new Random();
		int max = Math.abs(rn.nextInt() % 100);

		ret.setMaxResultCount(max);
		
		for(int i=0; i<max; i++)
			ret.addIntermediateResult("A");
		
		return ret;
	}
}
