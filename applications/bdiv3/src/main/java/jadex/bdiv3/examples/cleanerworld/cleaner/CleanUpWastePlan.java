package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchieveCleanup;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchieveDropWaste;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchievePickupWaste;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.QueryWastebin;
import jadex.bdiv3.examples.cleanerworld.world.Waste;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;


/**
 *  Clean-up some waste.
 */
@Plan
public class CleanUpWastePlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
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
		
//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//			}
//			
//			public void exceptionOccurred(Exception exception) 
//			{
//				if(!(exception instanceof PlanAbortedException))
//					exception.printStackTrace();
//			}
//		});
		
//		System.out.println("Clean-up waste plan started: "+goal.getWaste());

		if(capa.getCarriedWaste()==null)
		{
			// todo: hack, depends on goal type
			Waste waste = goal.getWaste();
			
			IFuture<AchievePickupWaste> fut = rplan.dispatchSubgoal(capa.new AchievePickupWaste(waste));
			fut.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchievePickupWaste, Void>(ret)
			{
				public void customResultAvailable(AchievePickupWaste apw)
				{
//					System.out.println("picked up waste: "+goal.getWaste());
					
					dropWaste().addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			dropWaste().addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> dropWaste()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<QueryWastebin> fut = rplan.dispatchSubgoal(capa.new QueryWastebin());
		fut.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.QueryWastebin, Void>(ret)
		{
			public void customResultAvailable(QueryWastebin qw)
			{
//				System.out.println("found wastebin: "+qw.getWastebin());
				
				IFuture<AchieveDropWaste> fut = rplan.dispatchSubgoal(capa.new AchieveDropWaste(qw.getWastebin()));
				fut.addResultListener(new IResultListener<CleanerBDI.AchieveDropWaste>()
				{
					public void resultAvailable(AchieveDropWaste result)
					{
//						System.out.println("clean-up waste plan succ: "+goal.getWaste());
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
		
		return ret;
	}
}
