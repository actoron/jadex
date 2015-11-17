package jadex.bdi.examples.marsworld_classic.producer;

import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFServiceDescription;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.fipa.SearchConstraints;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.SUtil;

/**
 *  Inform the sentry agent about a new target.
 */
public class InformNewTargetPlan extends Plan
{
	//-------- attributes --------

	/** The target. */
	protected Target target;

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

		// Search for Production_Service
		// Create a service description to search for.
		IDF	df	= (IDF)SServiceProvider.getService(getAgent(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		IDFServiceDescription sd = new DFServiceDescription("service_sentry", null, null);
		IDFComponentDescription dfadesc = new DFComponentDescription(null, sd);

		// A hack - default is 2! to reach more Agents, we have
		// to increase the number of possible results.
		ISearchConstraints constraints = new SearchConstraints(-1, 0);
		IDFComponentDescription[] sentries = df.search(dfadesc, constraints).get();

		if(sentries.length>0)
		{
			//InformTarget it = new InformTarget();
			//it.setTarget(target);
			//Action action = new Action();
			//action.setAction(it);
			//action.setActor(new AID("dummy", true)); // Hack!! What to do with more than one receiver?
			IMessageEvent mevent = createMessageEvent("inform_target");
			for(int i=0; i<sentries.length; i++)
				mevent.getParameterSet(SFipa.RECEIVERS).addValue(sentries[i].getName());
			mevent.getParameter(SFipa.CONTENT).setValue(target);
			sendMessage(mevent);
		}
	}
}
