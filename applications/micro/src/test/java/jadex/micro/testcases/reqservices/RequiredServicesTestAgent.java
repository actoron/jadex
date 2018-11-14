package jadex.micro.testcases.reqservices;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 * Tests if agent required services can be inferred from field agentservice declarations.
 */
@Agent
@RequiredServices(@RequiredService(name="clock", type=IClockService.class))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class RequiredServicesTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;

	@AgentServiceSearch(name="clock", lazy=false)
	protected IClockService clockser;
	
	// todo: allow for omitting name=""
	@AgentServiceSearch(lazy=false, requiredservice=@RequiredService(name="", type=ILibraryService.class))
	protected ILibraryService ls;

	/**
	 * The agent body.
	 */
	@AgentBody
	public void body()
	{
		final TestReport tr1 = new TestReport("#1", "Test if inline required service definition works");
		
		if(ls instanceof ILibraryService)
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Component management service was not injected: "+ls);
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr1}));
		agent.killComponent();
	}
}
