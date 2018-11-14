package jadex.bdiv3.examples.cleanerworld.cleaner;

import java.util.Iterator;
import java.util.List;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerAgent.AchieveMoveTo;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;



/**
 *  Patrol along the patrol points.
 */
@Plan
public class PatrolPlan
{
	@PlanCapability
	protected CleanerAgent capa;
	
	@PlanAPI
	protected IPlan rplan;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public PatrolPlan()
	{
//		getLogger().info("Created: "+this);
//		System.out.println("created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		List<Location> loci = capa.getPatrolPoints();
		moveToLocations(loci.iterator()).addResultListener(new DelegationResultListener<Void>(ret));
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> moveToLocations(final Iterator<Location> it)
	{
		final Future<Void> ret = new Future<Void>();

		if(it.hasNext())
		{
			Location loc = it.next();
//			if(loc.getX()==0.1 && loc.getY()==0.1)
//				System.out.println("patrol to: "+loc);
			IFuture<AchieveMoveTo> fut = rplan.dispatchSubgoal(capa.new AchieveMoveTo(loc));
			fut.addResultListener(new ExceptionDelegationResultListener<CleanerAgent.AchieveMoveTo, Void>(ret)
			{
				public void customResultAvailable(AchieveMoveTo mtg)
				{
					moveToLocations(it).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
//	@PlanPassed
//	@PlanFailed
//	@PlanAborted
//	public void end()
//	{
//		System.out.println("patrol end");
//	}
}
