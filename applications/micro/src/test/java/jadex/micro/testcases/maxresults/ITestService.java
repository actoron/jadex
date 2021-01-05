package jadex.micro.testcases.maxresults;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

@Service
public interface ITestService 
{
	public IIntermediateFuture<String> getInfos();
	
	public ISubscriptionIntermediateFuture<String> subscribeToInfos();
}
