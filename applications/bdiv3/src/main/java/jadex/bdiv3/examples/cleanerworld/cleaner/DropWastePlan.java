package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerAgent.AchieveDropWaste;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerAgent.AchieveMoveTo;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerAgent.DropWasteAction;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.examples.cleanerworld.world.Waste;
import jadex.bdiv3.examples.cleanerworld.world.Wastebin;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  Clean-up some waste.
 */
@Plan
public class DropWastePlan
{
	@PlanCapability
	protected CleanerAgent capa;
	
	@PlanAPI
	protected IPlan rplan;
	
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
		{
			ret.setException(new PlanFailureException());
			return ret;
//			throw new PlanFailureException();
		}
		
		Location location = wastebin.getLocation();
		
		IFuture<AchieveMoveTo> fut = rplan.dispatchSubgoal(capa.new AchieveMoveTo(location));
		fut.addResultListener(new ExceptionDelegationResultListener<CleanerAgent.AchieveMoveTo, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveTo amt)
			{
				IFuture<DropWasteAction> fut = rplan.dispatchSubgoal(capa.new DropWasteAction(waste, wastebin));
				fut.addResultListener(new ExceptionDelegationResultListener<CleanerAgent.DropWasteAction, Void>(ret)
				{
					public void customResultAvailable(DropWasteAction result)
					{
						wastebin.addWaste(waste);
						capa.setCarriedwaste(null);
//						System.out.println("carried waste set null: "+rplan.getId()+" "+((IGoal)rplan.getReason()).getId());
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
