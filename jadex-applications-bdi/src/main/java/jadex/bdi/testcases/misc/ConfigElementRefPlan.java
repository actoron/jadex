package jadex.bdi.testcases.misc;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.collection.SCollection;

import java.util.List;
import java.util.Map;

/**
 *  Check naming of initial and end elements using config element ref worker agent.
 */
public class ConfigElementRefPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Store report message from worker agent.
		getWaitqueue().addMessageEvent("inform_reports");
		
		// Create worker agent (kills itself automatically).
		IComponentManagementService	cms	= getAgent().getComponentFeature(IRequiredServicesFeature.class)
			.searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		Map<String, Object> args = SCollection.createHashMap();
		args.put("testagent", getComponentIdentifier());
		cms.createComponent("/jadex/bdi/testcases/misc/ConfigElementRefWorker.agent.xml",
			new CreationInfo(args, getComponentIdentifier())).getFirstResult();
		
		// Wait for reports from worker agent.
		IMessageEvent	msg	= waitForMessageEvent("inform_reports");
		getWaitqueue().removeMessageEvent("inform_reports");
		List	reports	= (List)msg.getParameter(SFipa.CONTENT).getValue();
		for(int i=0; i<reports.size(); i++)
		{
			getBeliefbase().getBeliefSet("testcap.reports").addFact(reports.get(i));
		}
	}
}
