package jadex.bdi.examples.marsworld_classic.producer;

import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.extension.agr.AGRSpace;
import jadex.extension.agr.Group;

/**
 *  Inform the sentry agent about a new target.
 */
public class InformNewTargetPlanAGR extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Substract both sets.
		Target[] ts = (Target[])getBeliefbase().getBeliefSet("move.my_targets").getFacts();
		Target[] fts = (Target[])getBeliefbase().getBeliefSet("finished_targets").getFacts();
		Target[] res = (Target[])SUtil.substractArrays(ts, fts);

		for(int i=0; i<res.length; i++)
		{
			//System.out.println("Infoming sentry about a new target!!!");
			informSentryAgents(res[i]);
			getBeliefbase().getBeliefSet("finished_targets").addFact(res[i]);
		}
	}

	/**
	 *  Sending a location to the agents with the sentry role.
	 */
	private void informSentryAgents(Target target)
	{
		//System.out.println("Informing all sentry agents.");
		
		AGRSpace agrs = (AGRSpace)((IExternalAccess)getScope().getParentAccess()).getExtension("myagrspace").get(this);

		Group group = agrs.getGroup("mymarsteam");
		IComponentIdentifier[]	sentries	= group.getAgentsForRole("sentry");
		
		IMessageEvent mevent = createMessageEvent("inform_target");
		for(int i=0; i<sentries.length; i++)
			mevent.getParameterSet(SFipa.RECEIVERS).addValue(sentries[i]);
		mevent.getParameter(SFipa.CONTENT).setValue(target);
		sendMessage(mevent);

		//System.out.println("Informing sentries: "+getScope().getPlatformAgent().getLocalName());
	}
}
