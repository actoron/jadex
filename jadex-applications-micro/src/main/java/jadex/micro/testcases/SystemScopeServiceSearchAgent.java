package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;


/**
 *  Agent that tests if system services can be found without
 *  explicitly stating search scope PLATORM.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class))
public class SystemScopeServiceSearchAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentService
	protected IComponentManagementService cms;
	
	@AgentBody
	public void body()
	{	
		TestReport tr1 = new TestReport("#1", "Test if system service can be found without scope with SServiceProvider");
		try
		{
			IComponentManagementService cms = SServiceProvider.getLocalService(agent, IComponentManagementService.class);
			System.out.println("Found: "+cms);
			tr1.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr1.setFailed("Not found: "+e);
		}
		
		
		TestReport tr2 = new TestReport("#2", "Test if system service can be found without scope with required service def");
		try
		{
			IFuture<IComponentManagementService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms");
			System.out.println("Found: "+fut.get());
			tr2.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr2.setFailed("Not found: "+e);
		}
		
		TestReport tr3 = new TestReport("#3", "Test if system service can be found without scope with required service injection");
		if(cms!=null)
		{
			tr3.setSucceeded(true);
		}
		else
		{
			tr3.setFailed("Not injected");
		}
		
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(3, 
			new TestReport[]{tr1, tr2, tr3}));
		
		agent.killComponent();
	}
}
