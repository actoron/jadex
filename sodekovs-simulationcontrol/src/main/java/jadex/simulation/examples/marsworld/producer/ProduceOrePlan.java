package jadex.simulation.examples.marsworld.producer;

import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;


/**
 *  Inform the sentry agent about a new target.
 */
public class ProduceOrePlan extends Plan
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

		// Produce ore at the target.
		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("move.myself").getFact();
		SyncResultListener	res	= new SyncResultListener();
		Map props = new HashMap();
		props.put(ProduceOreTask.PROPERTY_TARGET, target);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
		Object taskid	= space.createObjectTask(ProduceOreTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
//		System.out.println("Produced ore at target: "+getAgentName()+", "+ore+" ore produced.");
	}
}
