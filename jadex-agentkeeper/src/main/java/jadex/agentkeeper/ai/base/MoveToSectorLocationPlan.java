package jadex.agentkeeper.ai.base;

import jadex.agentkeeper.ai.AbstractBeingBDI;
import jadex.agentkeeper.ai.AbstractBeingBDI.AchieveMoveToSector;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.Iterator;
import java.util.Stack;

import jadex.agentkeeper.ai.pathfinding.*;

/**
 * Move to a Location on the Grid
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class MoveToSectorLocationPlan
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
	public MoveToSectorLocationPlan()
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

	//TODO: Why the Iterator final?
	private IFuture<Void> moveToNextSector(final Iterator<Vector2Int> it)
	{
		final Future<Void> ret = new Future<Void>();
		if(it.hasNext())
		{
			Vector2Int nextTarget = it.next();
			
			System.out.println("nextTarget " + nextTarget);

			
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}


}
