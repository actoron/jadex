package jadex.platform.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.persistence.IPersistenceService;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that tests snapshot and restore of a simple BPMN processes.
 */
@Agent
//@Results(@Result(name="testresults", clazz=Testcase.class))
// Todo: (re)implement persistence
public class BPMNRecoveryTestAgent
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
		
		runTests("jadex.platform.persistence.SimplePersistence.bpmn2")
			.addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				trs.add(result);
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(trs.size(), trs.toArray(new TestReport[trs.size()])));
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
		
//		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		IPersistenceService	ps	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IPersistenceService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		CreationInfo ci = new CreationInfo(agent.getId());
		ci.setFilename(model);
		
		ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = agent.createComponent(null, ci);
		IExternalAccess	exta = agent.getExternalAccess(fut.getFirstResult()).get();
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

		IPersistInfo	info	= ps.snapshot(fut.getFirstResult()).get();
		exta.killComponent().get();

		ps.restore(info).get();
		
		Future<Collection<Tuple2<String,Object>>>	cres	= new Future<Collection<Tuple2<String,Object>>>();
		IExternalAccess	exta2	= agent.getExternalAccess(fut.getFirstResult()).get();
		exta2.subscribeToResults().addResultListener(new DelegationResultListener<Collection<Tuple2<String,Object>>>(cres));
//		cms.addComponentResultListener(new DelegationResultListener<Collection<Tuple2<String,Object>>>(cres), fut.getFirstResult()).get();
		
		Map<String, Object>	msg	= new HashMap<String, Object>();
//		msg.put(SFipa.RECEIVERS, fut.getFirstResult());
		agent.getFeature(IMessageFeature.class).sendMessage(msg, fut.getFirstResult()).get();
		
		cres.get();
		
		ret.setResult(new TestReport("1#"+model, "Test if a process can be snapshotted and resurrected: "+model, true, null));
		
		return ret;
	}
}
