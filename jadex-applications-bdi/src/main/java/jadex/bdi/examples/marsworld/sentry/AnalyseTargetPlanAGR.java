package jadex.bdi.examples.marsworld.sentry;

import jadex.adapter.base.agr.AGRSpace;
import jadex.adapter.base.agr.Group;
import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.contextservice.IContextService;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.marsworld.RequestProduction;
import jadex.bdi.examples.marsworld.Target;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;


/**
 *  Inform the sentry agent about a new target.
 */
public class AnalyseTargetPlanAGR extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public AnalyseTargetPlanAGR()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
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

		IContextService cs = (IContextService)getScope().getPlatform().getService(IContextService.class);
		ApplicationContext ac = (ApplicationContext)cs.getContexts(ApplicationContext.class)[0];
		AGRSpace agrs = (AGRSpace)ac.getSpace("myagrspace");
		Group group = agrs.getGroup("mymarsteam");
		IAgentIdentifier[]	producers	= group.getAgentsForRole("producer");

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
