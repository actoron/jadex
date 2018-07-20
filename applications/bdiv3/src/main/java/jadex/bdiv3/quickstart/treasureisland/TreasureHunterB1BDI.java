package jadex.bdiv3.quickstart.treasureisland;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.quickstart.treasureisland.environment.Treasure;
import jadex.bdiv3.quickstart.treasureisland.environment.TreasureHunterEnvironment;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  A treasure hunter that picks up the treasures one by one
 *  by creating a goal and thus a plan for each treasure.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class TreasureHunterB1BDI
{
	//-------- beliefs --------
	
	/** The treasure hunter world object. */
	@Belief
	protected TreasureHunterEnvironment	env	= new TreasureHunterEnvironment();
	
	//-------- goals --------
	
	/**
	 *  A goal to collect a given treasure.
	 */
	@Goal
	public class CollectTreasureGoal
	{
		/** The treasure to collect. */
		protected Treasure treasure;

		/**
		 *  Create a collect treasure goal for a given treasure:
		 *  @param treasure	The treasure.
		 */
		public CollectTreasureGoal(Treasure treasure)
		{
			this.treasure	= treasure;
		}
	}
	
	//-------- plans --------
	
	/**
	 *  A plan to collect a treasure.
	 */
	@Plan(trigger=@Trigger(goals={CollectTreasureGoal.class}))
	public void	collectTreasurePlan(CollectTreasureGoal goal)
	{
		// Move towards treasure location
		double	dx	= goal.treasure.getLocation().getX() - env.getHunterLocation().getX();
		double	dy	= goal.treasure.getLocation().getY() - env.getHunterLocation().getY();
		env.move(dx, dy).get();
		
		// Then, pick up the treasure
		env.pickUp(goal.treasure).get();
	}
	
	//-------- agent life cycle --------
	
	/**
	 *  Agent body is implemented as a loop that runs until no more treasures available.
	 *  @param agent	The agent parameter is optional and allows to access bdi agent functionality.
	 */
	@AgentBody
	public void	body(IBDIAgentFeature agent)
	{
		// Continue until no more treasures.
		while(!env.getTreasures().isEmpty())
		{
			// Fetch next treasure.
			Treasure	treasure	= env.getTreasures().iterator().next();
			
			// Create the goal object.
			CollectTreasureGoal	ctgoal	= new CollectTreasureGoal(treasure);
			
			// Dispatch the goal in the agent.
			IFuture<Void>	fut	= agent.dispatchTopLevelGoal(ctgoal);
			
			// Wait for the goal to finish.
			fut.get();
		}
	}
}
