package jadex.agentkeeper.ai.base;

import jadex.agentkeeper.ai.AbstractBeingBDI;
import jadex.agentkeeper.ai.AbstractBeingBDI.AchieveMoveToSector;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.Plan.SyncResultListener;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import com.jme3.audio.Environment;

import jadex.agentkeeper.ai.pathfinding.*;

/**
 * Move to a Location on the Grid
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class MoveToGridSectorPlan
{
	@PlanCapability
	protected AbstractBeingBDI		capa;

	@PlanPlan
	protected RPlan					rplan;

	@PlanReason
	protected AchieveMoveToSector	goal;

	private AStarSearch				astar;

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public MoveToGridSectorPlan()
	{

		// getLogger().info("Created: "+this);
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		Vector2Int target = goal.getTarget();
		Vector2Double myloc = capa.getMyPosition();
		
		// TODO: refractor AStar-Search
		astar = new AStarSearch(myloc, target, capa.getEnvironment(), true);
		
		// TODO: what to do if not reachable? fail the plan?
		if(astar.istErreichbar())
		{
			Stack<Vector2Int> path = astar.gibPfad();
			moveToNextSector(path.iterator()).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setException(new RuntimeException("Not reachable: "+target));
		}

		return ret;
	}

	
	private IFuture<Void> moveToNextSector(final Iterator<Vector2Int> it)
	{
		final Future<Void> ret = new Future<Void>();
		if(it.hasNext())
		{
			Vector2Int nextTarget = it.next();
			
			System.out.println("nextTarget " + nextTarget);
			
			oneStepToTarget(nextTarget)
				.addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	private IFuture<Void> oneStepToTarget(Vector2Int nextTarget)
	{
		Future<Void>	ret = new Future<Void>();
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, nextTarget);

		//TODO: how?
//		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		
		
//		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
//		myself.getId();
		
		Object mtaskid = capa.getEnvironment().createObjectTask(MoveTask.PROPERTY_TYPENAME, props, rplan.getId());
		capa.getEnvironment().addTaskListener(mtaskid, rplan.getId(),
			new DelegationResultListener<Void>(ret));
		
		return ret;
	}
}

