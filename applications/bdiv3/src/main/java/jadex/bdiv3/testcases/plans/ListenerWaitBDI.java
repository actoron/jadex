package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test abort of externally waiting plan with invokeInterruptable
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="extWait")))
public class ListenerWaitBDI
{
	@Agent
	protected IInternalAccess agent;
	
	protected TestReport tr = new TestReport("#1", "Test if external wait with invokeInterruptable works.");
	
	@Plan
	protected IFuture<Void> extWait(IPlan plan)
	{
		final Future<Void> ret = new Future<Void>();
		
		System.out.println("before cms fetch");
		
//		agent.createResultListener(listener)
		
		IFuture<ILibraryService> fut = agent.getFeature(IRequiredServicesFeature.class).getService(ILibraryService.class);
//		agent.createResultListener(listener)
		fut.addResultListener(new IResultListener<ILibraryService>()
		{
			public void resultAvailable(ILibraryService cms)
			{
				System.out.println("after lib: "+cms);
				ret.setResult(null);
				tr.setSucceeded(true);
				agent.killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				agent.killComponent();
			}
		});
		
		return ret;
	}	
	
	@PlanAborted
	@PlanFailed
	@PlanPassed
	public void end()
	{
		System.out.println("plan end");
	}
	
	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		if(!tr.isFinished())
			tr.setFailed("Plan not activated");
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
