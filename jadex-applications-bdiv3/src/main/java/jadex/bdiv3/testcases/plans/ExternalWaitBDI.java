package jadex.bdiv3.testcases.plans;

import jadex.micro.annotation.Agent;

/**
 *  Test abort of externally waiting plan with invokeInterruptable.
 */
@Agent
//@Results(@Result(name="testresults", clazz=Testcase.class))
//@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="extWait")))
public class ExternalWaitBDI
{
	// Todo: implement fully functional plan.scheduleSubstep() that allows step execution to be aborted?
	
//	@Agent
//	protected IInternalAccess agent;
//	
//	protected TestReport tr = new TestReport("#1", "Test if external wait with invokeInterruptable works.");
//	
//	@Plan
//	protected IFuture<Void> extWait(IPlan plan)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		System.out.println("before waiting");
//		
//		plan.invokeInterruptable(new IResultCommand<IFuture<Void>, Void>()
//		{
//			public IFuture<Void> execute(Void args)
//			{
//				System.out.println("start waiting...");
//				return agent.getComponentFeature(IExecutionFeature.class).waitForDelay(3000);
//			}
//		}).addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				System.out.println("ended waiting normally");
//				tr.setFailed("ended waiting normally");
//				agent.killComponent();
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//				if(exception instanceof PlanAbortedException)
//				{
//					tr.setSucceeded(true);
//				}
//				agent.killComponent();
//			}
//		});
//		
//		plan.abort();
//		
//		return ret;
//	}	
//	
//	/**
//	 *  Called when agent is killed.
//	 */
//	@AgentKilled
//	public void	destroy(IInternalAccess agent)
//	{
//		System.out.println("destroy: "+agent);
//		
//		if(!tr.isFinished())
//				tr.setFailed("Plan not activated");
//		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
//	}
}
