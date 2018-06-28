package jadex.bdi.examples.marsworld_classic.sentry;

import jadex.bdi.examples.marsworld_classic.RequestProduction;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFServiceDescription;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;

/**
 *  Analyse a target.
 */
public class AnalyseTargetPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("Created: "+this);
		
		Target target = (Target)getParameter("target").getValue();

		// Move to the target.
		IGoal go_target = createGoal("move.move_dest");
		go_target.getParameter("destination").setValue(target.getLocation());
		dispatchSubgoalAndWait(go_target);

		// Analyse the target.
		waitFor(1000);
		target.setMarked();
		if(target.getOreCapacity()>0)
			callProductionAgent(target);

		startAtomic();
		
		getBeliefbase().getBeliefSet("analysed_targets").addFact(target);
		getBeliefbase().getBeliefSet("my_targets").removeFact(target);
		
		endAtomic();
	}

	/**
	 *  Sending a locaton to the Production Agent.
	 *  Therefore it has first to be looked up in the DF.
	 *  @param target
	 */
	private void callProductionAgent(Target target)
	{
//		System.out.println("Calling some Production Agent...");

		// Search for Production_Service
		// Create a service description to search for.
		IDF	df	= (IDF)SServiceProvider.getService(getAgent(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		IDFServiceDescription sd = new DFServiceDescription("service_produce", null, null);
		IDFComponentDescription dfadesc = new DFComponentDescription(null, sd);

		// A hack - default is 2! to reach more Agents, we have
		// to increase the number of possible results.
		ISearchConstraints constraints = df.createSearchConstraints(-1, 0);
		IDFComponentDescription[] producers = df.search(dfadesc, constraints).get();

		if(producers.length>0)
		{
			int sel = (int)(Math.random()*producers.length); // todo: Select not randomly
//			System.out.println("Found agents: "+producers.length+" selected: "+sel);

			RequestProduction rp = new RequestProduction();
			rp.setTarget(target);
			//Action action = new Action();
			//action.setAction(rp);
			//action.setActor(SJade.convertAIDtoJade(producers[sel].getName()));
			IMessageEvent mevent = createMessageEvent("request_producer");
			mevent.getParameterSet(SFipa.RECEIVERS).addValue(producers[sel].getName());
			mevent.getParameter(SFipa.CONTENT).setValue(rp);
			sendMessage(mevent);
//			System.out.println("Sentry Agent: sent location to: "+producers[sel].getName());
		}
	}
}
