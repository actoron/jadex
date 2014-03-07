package jadex.platform.service.bpmnstarter;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.ecarules.IRuleService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.platform.service.processengine.IProcessEngineService;
import jadex.platform.service.processengine.ProcessEngineEvent;
import jadex.rules.eca.Event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Agent that tests the rule and timer monitoring of initial events in bpmn processes.
 */
@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="mons", type=IProcessEngineService.class, 
		binding=@Binding(create=true, creationinfo=@CreationInfo(type="monagent"))),
	@RequiredService(name="rules", type=IRuleService.class)
})
@ComponentTypes(@ComponentType(name="monagent", filename="jadex/platform/service/bpmnstarter/MonitoringStarterAgent.class"))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
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
		
		final TestReport[] trs = new TestReport[2];
		
		IFuture<IProcessEngineService> fut = agent.getServiceContainer().getRequiredService("mons");
		fut.addResultListener(new ExceptionDelegationResultListener<IProcessEngineService, Void>(ret)
		{
			public void customResultAvailable(final IProcessEngineService mons)
			{
				IFuture<ILibraryService> fut = agent.getServiceContainer().getRequiredService("libs");
				fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
				{
					public void customResultAvailable(ILibraryService libs)
					{
						testRuleBpmn().addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
						{
							public void customResultAvailable(TestReport tr)
							{
								trs[0] = tr;
								testTimerBpmn().addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
								{
									public void customResultAvailable(TestReport tr)
									{
										trs[1] = tr;
										agent.setResultValue("testresults", new Testcase(2, trs));
										ret.setResult(null);
									}
								});
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
	protected IFuture<TestReport> testRuleBpmn()
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#1", "Test if bpmn rule triggering works for initial rules.");
		
		final long dur = 10000;
		final String model = "jadex/bpmn/examples/execute/ConditionEventStart.bpmn";
		
		IFuture<IProcessEngineService> fut = agent.getRequiredService("mons");
		fut.addResultListener(new ExceptionDelegationResultListener<IProcessEngineService, TestReport>(ret)
		{
			public void customResultAvailable(final IProcessEngineService mons)
			{
				ISubscriptionIntermediateFuture<ProcessEngineEvent> fut = mons.addBpmnModel(model, null);
				fut.addResultListener(new IIntermediateResultListener<ProcessEngineEvent>()
				{
					protected Set<String> results = new HashSet<String>();
					protected boolean fini = false;
					
					public void intermediateResultAvailable(ProcessEngineEvent event)
					{
						System.out.println("received event: "+event);
						
						if(ProcessEngineEvent.PROCESSMODEL_ADDED.equals(event.getType()))
						{
							IFuture<IRuleService> fut = agent.getServiceContainer().getRequiredService("rules");
							fut.addResultListener(new ExceptionDelegationResultListener<IRuleService, TestReport>(ret)
							{
								public void customResultAvailable(final IRuleService rules)
								{
									// fire events to start process instances
									rules.addEvent(new Event("file_added", Boolean.TRUE));
									rules.addEvent(new Event("file_added", Boolean.FALSE));
									rules.addEvent(new Event("file_removed", Boolean.TRUE));
									rules.addEvent(new Event("file_removed", Boolean.FALSE));
								}
							});
						}
						else if(ProcessEngineEvent.PROCESSMODEL_REMOVED.equals(event.getType()))
						{
							// nop
						}
						else if(ProcessEngineEvent.INSTANCE_CREATED.equals(event.getType()))
						{
							// nop
						}
						else if(ProcessEngineEvent.INSTANCE_TERMINATED.equals(event.getType()))
						{
							Map<String, Object> res = (Map<String, Object>)event.getContent();
							results.add((String)res.get("result"));
							if(results.size()==4)
							{
								if(results.contains("a1") && results.contains("a2") && results.contains("b1") && results.contains("b2"))
								{
									tr.setSucceeded(true);
								}
								else
								{
									tr.setFailed("Not every path was activated: "+results);
								}
								proceed();
							}
						}
					}
					
					public void finished()
					{
						proceed();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(!fini)
							tr.setFailed("Exception: "+exception);
						proceed();
					}
					
					public void resultAvailable(Collection<ProcessEngineEvent> result)
					{
						for(ProcessEngineEvent ev: result)
						{
							intermediateResultAvailable(ev);
						}
						finished();
					}
					
					protected void proceed()
					{
						if(!fini)
						{
							fini = true;
							mons.removeBpmnModel(model, null).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
							{
								public void customResultAvailable(Void result)
								{
									System.out.println("monitoring ended\n");
									ret.setResult(tr);
								}
							});
						}
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Monitor a timer start.
	 */
	protected IFuture<TestReport> testTimerBpmn()
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final long dur = 65000;
		final String model = "jadex/bpmn/examples/execute/TimerEventStart.bpmn";
		final TestReport tr = new TestReport("#1", "Test if bpmn rule triggering works for initial rules.");
		
		IFuture<IProcessEngineService> fut = agent.getRequiredService("mons");
		fut.addResultListener(new ExceptionDelegationResultListener<IProcessEngineService, TestReport>(ret)
		{
			public void customResultAvailable(final IProcessEngineService mons)
			{	
				ISubscriptionIntermediateFuture<ProcessEngineEvent> fut = mons.addBpmnModel(model, null);
				fut.addResultListener(new IIntermediateResultListener<ProcessEngineEvent>()
				{
					protected boolean fini = false;
					
					public void intermediateResultAvailable(ProcessEngineEvent event)
					{
						System.out.println("received event: "+event);
						
						if(ProcessEngineEvent.PROCESSMODEL_ADDED.equals(event.getType()))
						{
							// nop
						}
						else if(ProcessEngineEvent.PROCESSMODEL_REMOVED.equals(event.getType()))
						{
							// nop
						}
						else if(ProcessEngineEvent.INSTANCE_CREATED.equals(event.getType()))
						{
							// nop
						}
						else if(ProcessEngineEvent.INSTANCE_TERMINATED.equals(event.getType()))
						{
							tr.setSucceeded(true);
							proceed();
						}
					}
					
					public void finished()
					{
						proceed();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						tr.setFailed("Exception: "+exception);
						proceed();
					}
					
					public void resultAvailable(Collection<ProcessEngineEvent> result)
					{
						for(ProcessEngineEvent ev: result)
						{
							intermediateResultAvailable(ev);
						}
						finished();
					}
					
					protected void proceed()
					{
						if(!fini)
						{
							fini = true;
							mons.removeBpmnModel(model, null).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
							{
								public void customResultAvailable(Void result)
								{
									System.out.println("monitoring ended\n");
									ret.setResult(tr);
								}
							});
						}
					}
				});
			}
		});
		
		return ret;
	}
}
