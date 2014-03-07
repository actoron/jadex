package jadex.platform.service.processengine;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
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
		
		final TestReport[] trs = new TestReport[1];
		
		testIntemediateBpmn().addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport tr)
			{
				trs[0] = tr;
				agent.setResultValue("testresults", new Testcase(trs.length, trs));
				ret.setResult(null);
			}
		});
	
		return ret;
	}		

	/**
	 *  Monitor an intermediate rule condition.
	 */
	protected IFuture<TestReport> testIntemediateBpmn()
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#1", "Test if bpmn rule triggering works for intermediate events.");
		
		final String model = "jadex.platform.service.processengine.TestIntermediateEvent.bpmn2";

		IComponentManagementService	cms	= agent.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		IProcessEngineService	pes	= (IProcessEngineService)agent.getServiceContainer().getRequiredService("engine").get();

		pes.addBpmnModel(model, agent.getModel().getResourceIdentifier()).getNextIntermediateResult();
		
		pes.processEvent("test-event", null).get();
		
		agent.waitForDelay(500).get();
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getComponentIdentifier()));
		fut.getFirstResult();
		fut.getSecondResult();
		
		tr.setSucceeded(true);
		ret.setResult(tr);
		
		return ret;
	}
}
