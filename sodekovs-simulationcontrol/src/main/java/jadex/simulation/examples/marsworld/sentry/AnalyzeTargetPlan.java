package jadex.simulation.examples.marsworld.sentry;

import jadex.base.fipa.SFipa;
import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.agr.AGRSpace;
import jadex.application.space.agr.Group;
import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.bdi.examples.marsworld.RequestProduction;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

import java.util.HashMap;
import java.util.Map;


/**
 *  Inform the sentry agent about a new target.
 */
public class AnalyzeTargetPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject target = (ISpaceObject)getParameter("target").getValue();

		// Move to the target.
		IGoal go_target = createGoal("move.move_dest");
		go_target.getParameter("destination").setValue(target.getProperty(Space2D.PROPERTY_POSITION));
		dispatchSubgoalAndWait(go_target);

		// Analyse the target.
		try
		{
			ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			SyncResultListener	res	= new SyncResultListener();
			Map props = new HashMap();
			props.put(AnalyzeTargetTask.PROPERTY_TARGET, target);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			Object	taskid	= space.createObjectTask(AnalyzeTargetTask.PROPERTY_TYPENAME, props, myself.getId());
			space.addTaskListener(taskid, myself.getId(), res);

			res.waitForResult();
//			System.out.println("Analyzed target: "+getAgentName()+", "+ore+" ore found.");
			if(((Number)target.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue()>0)
				callProducerAgent(target);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// Fails for one agent, when two agents try to analyze the same target at once.
		}
	}

	/**
	 *  Sending a location to the Producer Agent.
	 *  Therefore it has first to be looked up in the DF.
	 *  @param target
	 */
	private void callProducerAgent(ISpaceObject target)
	{
//		System.out.println("Calling some Production Agent...");

		AGRSpace agrs = (AGRSpace)((IApplicationExternalAccess)getScope().getParent()).getSpace("myagrspace");
		Group group = agrs.getGroup("mymarsteam");
		IComponentIdentifier[]	producers	= group.getAgentsForRole("producer");

		if(producers!=null && producers.length>0)
		{
			int sel = (int)(Math.random()*producers.length); // todo: Select not randomly
//			System.out.println("Found agents: "+producers.length+" selected: "+sel);

			RequestProduction rp = new RequestProduction(target);
			//Action action = new Action();
			//action.setAction(rp);
			//action.setActor(SJade.convertAIDtoJade(producers[sel].getName()));
			IMessageEvent mevent = createMessageEvent("request_producer");
			mevent.getParameterSet(SFipa.RECEIVERS).addValue(producers[sel]);
			mevent.getParameter(SFipa.CONTENT).setValue(rp);
			sendMessage(mevent);
//			System.out.println("Sentry Agent: sent location to: "+producers[sel].getName());
		}
	}
}
