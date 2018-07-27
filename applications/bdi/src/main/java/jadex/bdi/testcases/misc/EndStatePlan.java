package jadex.bdi.testcases.misc;

import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.collection.SCollection;

/**
 *  Check correct operation of end states.
 */
public class EndStatePlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		IComponentManagementService	cms	= getAgent().getFeature(IRequiredServicesFeature.class)
			.searchService(new ServiceQuery<>(IComponentManagementService.class)).get();
		
		// Store report message from worker agent.
		getWaitqueue().addMessageEvent("inform_reports");
		
		// Create worker agent.
		Map<String, Object> args = SCollection.createHashMap();
		args.put("testagent", getComponentIdentifier());
		IComponentIdentifier	worker	= cms.createComponent("/jadex/bdi/testcases/misc/EndStateWorker.agent.xml",
			new CreationInfo(args, getComponentIdentifier())).getFirstResult();
		
		// Wait for reports from worker agent.
		IMessageEvent	msg	= waitForMessageEvent("inform_reports");
		getWaitqueue().removeMessageEvent("inform_reports");
		List	reports	= (List)msg.getParameter(SFipa.CONTENT).getValue();
		
		// Check if worker agent has been correctly removed.
		waitFor(1000);	// Hack!!! how to ensure that agent has time to remove itself?
		IComponentDescription[]	results	= cms.searchComponents(
			new CMSComponentDescription(worker, null, false, false, false, false, false, null, null, null, null, -1, null, null, false), null).get();
		TestReport	report	= new TestReport("termination", "Test if the worker agent has been terminated");
		if(results.length==0)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Worker agent still alive.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		// Add test results from worker.
		for(int i=0; i<reports.size(); i++)
		{
			getBeliefbase().getBeliefSet("testcap.reports").addFact(reports.get(i));
		}
	}
}
