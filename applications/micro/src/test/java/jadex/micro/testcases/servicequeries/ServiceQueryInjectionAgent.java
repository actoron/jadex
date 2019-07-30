package jadex.micro.testcases.servicequeries;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(TestService.class)))
@Arguments(@Argument(clazz=String.class, name="testarg", defaultvalue="\"testval\""))
public class ServiceQueryInjectionAgent extends JunitAgentTest
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
		
		ITestService ts = agent.getProvidedService(ITestService.class);
		TestReport[] trs = ts.test().get();
		
		final Testcase tc = new Testcase();
		tc.setTestCount(trs.length);
		tc.setReports(trs);

		agent.getResults().put("testresults", tc);
		ret.setResult(null);
		
		return ret;
	}
}
