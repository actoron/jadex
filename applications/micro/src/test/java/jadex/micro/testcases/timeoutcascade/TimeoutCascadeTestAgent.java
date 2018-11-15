package jadex.micro.testcases.timeoutcascade;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Calls service 1 on first agent. 
 */
@Agent
@RequiredServices(@RequiredService(name = "ser1", type=IService1.class, 
	scope = ServiceScope.PLATFORM))
@Results(@Result(name="testresults", clazz=Testcase.class))
@ComponentTypes(
{
	@ComponentType(name="agent1", clazz=Service1Agent.class),
	@ComponentType(name="agent2", clazz=Service2Agent.class)
})
@Configurations(@Configuration(name="def", components={@Component(type="agent1"), @Component(type="agent2")}))
@Properties({
		@NameValue(name = Testcase.PROPERTY_TEST_TIMEOUT, value = "40000")
})
public class TimeoutCascadeTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;

	@AgentBody
	public void body()
	{
		Testcase tc = new Testcase(1);
		
		TestReport tr = new TestReport("#1", "Test if timeout annotations are respected in cascading service calls.");
		IService1 ser1 = (IService1)agent.getFeature(IRequiredServicesFeature.class).getService("ser1").get();
		try
		{
			ser1.service().get();
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setFailed("Exception occurred: "+e.getMessage());
		}
		
		tc.addReport(tr);
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
		agent.killComponent();
	}
}