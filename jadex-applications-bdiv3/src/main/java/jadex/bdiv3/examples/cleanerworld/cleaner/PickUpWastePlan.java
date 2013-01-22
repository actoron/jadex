package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchievePickupWaste;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.PickupWasteAction;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.examples.cleanerworld.world.Waste;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  Clean-up some waste.
 */
public class PickUpWastePlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected RPlan rplan;
	
	@PlanReason
	protected AchievePickupWaste goal;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public PickUpWastePlan()
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
		
		final Waste waste = goal.getWaste();

		// Move to the waste position when necessary
//		getLogger().info("Moving to waste!");
		
		rplan.dispatchSubgoal(capa.new AchieveMoveTo(waste.getLocation()))
			.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveTo amt)
			{
				rplan.dispatchSubgoal(capa.new PickupWasteAction(waste))
					.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.PickupWasteAction, Void>(ret)
				{
					public void customResultAvailable(PickupWasteAction pwa)
					{
						capa.setCarriedwaste(waste);
						capa.getWastes().remove(waste);
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}	
}
