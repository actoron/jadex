package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanAbortedException;
import jadex.bdiv3.testcases.plans.ExternalWait2BDI.ExtWait;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test abort of externally waiting plan with manual "interruptable" step.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="extWait", clazz=ExtWait.class)))
public class ExternalWait2BDI
{
	@Agent
	protected IInternalAccess agent;
	
	protected TestReport tr = new TestReport("#1", "Test if external wait can be stopped in aborted().");
	
	@Plan
	class ExtWait
	{
		protected Future<Void>	fut	= new Future<Void>();
		
		@PlanBody
		protected IFuture<Void> extWait(IPlan plan)
		{
			final Future<Void> ret = new Future<Void>();
			
			System.out.println("before waiting");
			
			agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess args)
				{
					System.out.println("start waiting...");
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(3000)
						.addResultListener(new DelegationResultListener<Void>(fut, true));
					return fut;
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
		
		@PlanAborted
		protected void	aborted(IPlan plan)
		{
			fut.setExceptionIfUndone(plan.getException());
		}
	}
	
	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		System.out.println("destroy: "+agent);
		
		if(!tr.isFinished())
				tr.setFailed("Plan not activated");
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
