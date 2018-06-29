package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchievePickupWaste;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.PickupWasteAction;
import jadex.bdiv3.examples.cleanerworld.world.Waste;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  Clean-up some waste.
 */
@Plan
public class PickUpWastePlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
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
		
		IFuture<AchieveMoveTo> fut = rplan.dispatchSubgoal(capa.new AchieveMoveTo(waste.getLocation()));
		fut.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveTo amt)
			{
				IFuture<PickupWasteAction> fut = rplan.dispatchSubgoal(capa.new PickupWasteAction(waste));
				fut.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.PickupWasteAction, Void>(ret)
				{
					public void customResultAvailable(PickupWasteAction pwa)
					{
						capa.setCarriedwaste(waste);
//						System.out.println("carried waste set to: "+waste+rplan.getId()+" "+((IGoal)rplan.getReason()).getId());
						capa.getWastes().remove(waste);
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}	
}
