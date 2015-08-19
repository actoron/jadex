package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanAbortedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.IResultCommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test abort of externally waiting plan with invokeInterruptable
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="extWait")))
public class ExternalWaitBDI
{
	@Agent
	protected IInternalAccess agent;
	
	protected TestReport tr = new TestReport("#1", "Test if external wait with invokeInterruptable works.");
	
	@Plan
	protected IFuture<Void> extWait(IPlan plan)
	{
		final Future<Void> ret = new Future<Void>();
		
		System.out.println("before waiting");
		
		plan.invokeInterruptable(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				System.out.println("start waiting...");
				return agent.getComponentFeature(IExecutionFeature.class).waitForDelay(3000);
			}
		}).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("ended waiting normally");
				tr.setFailed("ended waiting normally");
				agent.killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				if(exception instanceof PlanAbortedException)
				{
					tr.setSucceeded(true);
				}
				agent.killComponent();
			}
		});
		
		plan.abort();
		
		return ret;
	}	
	
	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		if(!tr.isFinished())
				tr.setFailed("Plan not activated");
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
