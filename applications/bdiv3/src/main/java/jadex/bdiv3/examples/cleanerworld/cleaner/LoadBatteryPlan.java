package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.examples.cleanerworld.world.Chargingstation;
import jadex.bdiv3.runtime.IPlan;


/**
 *  Go to the charging station and load the battery.
 */
@Plan
public class LoadBatteryPlan
{
	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body(CleanerAgent agentapi, IPlan planapi)
	{
		Chargingstation station = ((CleanerAgent.QueryChargingStation)planapi.dispatchSubgoal(agentapi.new QueryChargingStation()).get()).getStation();
		planapi.dispatchSubgoal(agentapi.new AchieveMoveTo(station.getLocation())).get();
		
		double charge = agentapi.getMyChargestate();
		while(charge<1 && agentapi.getMyLocation().getDistance(station.getLocation())<0.01)
		{
			charge	= Math.min(charge + 0.01, 1.0);
			agentapi.setMyChargestate(charge);
			planapi.waitFor(100).get();
		}
	}
	
//	@PlanCapability
//	protected CleanerBDI agentapi;
	
//	@PlanAPI
//	protected IPlan planapi;
//	
//	@PlanReason
//	protected MaintainBatteryLoaded goal;

//	/**
//	 *  The plan body.
//	 */
//	@PlanBody
//	public IFuture<Void> body()
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		// Move to station.
//		IFuture<QueryChargingStation> fut = rplan.dispatchSubgoal(capa.new QueryChargingStation());
//		fut.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.QueryChargingStation, Void>(ret)
//		{
//			public void customResultAvailable(CleanerBDI.QueryChargingStation qcs)
//			{
//				final Chargingstation station = qcs.getStation();
//				IFuture<AchieveMoveTo> fut = rplan.dispatchSubgoal(capa.new AchieveMoveTo(station.getLocation()));
//				fut.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
//				{
//					public void customResultAvailable(AchieveMoveTo amt)
//					{
//						IComponentStep<Void> loadstep = new IComponentStep<Void>()
//						{
//							public IFuture<Void> execute(IInternalAccess ia) 
//							{
//								final IComponentStep<Void> self = this;
//								
//								double charge = capa.getMyChargestate();
//								if(capa.getMyLocation().getDistance(station.getLocation())<0.01 && charge<1.0)
//								{
//									charge	= Math.min(charge + 0.01, 1.0);
//									capa.setMyChargestate(charge);
//								}
//								if(charge>=1.0)
//								{
////									System.out.println("finished loading");
//									ret.setResult(null);
//								}
//								else
//								{
//									rplan.waitFor(100).addResultListener(new DefaultResultListener<Void>()
//									{
//										public void resultAvailable(Void result)
//										{
//											capa.getAgent().scheduleStep(self);
//										}
//									});
//								}
//								return IFuture.DONE;
//							};
//						};
//						capa.getAgent().scheduleStep(loadstep);
//					}
//				});
//			}
//		});
//		
//		return ret;
//	}

}
