package jadex.bdiv3.testcases.capabilities;

import jadex.base.test.Testcase;

import java.util.concurrent.TimeoutException;

import jadex.base.test.TestReport;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Capability;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class TestNestedCapabilitiesBDI
{	
	@Capability
	TestCapabilityBDI	mycapa	= new TestCapabilityBDI();

	Testcase	tc	= new Testcase(2);
	TestReport	tr1	= new TestReport("#1", "Initial plan in outer capability");
	TestReport	tr2	= new TestReport("#2", "Initial plan in outer capability");

	@AgentBody
	void body(IInternalAccess agent)
	{
		tc.addReport(tr1);
		tc.addReport(tr2);
		agent.getResults().put("testresults", tc);
		mycapa.result.then(nil ->
		{
			tr1.setSucceeded(true);
			checkFinished(agent);
		}).catchEx(ex ->
		{
			tr1.setFailed(ex);
			checkFinished(agent);
		});
		mycapa.subcapa.result.then(nil ->
		{
			tr2.setSucceeded(true);
			checkFinished(agent);
		}).catchEx(ex ->
		{
			tr2.setFailed(ex);
			checkFinished(agent);
		});
		agent.waitForDelay(500).then(nil ->
		{
			mycapa.result.setException(new TimeoutException());
			mycapa.subcapa.result.setException(new TimeoutException());
		});
	}
	
	void checkFinished(IInternalAccess agent)
	{
		if(tr1.isFinished() && tr2.isFinished())
		{
			agent.killComponent();
		}
	}
}
