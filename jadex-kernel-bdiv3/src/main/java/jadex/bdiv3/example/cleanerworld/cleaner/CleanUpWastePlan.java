package jadex.bdiv3.example.cleanerworld.cleaner;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchieveCleanup;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchieveDropWaste;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchievePickupWaste;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.QueryWastebin;
import jadex.bdiv3.example.cleanerworld.world.Waste;
import jadex.bdiv3.example.cleanerworld.world.Wastebin;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;


/**
 *  Clean-up some waste.
 */
public class CleanUpWastePlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected RPlan rplan;
	
	@PlanReason
	protected AchieveCleanup goal;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public CleanUpWastePlan()
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
		
		System.out.println("Clean-up waste plan started.");

		if(capa.getCarriedWaste()==null)
		{
			// todo: hack, depends on goal type
			Waste waste = goal.getWaste();
			
			rplan.dispatchSubgoal(capa.new AchievePickupWaste(waste)).addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchievePickupWaste, Void>(ret)
			{
				public void customResultAvailable(AchievePickupWaste apw)
				{
					rplan.dispatchSubgoal(capa.new QueryWastebin()).addResultListener(new ExceptionDelegationResultListener<CleanerBDI.QueryWastebin, Void>(ret)
					{
						public void customResultAvailable(QueryWastebin qw)
						{
							rplan.dispatchSubgoal(capa.new AchieveDropWaste(qw.getResult())).addResultListener(new IResultListener<CleanerBDI.AchieveDropWaste>()
							{
								public void resultAvailable(AchieveDropWaste result)
								{
									ret.setResult(null);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									// todo: retry QueryWastebin() goal to find other wastebin
									ret.setException(exception);
								}
							});
						}
					});
				}
			});
		}
		
		return ret;
	}
}
