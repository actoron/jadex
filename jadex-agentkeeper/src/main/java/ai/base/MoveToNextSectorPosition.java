package ai.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;
import agentkeeper.wegfindung.ASternSuche;
import ai.AbstractBeingBDI;
import ai.AbstractBeingBDI.AchieveMoveToSector;
import ai.AbstractBeingBDI.PerformMoveToNextSector;

/**
 * Move from an Sector on the Grid to the Next One
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 *
 */
public class MoveToNextSectorPosition
{
	@PlanCapability
	protected AbstractBeingBDI		capa;

	@PlanPlan
	protected RPlan					rplan;

	@PlanReason
	protected PerformMoveToNextSector	goal;
	
	/**
	 * The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		return performMoveTask();
	}
	
	/**
	 * Use the Move Task to Move the "Being"
	 */
	protected IFuture<Void> performMoveTask()
	{
		final Future<Void> ret = new Future<Void>();
		
		Vector2Int next = goal.getNext();
		Vector2Double myloc = capa.getMyPosition();
		
//		Map props = new HashMap();
//		props.put(MoveTask.PROPERTY_DESTINATION, dest);
//		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
//		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		
		return ret;
	}


}
