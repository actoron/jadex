package jadex.platform.service.bpmnstarter;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.ecarules.IRuleService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.rules.eca.Event;

/**
 *  Agent that tests the rule and timer monitoring of initial events in bpmn processes.
 */
@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="mons", type=IMonitoringStarterService.class, 
		binding=@Binding(create=true, creationtype="monagent")),
	@RequiredService(name="rules", type=IRuleService.class)
})
@ComponentTypes(@ComponentType(name="monagent", filename="jadex/platform/service/bpmnstarter/MonitoringStarterAgent.class"))
@Agent
public class UserAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<IMonitoringStarterService> fut = agent.getServiceContainer().getRequiredService("mons");
		fut.addResultListener(new ExceptionDelegationResultListener<IMonitoringStarterService, Void>(ret)
		{
			public void customResultAvailable(final IMonitoringStarterService mons)
			{
				IFuture<ILibraryService> fut = agent.getServiceContainer().getRequiredService("libs");
				fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
				{
					public void customResultAvailable(ILibraryService libs)
					{
						testRuleBpmn().addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								testTimerBpmn().addResultListener(new DelegationResultListener<Void>(ret));
							}
						});
					}
				});
			}
		});
	
		return ret;
	}
	
	/**
	 *  Monitor a rule condition start.
	 */
	protected IFuture<Void> testRuleBpmn()
	{
		final Future<Void> ret = new Future<Void>();
		
		final long dur = 10000;
		final String model = "jadex/bpmn/examples/execute/ConditionEventStart.bpmn";
		
		IFuture<IMonitoringStarterService> fut = agent.getRequiredService("mons");
		fut.addResultListener(new ExceptionDelegationResultListener<IMonitoringStarterService, Void>(ret)
		{
			public void customResultAvailable(final IMonitoringStarterService mons)
			{
				mons.addBpmnModel(model, null).addResultListener(new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						System.out.println("monitoring "+dur/1000+"s "+model);
						
						IFuture<IRuleService> fut = agent.getServiceContainer().getRequiredService("rules");
						fut.addResultListener(new DefaultResultListener<IRuleService>()
						{
							public void resultAvailable(final IRuleService rules)
							{
								// push user event to rule service
								rules.addEvent(new Event("file_added", Boolean.TRUE));
								rules.addEvent(new Event("file_added", Boolean.FALSE));
							}
						});
						
						agent.waitFor(dur, new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								mons.removeBpmnModel(model, null).addResultListener(new DefaultResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
										System.out.println("monitoring ended\n\n");
										ret.setResult(null);
									}
								});
								return IFuture.DONE;
							}
						});
					}
				});
			}
		});
		
		
		return ret;
	}
	
	/**
	 *  Monitor a timer start.
	 */
	protected IFuture<Void> testTimerBpmn()
	{
		final Future<Void> ret = new Future<Void>();
		
		final long dur = 65000;
		final String model = "jadex/bpmn/examples/execute/TimerEventStart.bpmn";
		
		IFuture<IMonitoringStarterService> fut = agent.getRequiredService("mons");
		fut.addResultListener(new ExceptionDelegationResultListener<IMonitoringStarterService, Void>(ret)
		{
			public void customResultAvailable(final IMonitoringStarterService mons)
			{		
				mons.addBpmnModel(model, null).addResultListener(new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						System.out.println("monitoring "+dur/1000+"s "+model);
						
						agent.waitFor(dur, new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								mons.removeBpmnModel(model, null).addResultListener(new DefaultResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
										System.out.println("monitoring ended\n\n");
										ret.setResult(null);
									}
								});
								return IFuture.DONE;
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
}
