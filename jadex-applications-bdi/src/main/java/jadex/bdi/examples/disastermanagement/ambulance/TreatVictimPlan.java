package jadex.bdi.examples.disastermanagement.ambulance;

import jadex.bdi.examples.disastermanagement.DeliverPatientTask;
import jadex.bdi.examples.disastermanagement.DisasterType;
import jadex.bdi.examples.disastermanagement.TreatVictimTask;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 *  Move to victim and treat her.
 */
public class TreatVictimPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D	space	= (Space2D)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2	home	= (IVector2)getBeliefbase().getBelief("home").getFact();
		ISpaceObject	disaster	= (ISpaceObject)getParameter("disaster").getValue();
		
		// Move to disaster location
		myself.setProperty("state", "moving_to_disaster");
		IVector2	targetpos	= DisasterType.getVictimLocation(disaster);
		IGoal move = createGoal("move");
		move.getParameter("destination").setValue(targetpos);
		dispatchSubgoalAndWait(move);
		
		// Treat victim.
		myself.setProperty("state", "treating_victim");
		Map props = new HashMap();
		props.put(TreatVictimTask.PROPERTY_DISASTER, disaster);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = space.createObjectTask(TreatVictimTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener	res	= new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
		
		// Move to hospital
		myself.setProperty("state", "moving_to_hospital");
		move = createGoal("move");
		move.getParameter("destination").setValue(home);
		dispatchSubgoalAndWait(move);
		
		//  Deliver patient.
		myself.setProperty("state", "delivering_patient");
		props = new HashMap();
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		taskid = space.createObjectTask(DeliverPatientTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}
	
	/**
	 *  Called when a plan fails.
	 */
	public void failed()
	{
		System.err.println("Plan failed: "+this);
		getException().printStackTrace();
	}
}
