package jadex.bdiv3.testcases.capabilities;

import jadex.base.test.Testcase;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IFuture;

import java.util.concurrent.TimeoutException;

import jadex.base.test.TestReport;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Capability;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class TestNestedCapabilitiesBDI
{	
	@Capability
	TestCapabilityBDI	mycapa	= new TestCapabilityBDI();

	Testcase	tc	= new Testcase(2);
	TestReport	tr1	= new TestReport("#1", "Initial plan in outer capability");
	TestReport	tr2	= new TestReport("#2", "Initial plan in outer capability");

	@AgentBody
	void body(final IInternalAccess agent)
	{
		tc.addReport(tr1);
		tc.addReport(tr2);
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
		mycapa.result.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				tr1.setSucceeded(true);
				checkFinished(agent);
			}
			
			public void exceptionOccurred(Exception ex)
			{
				tr1.setFailed(ex);
				checkFinished(agent);
			}
		});
		mycapa.subcapa.result.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				tr2.setSucceeded(true);
				checkFinished(agent);
			}
			
			public void exceptionOccurred(Exception ex)
			{
				tr2.setFailed(ex);
				checkFinished(agent);
			}
		});
		agent.waitForDelay(500, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess agent)
			{
				mycapa.result.setException(new TimeoutException());
				mycapa.subcapa.result.setException(new TimeoutException());
				return IFuture.DONE;
			}
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
