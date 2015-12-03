package jadex.bdiv3.examples.marsworld.movement;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;

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
	protected IDestinationGoal goal;
	
	/**
	 *  The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
//		System.out.println("MoveToLocation: "+capa.getMyself());
		
		ISpaceObject myself	= capa.getMyself();
		Object dest = goal.getDestination();
		
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, capa.getCapability().getAgent().getExternalAccess());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(rplan));
		IEnvironmentSpace space = capa.getEnvironment();
		
//		System.out.println("move body start: "+rplan);
			
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
		Object rtaskid = space.createObjectTask(RotationTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(rtaskid, myself.getId(), lis);
		fut.get();
//		System.out.println("move after first task: "+rplan);
		
		fut = new Future<Void>();
		lis = new DelegationResultListener<Void>(fut, true);
		Object mtaskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(mtaskid, myself.getId(), lis);
		fut.get();
//		System.out.println("move after second task: "+rplan);
		
//		System.out.println("Moved to location: "+capa.getMyself());
		
		return IFuture.DONE;
	}
	
//	@PlanAborted
//	public void aborted()
//	{
//		System.out.println("aborted: "+this);
//	}
//	
//	@PlanFailed
//	public void failed(Exception e)
//	{
//		if(e!=null)
//			e.printStackTrace();
//		System.out.println("failed: "+this);
//	}
//	
//	@PlanPassed
//	public void passed()
//	{
//		System.out.println("passed: "+this);
//	}
}