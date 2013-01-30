package jadex.agentkeeper.ai.base;

import jadex.agentkeeper.ai.AbstractBeingBDI;
import jadex.agentkeeper.ai.AbstractBeingBDI.AchieveMoveToSector;
import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.agentkeeper.util.ISpaceObjectStrings;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Move to a Location on the Grid
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class MoveToGridSectorPlan
{
	@PlanCapability
	protected AbstractBeingBDI				capa;

	@PlanPlan
	protected RPlan					rplan;

	@PlanReason
	protected AchieveMoveToSector	goal;

	private AStarSearch				astar;

	private Iterator<Vector2Int>	path_iterator;

	Vector2Double					myloc;
	
	private ISpaceObject spaceObject;

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
		
		spaceObject = capa.getMySpaceObject();
		Vector2Int target = goal.getTarget();
		Vector2Double myloc = (Vector2Double)spaceObject.getProperty(Space2D.PROPERTY_POSITION);
		
		

		// TODO: refractor AStar-Search
		astar = new AStarSearch(myloc.copy(), target, capa.getEnvironment(), true);

		if(astar.istErreichbar())
		{

			ArrayList<Vector2Int> path = astar.gibPfadInverted();

			path_iterator = path.iterator();
			
			spaceObject.setProperty(ISpaceObjectStrings.PROPERTY_STATUS, "Walk");

			moveToNextSector(path_iterator).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setException(new RuntimeException("Not reachable: " + target));
		}


		return ret;
	}


	/**
	 * Iterative Method
	 * @param it iterator
	 * @return empty result when finished
	 */
	private IFuture<Void> moveToNextSector(final Iterator<Vector2Int> it)
	{


		final Future<Void> ret = new Future<Void>();
		if(it.hasNext())
		{
			Vector2Int nextTarget = it.next();
			
//			capa.getUpdatedPosition(); 
			// Hack to Update the Belief-Position to
			// Trigger the GoalTargetCondition

			oneStepToTarget(nextTarget).addResultListener(new DelegationResultListener<Void>(ret)
			{

				public void customResultAvailable(Void result)
				{
					moveToNextSector(path_iterator).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}

		return ret;
	}

	/**
	 * We use the MoveTask for the "moving" in the virtual World.
	 * 
	 * @param nextTarget
	 * @return
	 */
	private IFuture<Void> oneStepToTarget(Vector2Int nextTarget)
	{
		final Future<Void> ret = new Future<Void>();
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, nextTarget);
		props.put(MoveTask.PROPERTY_SPEED, capa.getMySpeed());
		props.put(MoveTask.PROPERTY_AGENT, capa);

		Object mtaskid = capa.getEnvironment().createObjectTask(MoveTask.PROPERTY_TYPENAME, props, capa.getMySpaceObject().getId());
		capa.getEnvironment().addTaskListener(mtaskid, capa.getMySpaceObject().getId(), new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(null);
			}
		});

		return ret;
	}
}
