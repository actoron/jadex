package jadex.agentkeeper.ai.base;

import jadex.agentkeeper.ai.creatures.orc.OrcBDI.AchieveMoveToSector;
import jadex.agentkeeper.ai.creatures.orc.OrcBDI;
import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Move to a Location on the Grid
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class MoveToGridSectorPlan
{
	@PlanCapability
	protected OrcBDI				capa;

	@PlanPlan
	protected RPlan					rplan;

	@PlanReason
	protected AchieveMoveToSector	goal;

	private AStarSearch				astar;

	private Iterator<Vector2Int>				pathit;

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

			System.out.println("erreichbar!");

			ArrayList<Vector2Int> path = astar.gibPfadInverted();

			pathit = path.iterator();

			moveToNextSector(pathit).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setException(new RuntimeException("Not reachable: " + target));
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

			oneStepToTarget(nextTarget).addResultListener(new DelegationResultListener<Void>(ret)
			{
				
				public void customResultAvailable(Void result)
				{
					System.out.println("custom result");
					moveToNextSector(pathit).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}

		return ret;
	}

	private IFuture<Void> oneStepToTarget(Vector2Int nextTarget)
	{
		Future<Void> ret = new Future<Void>();
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, nextTarget);
		props.put(MoveTask.PROPERTY_SPEED, capa.getMySpeed());
		props.put(MoveTask.PROPERTY_AGENT, capa);

		Object mtaskid = capa.getEnvironment().createObjectTask(MoveTask.PROPERTY_TYPENAME, props, capa.getMySpaceObject().getId());
		capa.getEnvironment().addTaskListener(mtaskid, capa.getMySpaceObject().getId(), new DelegationResultListener<Void>(ret));

		return ret;
	}
}
