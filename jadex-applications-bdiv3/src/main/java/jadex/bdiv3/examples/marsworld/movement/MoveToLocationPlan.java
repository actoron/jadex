package jadex.bdiv3.examples.marsworld.movement;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.Move;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 *  The move to a location plan.
 */
@Plan
public class MoveToLocationPlan 
{
	//-------- attributes --------

	@PlanCapability
	protected MovementCapability capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected Move goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		Future<Void> ret = new Future<Void>();
//		System.out.println("MoveToLocation: "+getComponentIdentifier());
		
		ISpaceObject myself	= capa.getMyself();
		IVector2 dest = goal.getDestination();
		
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, capa.getCapability().getAgent().getExternalAccess());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
		IEnvironmentSpace space = capa.getEnvironment();
		
		CounterResultListener<Void> lis = new CounterResultListener<Void>(2, new DelegationResultListener<Void>(ret));
		
		Object rtaskid = space.createObjectTask(RotationTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(rtaskid, myself.getId(), lis);
		
		Object mtaskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(mtaskid, myself.getId(), lis);
		
		return ret;
	}
}