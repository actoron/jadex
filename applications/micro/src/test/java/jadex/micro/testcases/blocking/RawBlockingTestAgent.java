package jadex.micro.testcases.blocking;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test threaded access to raw services.
 */
@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class RawBlockingTestAgent extends JunitAgentTest
{
	/**
	 *  Execute the agent
	 */
	@AgentBody
	public void	execute(final IInternalAccess agent)
	{
//		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(
//			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		
//		cms.getComponentDescriptions().get();
		SComponentManagementService.getComponentDescriptions(agent).get();
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1,
			new TestReport[]{new TestReport("#1", "Test blocking wait.", true, null)}));
	}
}
