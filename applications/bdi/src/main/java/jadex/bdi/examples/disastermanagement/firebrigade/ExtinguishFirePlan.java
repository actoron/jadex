package jadex.bdi.examples.disastermanagement.firebrigade;

import java.util.HashMap;
import java.util.Map;

import jadex.bdi.examples.disastermanagement.DisasterType;
import jadex.bdi.examples.disastermanagement.ExtinguishFireTask;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Move to fire and extinguish it.
 */
public class ExtinguishFirePlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D	space	= (Space2D)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		Object	disasterId	= getParameter("disasterId").getValue();
		ISpaceObject disaster = space.getSpaceObject0(disasterId);
//		if (disaster == null) {
//			System.out.println("skipping plan, no disaster with id="+disasterId);
//			return;
//		}


		// Move to disaster location
		myself.setProperty("state", "moving_to_disaster");
		IVector2	targetpos	= DisasterType.getFireLocation(disaster);
		IGoal move = createGoal("move");
		move.getParameter("destination").setValue(targetpos);
		dispatchSubgoalAndWait(move);
		
		// Extinguish fire
		myself.setProperty("state", "extinguishing_fire");
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ExtinguishFireTask.PROPERTY_DISASTER, disaster);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = space.createObjectTask(ExtinguishFireTask.PROPERTY_TYPENAME, props, myself.getId());
		Future<Void> fut = new Future<Void>();
		space.addTaskListener(taskid, myself.getId(), new DelegationResultListener<Void>(fut));
		fut.get();
	}
	
	/**
	 *  Called when a plan fails.
	 */
	public void failed()
	{
//		System.err.println("Plan failed: "+this);
//		getException().printStackTrace();
	}
}
