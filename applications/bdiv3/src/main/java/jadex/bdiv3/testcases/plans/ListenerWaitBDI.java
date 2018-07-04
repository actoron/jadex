package jadex.bdiv3.testcases.plans;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
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
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test abort of externally waiting plan with invokeInterruptable
 */
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
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
		
		IFuture<IComponentManagementService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("cms");
//		agent.createResultListener(listener)
		fut.addResultListener(new IResultListener<IComponentManagementService>()
		{
			public void resultAvailable(IComponentManagementService cms)
			{
				System.out.println("after cms: "+cms);
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
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
