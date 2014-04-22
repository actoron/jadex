package jadex.platform.persistence;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Agent that tests the rule and timer monitoring of events in bpmn processes.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class BPMNPersistenceTestAgent
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
		
		runTests("jadex.platform.persistence.SimplePersistence.bpmn2")
			.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				trs.add(result);
				agent.setResultValue("testresults", new Testcase(trs.size(), trs.toArray(new TestReport[trs.size()])));
				ret.setResult(null);
			}
		});
	
		return ret;
	}
	
	/**
	 *  Run the tests for a specified model
	 */
	public IFuture<TestReport> runTests(final String model)
	{
		Future<TestReport> ret = new Future<TestReport>();
		
		IComponentManagementService	cms	= agent.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		ITuple2Future<IComponentIdentifier, Map<String, Object>>	fut = cms.createComponent(model, new jadex.bridge.service.types.cms.CreationInfo(agent.getComponentIdentifier()));
		IExternalAccess	exta	= cms.getExternalAccess(fut.getFirstResult()).get();
		ISubscriptionIntermediateFuture<Tuple2<String, Object>>	res	= exta.subscribeToResults();
		if(!exta.getResults().get().containsKey("running"))
		{
			System.out.println("Not yet running.");
			res.getNextIntermediateResult();
			System.out.println("Now running.");
		}
		else
		{
			System.out.println("Already running.");
		}

		IPersistInfo	info	= cms.getPersistableState(fut.getFirstResult()).get();
		exta.killComponent().get();

		cms.resurrectComponent(info).get();
		
		Map<String, Object>	msg	= new HashMap<String, Object>();
		msg.put(SFipa.RECEIVERS, fut.getFirstResult());
		agent.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE).get();
		
		Future<Collection<Tuple2<String,Object>>>	cres	= new Future<Collection<Tuple2<String,Object>>>();
		cms.addComponentResultListener(new DelegationResultListener<Collection<Tuple2<String,Object>>>(cres), fut.getFirstResult());
		cres.get();
		
		return ret;
	}
}
