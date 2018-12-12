package jadex.bdiv3.examples.disastermanagement.firebrigade;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.disastermanagement.DisasterType;
import jadex.bdiv3.examples.disastermanagement.ExtinguishFireTask;
import jadex.bdiv3.examples.disastermanagement.firebrigade.FireBrigadeAgent.ExtinguishFire;
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
 *  Move to fire and extinguish it.
 */
@Plan
public class ExtinguishFirePlan
{
	//-------- attributes --------

	@PlanCapability
	protected FireBrigadeAgent capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected ExtinguishFire goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		Space2D	space	= (Space2D)capa.getMoveCapa().getEnvironment();
		ISpaceObject myself	= capa.getMoveCapa().getMyself();
		ISpaceObject disaster = (ISpaceObject)goal.getDisaster();
		
		// Move to disaster location
		myself.setProperty("state", "moving_to_disaster");
		IVector2	targetpos	= DisasterType.getFireLocation(disaster);
		Move move = capa.getMoveCapa().new Move(targetpos);
		rplan.dispatchSubgoal(move).get();
		
		// Extinguish fire
		myself.setProperty("state", "extinguishing_fire");
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ExtinguishFireTask.PROPERTY_DISASTER, disaster);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
		Object taskid = space.createObjectTask(ExtinguishFireTask.PROPERTY_TYPENAME, props, myself.getId());
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
		space.addTaskListener(taskid, myself.getId(), lis);
		fut.get();
	}
	
//	/**
//	 *  Called when a plan fails.
//	 */
//	public void failed()
//	{
//		System.out.println("Plan failed: "+this);
//		getException().printStackTrace();
//	}
}
