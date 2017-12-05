package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if in configurations provided service implementations can be overridden.
 */ 
@ProvidedServices(@ProvidedService(type=IAService.class, implementation=@Implementation(expression="$pojoagent")))
@Configurations({
	@Configuration(name="a", providedservices=@ProvidedService(type=IAService.class, 
		implementation=@Implementation(expression="$pojoagent.getService()"))),
	@Configuration(name="b")
})
@Results(@Result(name="testresults", clazz=Testcase.class)) 
@Service(IAService.class)
@Agent
public class ProvidedServiceConfigurationsAgent implements IAService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Agent created.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();
		IAService as = (IAService)agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedServices(IAService.class)[0];
		as.test().addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
//				System.out.println(result);
		        TestReport    tr    = new TestReport("#1", "Test provided service overriding.");
		        if(result.equals("a"))
		        {
		        	tr.setSucceeded(true);
		        }
		        else
		        {
		        	tr.setFailed("Wrong service implementation: "+result);
		        }
		        agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		        ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  The body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		return IFuture.DONE;
//		killAgent();
	}
	
	/**
	 *  Dummy service method.
	 */
	public IFuture test()
	{
		return new Future("b");
	}
	
	/**
	 *  Static method for fetching alternative service implementation.
	 */
	public static IAService getService()
	{
		return new MyAService();
//		return new IAService()
//		{
//			public IFuture test()
//			{
//				return new Future("a");
//			}
//		};
	}
	
	@Service
	public static class MyAService implements IAService
	{
		public IFuture test()
		{
			return new Future("a");
		}
	}
}

