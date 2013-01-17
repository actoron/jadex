package jadex.bdiv3.example.cleanerworld.cleaner;

import java.util.Iterator;
import java.util.List;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.example.cleanerworld.world.Location;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;



/**
 *  Patrol along the patrol points.
 */
public class PatrolPlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected RPlan rplan;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public PatrolPlan()
	{
//		getLogger().info("Created: "+this);
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
			System.out.println("patrol to: "+loc);
			rplan.dispatchSubgoal(capa.new AchieveMoveTo(loc))
				.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
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
}
