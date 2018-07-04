package jadex.micro.testcases.serviceinheritance;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
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
 *  Agent that tests if a service can be published with more than one interface.
 *  
 *  The provider agent has a provided service of type IExtendedService.
 *  The super interface IBasicService is annotated with @Service and
 *  thus also published.
 */
@RequiredServices({
	@RequiredService(name="basicser", type=IBasicService.class),
	@RequiredService(name="extser", type=IExtendedService.class)
})
@Agent
@ComponentTypes(@ComponentType(name="provider", filename="jadex.micro.testcases.serviceinheritance.ProviderAgent.class"))
@Configurations(@Configuration(name="def", components=@Component(type="provider")))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ServiceInheritanceTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final Testcase tc = new Testcase();
		tc.setTestCount(2);
		
		invokeBasic().addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport tr)
			{
				tc.addReport(tr);
				invokeExtended().addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport tr)
					{
						tc.addReport(tr);
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Invoke the basic service.
	 */
	protected IFuture<TestReport> invokeBasic()
	{
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport tr = new TestReport();
		
		IFuture<IBasicService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("basicser");
		fut.addResultListener(new IResultListener<IBasicService>()
		{
			public void resultAvailable(IBasicService service)
			{
//				System.out.println("Fetched basic service: "+service);
				
				service.getBasicInfo().addResultListener(new IResultListener<String>()
				{
					public void resultAvailable(String result) 
					{
//						System.out.println("Invoked basic service: "+result);
						tr.setSucceeded(true);
						ret.setResult(tr);
					}
					
					public void exceptionOccurred(Exception exception) 
					{
						tr.setFailed(exception.getMessage());
						ret.setResult(tr);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed(exception.getMessage());
				ret.setResult(tr);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Invoke the extended service.
	 */
	protected IFuture<TestReport> invokeExtended()
	{
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport tr = new TestReport();
		
		IFuture<IExtendedService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("extser");
		fut.addResultListener(new IResultListener<IExtendedService>()
		{
			public void resultAvailable(IExtendedService service)
			{
//				System.out.println("Fetched extended service: "+service);
				
				service.getExtendedInfo().addResultListener(new IResultListener<String>()
				{
					public void resultAvailable(String result) 
					{
//						System.out.println("Invoked extended service: "+result);
						tr.setSucceeded(true);
						ret.setResult(tr);
					}
					
					public void exceptionOccurred(Exception exception) 
					{
						tr.setFailed(exception.getMessage());
						ret.setResult(tr);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed(exception.getMessage());
				ret.setResult(tr);
			}
		});
		
		return ret;
	}
	
	
}
