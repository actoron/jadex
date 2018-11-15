package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if binding of required service info can be overridden in configuration.
 */
@RequiredServices(@RequiredService(name="as", type=IAService.class, scope=ServiceScope.PLATFORM))
@Configurations({
	@Configuration(name="a", requiredservices=@RequiredService(name="as", type=IAService.class, scope=ServiceScope.COMPONENT_ONLY)),
	@Configuration(name="b")
})
@Results(@Result(name="testresults", clazz=Testcase.class)) 
@Agent(keepalive=Boolean3.FALSE)
public class RequiredServiceConfigurationsAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Agent created.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
//		BasicServiceContainer con = (BasicServiceContainer)agent.getServiceContainer();
		RequiredServiceInfo rsi = ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getServiceInfo("as");
//		System.out.println(rsi.getDefaultBinding().getScope());
		TestReport tr = new TestReport("#1", "Test required service overriding.");
		if(rsi.getDefaultBinding().getScope().equals(ServiceScope.COMPONENT_ONLY))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong service implementation: "+rsi.getDefaultBinding().getScope());
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		return IFuture.DONE;
	}	
}