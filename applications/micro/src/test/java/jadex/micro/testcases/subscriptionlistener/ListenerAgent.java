package jadex.micro.testcases.subscriptionlistener;

import java.util.ArrayList;
import java.util.Collection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.CollectingIntermediateResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test addition of normal listener on subscription future.
 *  Should fail on normal listener.
 *  Should succeed with collecting listener.
 */
@RequiredServices(
{
	@RequiredService(name="test", type=ITestService.class),
})
@ComponentTypes(@ComponentType(name="test", clazz=ProviderAgent.class))
@Configurations(@Configuration(name="default", components=@Component(type="test")))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ListenerAgent
{
	@AgentBody
	public IFuture<Void> body(final IInternalAccess agent)
	{
		final Future<Void>	ret	= new Future<Void>();
		final Collection<TestReport>	reports	= new ArrayList<TestReport>();
		
		ITestService ts = (ITestService)agent.getComponentFeature(IRequiredServicesFeature.class).getService("test").get();
		ISubscriptionIntermediateFuture<String> fut = ts.test();
		
		TestReport	tr	= new TestReport("#1", "Test addition of wrong listener.");
		try
		{
			fut.addResultListener(new IResultListener<Collection<String>>()
			{
				public void resultAvailable(Collection<String> result)
				{
					// ignore
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// ignore
				}
			});
			
			tr.setFailed("Wrong listener could be added.");
		}
		catch(IllegalArgumentException e)
		{
			tr.setSucceeded(true);			
		}
		reports.add(tr);
		
		final TestReport	tr2	= new TestReport("#2", "Test collecting listener.");
		fut.addResultListener(new CollectingIntermediateResultListener<String>()
		{
			public void resultAvailable(Collection<String> result)
			{
				tr2.setSucceeded(true);
				proceed();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr2.setFailed(exception);
				proceed();
			}
			
			protected void	proceed()
			{
				reports.add(tr2);
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(reports.size(), reports.toArray(new TestReport[reports.size()])));
				ret.setResult(null);				
			}
		});
		
		return ret;
	}
}
