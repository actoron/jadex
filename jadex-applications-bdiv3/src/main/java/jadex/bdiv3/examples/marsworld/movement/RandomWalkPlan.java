package jadex.bdiv3.examples.marsworld.movement;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.Move;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability.WalkAround;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  Wander around randomly.
 */
@Plan
public class RandomWalkPlan
{
	//-------- attributes --------

	@PlanCapability
	protected MovementCapability capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected WalkAround goal;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public RandomWalkPlan()
	{
		//getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
//		System.out.println("RandomWalk: "+capa.getCapability().getAgent().getComponentIdentifier());
		IVector2	dest	= capa.getEnvironment().getRandomPosition(Vector2Int.ZERO);
		Move moveto = capa.new Move(dest);
		rplan.dispatchSubgoal(moveto).get();
//		System.out.println("Reached point: "+dest);
	}
}
