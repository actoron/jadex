package jadex.bdi.testcases.misc;

import java.util.List;
import java.util.Map;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.collection.SCollection;

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
		Map<String, Object> args = SCollection.createHashMap();
		args.put("testagent", getComponentIdentifier());
		getAgent().createComponent(
			new CreationInfo(args).setFilename("/jadex/bdi/testcases/misc/ConfigElementRefWorker.agent.xml")).get();
		
		// Wait for init reports from worker agent.
		IMessageEvent	msg	= waitForMessageEvent("inform_reports");
		getWaitqueue().removeMessageEvent("inform_reports");
		List<?>	reports	= (List<?>)msg.getParameter(SFipa.CONTENT).getValue();
		for(int i=0; i<reports.size(); i++)
		{
			getBeliefbase().getBeliefSet("testcap.reports").addFact(reports.get(i));
		}
		
		// Wait for end reports from worker agent.
		msg	= waitForMessageEvent("inform_reports");
		getWaitqueue().removeMessageEvent("inform_reports");
		reports	= (List<?>)msg.getParameter(SFipa.CONTENT).getValue();
		for(int i=0; i<reports.size(); i++)
		{
			getBeliefbase().getBeliefSet("testcap.reports").addFact(reports.get(i));
		}
	}
}
