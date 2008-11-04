package jadex.bdi.testcases.misc;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
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
		IGoal	create	= createGoal("amscap.ams_create_agent");
		create.getParameter("type").setValue("/jadex/bdi/testcases/misc/ConfigElementRefWorker.agent.xml");
		Map args = SCollection.createHashMap();
		args.put("testagent", getAgentIdentifier());
		create.getParameter("arguments").setValue(args);
		dispatchSubgoalAndWait(create);
		
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
