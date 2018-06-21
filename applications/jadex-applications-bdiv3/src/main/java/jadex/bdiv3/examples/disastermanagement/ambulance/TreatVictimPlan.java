package jadex.bdiv3.examples.disastermanagement.ambulance;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.disastermanagement.DeliverPatientTask;
import jadex.bdiv3.examples.disastermanagement.DisasterType;
import jadex.bdiv3.examples.disastermanagement.TreatVictimTask;
import jadex.bdiv3.examples.disastermanagement.ambulance.AmbulanceBDI.TreatVictims;
import jadex.bdiv3.examples.disastermanagement.movement.MovementCapa.Move;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Move to victim and treat her.
 */
@Plan
public class TreatVictimPlan
{
	//-------- attributes --------

	@PlanCapability
	protected AmbulanceBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected TreatVictims goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
//		System.out.println("treat vic plan for: "+goal);
		
		Space2D	space	= (Space2D)capa.getMoveCapa().getEnvironment();
		ISpaceObject myself	= capa.getMoveCapa().getMyself();
		IVector2 home = capa.getMoveCapa().getHomePosition();
		ISpaceObject disaster = (ISpaceObject)goal.getDisaster();
		
		// Move to disaster location
		myself.setProperty("state", "moving_to_disaster");
		IVector2	targetpos	= DisasterType.getVictimLocation(disaster);
		Move move = capa.getMoveCapa().new Move(targetpos);
		rplan.dispatchSubgoal(move).get();
//		System.out.println("disaster reached: "+goal);
		
		// Treat victim.
		myself.setProperty("state", "treating_victim");
		Map props = new HashMap();
		props.put(TreatVictimTask.PROPERTY_DISASTER, disaster);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
		Object taskid = space.createObjectTask(TreatVictimTask.PROPERTY_TYPENAME, props, myself.getId());
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
		space.addTaskListener(taskid, myself.getId(), lis);
		fut.get();
//		System.out.println("victim treated: "+goal);
		
		// Move to hospital
		myself.setProperty("state", "moving_to_hospital");
		move = capa.getMoveCapa().new Move(home);
		rplan.dispatchSubgoal(move).get();
//		System.out.println("moved to hospital: "+goal);

		//  Deliver patient.
		myself.setProperty("state", "delivering_patient");
		props = new HashMap();
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
		taskid = space.createObjectTask(DeliverPatientTask.PROPERTY_TYPENAME, props, myself.getId());
		fut = new Future<Void>();
		lis = new DelegationResultListener<Void>(fut, true);
		space.addTaskListener(taskid, myself.getId(), lis);
		fut.get();
//		System.out.println("patient delivered (end): "+goal);

	}
	
	/**
	 *  Called when a plan fails.
	 */
	@PlanFailed
	@PlanAborted
	public void failed(Exception e)
	{
//		System.out.println("Plan failed for: "+goal+" "+e);
//		e.printStackTrace();
	}
}
