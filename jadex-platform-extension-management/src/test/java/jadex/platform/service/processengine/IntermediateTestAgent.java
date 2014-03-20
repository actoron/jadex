package jadex.platform.service.processengine;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Agent that tests the rule and timer monitoring of events in bpmn processes.
 */
@RequiredServices(
{
	@RequiredService(name="engine", type=IProcessEngineService.class, 
		binding=@Binding(create=true, creationinfo=@CreationInfo(type="engine"))),
})
@ComponentTypes(@ComponentType(name="engine", clazz=ProcessEngineAgent.class))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class IntermediateTestAgent
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
		
		final List<TestReport> trs = new ArrayList<TestReport>();
		
		testIntermediateBpmn("jadex.platform.service.processengine.TestIntermediateEvent.bpmn2", "Intermediate",
			new TestReport("#1", "Test if bpmn rule triggering works for intermediate events."))
			.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport tr)
			{
				trs.add(tr);
				
				testIntermediateBpmn("jadex.platform.service.processengine.TestSubprocessStartEvent.bpmn2", "SubprocessStart",
					new TestReport("#2", "Test if bpmn rule triggering works for start events of active subprocesses."))
					.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport tr)
					{
						trs.add(tr);
		
						testNoEventIntermediateBpmn("jadex.platform.service.processengine.TestIntermediateEvent.bpmn2",
							new TestReport("#1", "Test if bpmn rule triggering works for intermediate events."))
							.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
						{
							public void customResultAvailable(TestReport tr)
							{
								trs.add(tr);
								
								testNoEventIntermediateBpmn("jadex.platform.service.processengine.TestSubprocessStartEvent.bpmn2", 
									new TestReport("#2", "Test if bpmn rule triggering works for start events of active subprocesses."))
									.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
								{
									public void customResultAvailable(TestReport tr)
									{
										trs.add(tr);
						
										testEventIgnore("Ignored",
											new TestReport("#2", "Test if unknown event produces exception."))
											.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
										{
											public void customResultAvailable(TestReport tr)
											{
												trs.add(tr);
												
												agent.setResultValue("testresults", new Testcase(trs.size(), trs.toArray(new TestReport[trs.size()])));
												ret.setResult(null);
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
	 *  Monitor an intermediate rule condition.
	 */
	protected IFuture<TestReport> testEventIgnore(String eventtype, TestReport tr)
	{
		Future<TestReport> ret = new Future<TestReport>();
		
		IProcessEngineService	pes	= (IProcessEngineService)agent.getServiceContainer().getRequiredService("engine").get();

		try
		{
			pes.processEvent("test-event", eventtype).get();
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
	 *  Monitor an intermediate rule condition.
	 */
	protected IFuture<TestReport> testIntermediateBpmn(String model, String eventtype, TestReport tr)
	{
		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getServiceContainer().getRequiredService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		pes.processEvent("test-event", eventtype).get();
		
		agent.waitForDelay(500).get();
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getComponentIdentifier()));
		fut.getFirstResult();
		fut.getSecondResult();
		
		tr.setSucceeded(true);
		ret.setResult(tr);
		
		return ret;
	}
	
	/**
	 *  Monitor that the intermediate rule condition does not trigger.
	 */
	protected IFuture<TestReport> testNoEventIntermediateBpmn(String model, TestReport tr)
	{
		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getServiceContainer().getRequiredService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		agent.waitForDelay(500).get();
		
		try
		{
			ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getComponentIdentifier()));
			fut.get(3000);
			tr.setFailed("No timeout exception.");
		}
		catch(TimeoutException e)
		{
			tr.setSucceeded(true);
		}
		
		ret.setResult(tr);
		
		return ret;
	}
}
