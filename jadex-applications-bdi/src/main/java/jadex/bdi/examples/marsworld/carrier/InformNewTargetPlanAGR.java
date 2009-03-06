package jadex.bdi.examples.marsworld.carrier;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.contextservice.IContextService;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.marsworld.Target;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.commons.SUtil;

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
	 *  Sending a locaton to the Production Agent.
	 *  Therefore it has first to be looked up in the DF.
	 *  @param target
	 */
	private void informSentryAgents(Target target)
	{
		//System.out.println("Informing all sentry agents.");
		
//		IContextService cs = (IContextService)getScope().getPlatform().getService(IContextService.class);
//		ApplicationContext ac = (ApplicationContext)cs.getContexts(ApplicationContext.class)[0];
//		AGRSpace agrs = (AGRSpace)ac.getSpace(AGRSpace.class);
//		AGRGroup group = agrs.getGroup("mymarsteam");
//		group.sendMessage("sentry");
		
		if(sentries.length>0)
		{
			IMessageEvent mevent = createMessageEvent("inform_target");
//			for(int i=0; i<sentries.length; i++)
//				mevent.getParameterSet(SFipa.RECEIVERS).addValue(sentries[i].getName());
			mevent.getParameter(SFipa.CONTENT).setValue(target);
//			sendMessage(mevent);
//			group.sendMessage("sentry", mevent);
			//System.out.println("Informing sentries: "+getScope().getPlatformAgent().getLocalName());
		}
	}
}
