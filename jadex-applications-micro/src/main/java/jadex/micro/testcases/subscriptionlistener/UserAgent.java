package jadex.micro.testcases.subscriptionlistener;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class UserAgent
{
	@AgentBody
	public void body(IInternalAccess agent)
	{
		ITestService ts = SServiceProvider.getService(agent.getServiceContainer(), ITestService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		ISubscriptionIntermediateFuture<String> fut = ts.test();
		fut.addResultListener(new IResultListener<Collection<String>>()
		{
			public void resultAvailable(Collection<String> result)
			{
				System.out.println("rec: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
	}
}
