package jadex.bdi.examples.marsworld.movement;

import jadex.bdi.examples.marsworld.carry.O;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 *  The move to a location plan.
 */
public class MoveToLocationPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("MoveToLocation: "+getComponentIdentifier());
		
		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2 dest = (IVector2)getParameter("destination").getValue();
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		
		Object rtaskid = space.createObjectTask(RotationTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener	res	= new SyncResultListener();
		space.addTaskListener(rtaskid, myself.getId(), res);
		res.waitForResult();
		
		Object mtaskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		res	= new SyncResultListener();
		space.addTaskListener(mtaskid, myself.getId(), res);
		res.waitForResult();
	}
	
	@Override
	public void failed() {
		super.failed();
		System.out.println("failed: "+getException());
		getException().printStackTrace();
	}
}