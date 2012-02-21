package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.service.BasicServiceContainer;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if binding of required service info can be overridden in configuration.
 */
@RequiredServices(@RequiredService(name="as", type=IAService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@Configurations({
	@Configuration(name="a", requiredservices=@RequiredService(name="as", type=IAService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_LOCAL))),
	@Configuration(name="b")
})
@Results(@Result(name="testresults", clazz=Testcase.class)) 
@Agent(keepalive=false)
public class RequiredServiceConfigurationsAgent //extends MicroAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  Agent created.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		BasicServiceContainer con = (BasicServiceContainer)agent.getServiceContainer();
		RequiredServiceInfo rsi = con.getRequiredServiceInfo("as");
//		System.out.println(rsi.getDefaultBinding().getScope());
		TestReport tr = new TestReport("#1", "Test required service overriding.");
		if(rsi.getDefaultBinding().getScope().equals(RequiredServiceInfo.SCOPE_LOCAL))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong service implementation: "+rsi.getDefaultBinding().getScope());
		}
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
		return IFuture.DONE;
	}
	
//	/**
//	 *  The body.
//	 */
//	public void executeBody()
//	{
//		killAgent();
//	}
}