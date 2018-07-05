package jadex.platform.service.processengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.rules.eca.Event;

/**
 *  Agent that tests the rule and timer monitoring of initial events in bpmn processes.
 */
@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="engine", type=IProcessEngineService.class),
//	@RequiredService(name="rules", type=IRuleService.class)
})
@ComponentTypes(@ComponentType(name="engine", clazz=ProcessEngineAgent.class))
@Configurations(@Configuration(name="default", components=@Component(type="engine")))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class UserAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport[] trs = new TestReport[2];
		
		IFuture<IProcessEngineService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("engine");
		fut.addResultListener(new ExceptionDelegationResultListener<IProcessEngineService, Void>(ret)
		{
			public void customResultAvailable(final IProcessEngineService mons)
			{
				IFuture<ILibraryService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("libs");
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
										agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, trs));
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
		
		final String model = "jadex.platform.service.processengine.ConditionEventStart.bpmn2";
		
		IFuture<IProcessEngineService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("engine");
		fut.addResultListener(new ExceptionDelegationResultListener<IProcessEngineService, TestReport>(ret)
		{
			public void customResultAvailable(final IProcessEngineService engine)
			{
				ISubscriptionIntermediateFuture<ProcessEngineEvent> fut = engine.addBpmnModel(model, null);
				fut.addResultListener(new IIntermediateResultListener<ProcessEngineEvent>()
				{
					protected List<String> results = new ArrayList<String>();
					protected boolean fini = false;
					
					public void intermediateResultAvailable(ProcessEngineEvent event)
					{
//						System.out.println("received event: "+event);
						
						if(ProcessEngineEvent.PROCESSMODEL_ADDED.equals(event.getType()))
						{
							engine.processEvent(new Event("file_added", Boolean.TRUE), "file_added");
							engine.processEvent(new Event("file_added", Boolean.FALSE), "file_added");
							engine.processEvent(new Event("file_removed", Boolean.TRUE), "file_removed");
							engine.processEvent(new Event("file_removed", Boolean.FALSE), "file_removed");
						}
						else if(ProcessEngineEvent.PROCESSMODEL_REMOVED.equals(event.getType()))
						{
							// nop
						}
						else if(ProcessEngineEvent.INSTANCE_CREATED.equals(event.getType()))
						{
//							System.out.println("created: "+event);
							// nop
						}
						else if(ProcessEngineEvent.INSTANCE_TERMINATED.equals(event.getType()))
						{
//							System.out.println("received term: "+results);
							
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
						exception.printStackTrace();
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
							engine.removeBpmnModel(model, null).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
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
		
		final String model = "jadex.platform.service.processengine.TimerEventStart.bpmn2";
		final TestReport tr = new TestReport("#1", "Test if bpmn rule triggering works for initial rules.");
		
		IFuture<IProcessEngineService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("engine");
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
//						System.out.println("received event: "+event);
						
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
						exception.printStackTrace();
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
