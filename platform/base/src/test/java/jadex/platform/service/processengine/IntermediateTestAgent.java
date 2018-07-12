package jadex.platform.service.processengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.TimeoutException;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that tests the rule and timer monitoring of events in bpmn processes.
 */
@RequiredServices(
{
	@RequiredService(name="engine", type=IProcessEngineService.class),
})
@ComponentTypes(@ComponentType(name="engine", clazz=ProcessEngineAgent.class))
@Configurations(@Configuration(name="default", components=@Component(type="engine")))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class IntermediateTestAgent
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
		
		final List<TestReport> trs = new ArrayList<TestReport>();
		
		runTests("jadex.platform.service.processengine.TestIntermediateEvent.bpmn2", "Intermediate")
			.addResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void intermediateResultAvailable(TestReport result)
			{
				trs.add(result);
			}
			
			public void finished()
			{
				runTests("jadex.platform.service.processengine.TestSubprocessStartEvent.bpmn2", "SubprocessStart")
					.addResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void intermediateResultAvailable(TestReport result)
					{
						trs.add(result);
					}
					
					public void finished()
					{
						runTests2("jadex.platform.service.processengine.TestEventprocessStartEvent.bpmn2", "EventSubprocessStart")
							.addResultListener(new IntermediateExceptionDelegationResultListener<TestReport, Void>(ret)
						{
							public void intermediateResultAvailable(TestReport result)
							{
								trs.add(result);
							}
							
							public void finished()
							{
								testEventIgnore("Ignored",
									new TestReport("Ignored#1", "Test if unknown event produces exception."))
									.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
								{
									public void customResultAvailable(TestReport tr)
									{
										trs.add(tr);
										
										agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(trs.size(), trs.toArray(new TestReport[trs.size()])));
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
	 *  Run the tests for a specified model
	 */
	public IIntermediateFuture<TestReport> runTests(final String model, final String eventtype)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		testWrongEventValueIntermediateBpmn(model, eventtype, new TestReport(eventtype+"#1", "Test if bpmn rule not triggering for wrong event value works for: "+eventtype))
			.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(TestReport tr)
			{
				ret.addIntermediateResult(tr);
				
				testWQIntermediateBpmn(model, eventtype, new TestReport(eventtype+"#2", "Test if bpmn rule triggering from wait queue works for: "+eventtype))
					.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
				{
					public void customResultAvailable(TestReport tr)
					{
						ret.addIntermediateResult(tr);
						
						testNoEventIntermediateBpmn(model, new TestReport(eventtype+"#3", "Test if bpmn rule not triggering works for: "+eventtype))
							.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
						{
							public void customResultAvailable(TestReport tr)
							{
								ret.addIntermediateResult(tr);
								testIntermediateBpmn(model, eventtype, new TestReport(eventtype+"#4", "Test if bpmn rule triggering works for: "+eventtype))
									.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
								{
									public void customResultAvailable(TestReport tr)
									{
										ret.addIntermediateResult(tr);
										testWrongEventPropertyIntermediateBpmn(model, eventtype, new TestReport(eventtype+"#5", "Test if bpmn rule not triggering for wrong event property works for: "+eventtype))
											.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
										{
											public void customResultAvailable(TestReport tr)
											{
												ret.addIntermediateResult(tr);
												ret.setFinished();
											}
										});
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
	 *  Run the tests for a specified model.
	 *  
	 *  This methods excludes the waitqueue test because it cannot work
	 *  for event subprocesses (if model registered, event will not be disptached to waitqueue
	 *  but directly trigger instance creation.)
	 */
	public IIntermediateFuture<TestReport> runTests2(final String model, final String eventtype)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		testWrongEventValueIntermediateBpmn(model, eventtype, new TestReport(eventtype+"#1", "Test if bpmn rule not triggering for wrong event value works for: "+eventtype))
			.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
		{
			public void customResultAvailable(TestReport tr)
			{
				ret.addIntermediateResult(tr);
				
				testNoEventIntermediateBpmn(model, new TestReport(eventtype+"#3", "Test if bpmn rule not triggering works for: "+eventtype))
					.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
				{
					public void customResultAvailable(TestReport tr)
					{
						ret.addIntermediateResult(tr);
						testIntermediateBpmn(model, eventtype, new TestReport(eventtype+"#4", "Test if bpmn rule triggering works for: "+eventtype))
							.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
						{
							public void customResultAvailable(TestReport tr)
							{
								ret.addIntermediateResult(tr);
								testWrongEventPropertyIntermediateBpmn(model, eventtype, new TestReport(eventtype+"#5", "Test if bpmn rule not triggering for wrong event property works for: "+eventtype))
									.addResultListener(new ExceptionDelegationResultListener<TestReport, Collection<TestReport>>(ret)
								{
									public void customResultAvailable(TestReport tr)
									{
										ret.addIntermediateResult(tr);
										ret.setFinished();
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
	 *  Monitor an intermediate rule condition.
	 */
	protected IFuture<TestReport> testEventIgnore(String eventtype, TestReport tr)
	{
		Future<TestReport> ret = new Future<TestReport>();
		
		IProcessEngineService	pes	= (IProcessEngineService)agent.getFeature(IRequiredServicesFeature.class).getService("engine").get();

		try
		{
			Map<String, Object>	event	= new HashMap<String, Object>();
			event.put("value", 7);
			pes.processEvent(event, eventtype).get();
			tr.setFailed("No exception on unknown event.");
		}
		catch(RuntimeException e)
		{
			System.out.println(e.getMessage());
			tr.setSucceeded(true);
		}
		
		ret.setResult(tr);
		
		return ret;
	}
	
	/**
	 *  Monitor an intermediate rule condition triggered from wait queue.
	 */
	protected IFuture<TestReport> testWQIntermediateBpmn(String model, String eventtype, TestReport tr)
	{
		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getFeature(IRequiredServicesFeature.class).getService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		Map<String, Object>	event	= new HashMap<String, Object>();
		event.put("value", 7);
		pes.processEvent(event, eventtype).get();
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(500).get();
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getId()));
		
		try
		{
			fut.get(3000);
			tr.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			tr.setFailed("Timeout exception.");
			try
			{
				cms.destroyComponent(fut.getFirstResult());
			}
			catch(Exception ex)
			{
				agent.getLogger().warning("Exception when terminating process: "+model+", "+ex);
			}
		}
		ret.setResult(tr);
		
		return ret;
	}
	
	/**
	 *  Monitor an intermediate rule condition.
	 */
	protected IFuture<TestReport> testIntermediateBpmn(String model, String eventtype, TestReport tr)
	{
//		System.out.println("testIntermediateBpmn: "+model);
		
		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getFeature(IRequiredServicesFeature.class).getService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getId()));
		fut.getFirstResult();
		
//		// For debugging to receive error messages, when thread hangs before fut.get().
//		fut.addResultListener(new IResultListener<Collection<TupleResult>>()
//		{
//			public void resultAvailable(Collection<TupleResult> result)
//			{
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				exception.printStackTrace();
//			}
//		});

		agent.getFeature(IExecutionFeature.class).waitForDelay(500).get();
		
		Map<String, Object>	event	= new HashMap<String, Object>();
		event.put("value", 7);
		pes.processEvent(event, eventtype).get();

		try
		{
			fut.get(3000);
			tr.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			tr.setFailed("Timeout exception.");
			try
			{
				cms.destroyComponent(fut.getFirstResult());
			}
			catch(Exception ex)
			{
				agent.getLogger().warning("Exception when terminating process: "+model+", "+ex);
			}
		}
		ret.setResult(tr);
		
		return ret;
	}
	
	/**
	 *  Monitor an intermediate rule condition not triggering when event value is wrong.
	 */
	protected IFuture<TestReport> testWrongEventValueIntermediateBpmn(String model, String eventtype, TestReport tr)
	{
//		System.out.println("testWrongEventValueIntermediateBpmn: "+model);

		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getFeature(IRequiredServicesFeature.class).getService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getId()));
		fut.getFirstResult();

		agent.getFeature(IExecutionFeature.class).waitForDelay(500).get();
		
		Map<String, Object>	event	= new HashMap<String, Object>();
		event.put("value", 8);
		pes.processEvent(event, eventtype).get();

		try
		{
			fut.get(3000);
			tr.setFailed("No timeout exception.");
		}
		catch(TimeoutException e)
		{
			tr.setSucceeded(true);
			try
			{
				cms.destroyComponent(fut.getFirstResult());
			}
			catch(Exception ex)
			{
				agent.getLogger().warning("Exception when terminating process: "+model+", "+ex);
			}
		}
		ret.setResult(tr);
		
		return ret;
	}
	
	/**
	 *  Monitor an intermediate rule condition not triggering when event property is wrong.
	 */
	protected IFuture<TestReport> testWrongEventPropertyIntermediateBpmn(String model, String eventtype, TestReport tr)
	{
//		System.out.println("testWrongEventPropertyIntermediateBpmn: "+model);
		
		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getFeature(IRequiredServicesFeature.class).getService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getId()));
		fut.getFirstResult();

		agent.getFeature(IExecutionFeature.class).waitForDelay(500).get();
		
		Map<String, Object>	event	= new HashMap<String, Object>();
		event.put("wrong", 7);
		pes.processEvent(event, eventtype).get();

		try
		{
			fut.get(3000);
			tr.setFailed("No timeout exception.");
		}
		catch(TimeoutException e)
		{
			tr.setSucceeded(true);
			try
			{
				cms.destroyComponent(fut.getFirstResult());
			}
			catch(Exception ex)
			{
				agent.getLogger().warning("Exception when terminating process: "+model+", "+ex);
			}
		}
		
		ret.setResult(tr);
		
		return ret;
	}
	
	/**
	 *  Monitor that the intermediate rule condition does not trigger.
	 */
	protected IFuture<TestReport> testNoEventIntermediateBpmn(String model, TestReport tr)
	{
//		System.out.println("testNoEventIntermediateBpmn: "+model);

		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getFeature(IRequiredServicesFeature.class).getService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(500).get();

		ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getId()));
		
		try
		{
			fut.get(3000);
			tr.setFailed("No timeout exception.");
		}
		catch(TimeoutException e)
		{
			tr.setSucceeded(true);
			try
			{
				cms.destroyComponent(fut.getFirstResult());
			}
			catch(Exception ex)
			{
				agent.getLogger().warning("Exception when terminating process: "+model+", "+ex);
			}
		}
		
		ret.setResult(tr);
		
		return ret;
	}
}
