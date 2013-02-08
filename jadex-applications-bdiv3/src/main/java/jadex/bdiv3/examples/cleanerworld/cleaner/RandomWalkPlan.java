package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;



/**
 *  Wander around randomly.
 */
public class RandomWalkPlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected IPlan rplan;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public RandomWalkPlan()
	{
//		getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		double x_dest = Math.random();
		double y_dest = Math.random();
		Location dest = new Location(x_dest, y_dest);
		
		rplan.dispatchSubgoal(capa.new AchieveMoveTo(dest))
			.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveTo amt)
			{
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
