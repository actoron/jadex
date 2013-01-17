package jadex.bdiv3.example.cleanerworld.cleaner;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchieveDropWaste;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchievePickupWaste;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.DropWasteAction;
import jadex.bdiv3.example.cleanerworld.world.Location;
import jadex.bdiv3.example.cleanerworld.world.Waste;
import jadex.bdiv3.example.cleanerworld.world.Wastebin;
import jadex.bdiv3.runtime.PlanFailureException;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  Clean-up some waste.
 */
public class DropWastePlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected RPlan rplan;
	
	@PlanReason
	protected AchieveDropWaste goal;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public DropWastePlan()
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
		
		final Waste waste = capa.getCarriedWaste();
//		System.out.println("carriedwaste a ="+waste);
//		if(waste==null)
//			System.out.println("here");
		
		// Move to a not full waste-bin
		final Wastebin wastebin = goal.getWastebin();
		if(wastebin==null)
			throw new PlanFailureException();

		Location location = wastebin.getLocation();
		
		rplan.dispatchSubgoal(capa.new AchieveMoveTo(location))
			.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveTo amt)
			{
				rplan.dispatchSubgoal(capa.new DropWasteAction(waste, wastebin))
					.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.DropWasteAction, Void>(ret)
				{
					public void customResultAvailable(DropWasteAction result)
					{
						wastebin.addWaste(waste);
						capa.setCarriedwaste(null);
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
			
//		// Drop waste to waste-bin.
//		//IEnvironment env = (IEnvironment)getBeliefbase().getBelief("environment").getFact();
//		//boolean success = env.dropWasteInWastebin(waste, wastebin);
//		IGoal dg = createGoal("drop_waste_action");
//		dg.getParameter("waste").setValue(waste);
//		dg.getParameter("wastebin").setValue(wastebin);
//		dispatchSubgoalAndWait(dg);
//
//		// Update beliefs.
////		getLogger().info("Dropping waste to wastebin!");
//		wastebin.addWaste(waste);
//
//		// Todo: Find out why atomic is needed.
////		startAtomic();
//		IBeliefSet wbs = getBeliefbase().getBeliefSet("wastebins");
//		if(wbs.containsFact(wastebin))
//		{
//			((Wastebin)wbs.getFact(wastebin)).update(wastebin);
////			wbs.updateFact(wastebin);
//		}
//		else
//		{
//			wbs.addFact(wastebin);
//		}
//		//getBeliefbase().getBeliefSet("wastebins").updateOrAddFact(wastebin);
//		getBeliefbase().getBelief("carriedwaste").setFact(null);
////		System.out.println("carriedwaste b =null");
//		endAtomic();
	}
}
