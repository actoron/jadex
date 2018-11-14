package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  Move to a point.
 */
@Plan
public class MoveToLocationPlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected AchieveMoveTo goal;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public MoveToLocationPlan()
	{
//		capa.getAgent().getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public IFuture<Void> body()
	{
		return moveToTarget();
	}

	/**
	 * 
	 */
	@PlanBody
	protected IFuture<Void> moveToTarget()
	{
		final Future<Void> ret = new Future<Void>();

		Location target = goal.getLocation();
		Location myloc = capa.getMyLocation();
		
		if(!myloc.isNear(target))
		{
//			if(ExecutePlanStepAction.RPLANS.get()==null)
//			System.out.println("before: "+ExecutePlanStepAction.RPLANS.get());
			oneStepToTarget().addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					moveToTarget().addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> oneStepToTarget()
	{
		final Future<Void> ret = new Future<Void>();
		
		Location target = goal.getLocation();
		Location myloc = capa.getMyLocation();

		double speed = capa.getMySpeed();
		double d = myloc.getDistance(target);
		double r = speed*0.00004*100;//(newtime-time);
		double dx = target.getX()-myloc.getX();
		double dy = target.getY()-myloc.getY();

		double rx = r<d? r*dx/d: dx;
		double ry = r<d? r*dy/d: dy;
//		System.out.println("mypos: "+(myloc.getX()+rx)+" "+(myloc.getY()+ry)+" "+target);
		capa.setMyLocation(new Location(myloc.getX()+rx, myloc.getY()+ry));

		// Alter the charge state
		double	charge	= capa.getMyChargestate();
		charge	-= r*0.075;
		capa.setMyChargestate(Double.valueOf(charge));
		
		// wait for 0.01 seconds
		rplan.waitFor(100).addResultListener(new DelegationResultListener<Void>(ret)
		{
//			public void customResultAvailable(Void result)
//			{
//				updateVision().addResultListener(new DelegationResultListener<Void>(ret));
//			}
		});
		
		return ret;
	}
	
//	@PlanFailed
//	@PlanAborted
//	public void failedaborted()
//	{
//		System.out.println("failed+aborted: "+rplan.getId());
////		rplan.getException().printStackTrace();
//	}
	
	
	//-------- helper methods --------

//	/**
//	 *  Update the vision, when having moved.
//	 */
//	protected IFuture<Void>	updateVision()
//	{		
//		final Future<Void> ret = new Future<Void>();
//////		long start = System.currentTimeMillis();
////		
//		// Create a representation of myself.
//		final Cleaner cl = new Cleaner(capa.getMyLocation(),
//			capa.getAgent().getComponentIdentifier().getName(),
//			capa.getCarriedWaste(), capa.getMyVision(),
//			capa.getMyChargestate());
//
//		//IEnvironment env = (IEnvironment)getBeliefbase().getBelief("environment").getFact();
////		//Vision vi = env.getVision(cl);
////		IGoal dg = createGoal("get_vision_action");
////		dispatchSubgoalAndWait(dg);
//
//		rplan.dispatchSubgoal(capa.new GetVisionAction())
//			.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.GetVisionAction, Void>(ret)
//		{
//			public void customResultAvailable(GetVisionAction gva)
//			{
//				Vision vi = gva.getVision();
//			
//				if(vi!=null)
//				{
//					capa.setDaytime(vi.isDaytime());
//					
//					Waste[] ws = vi.getWastes();
//					Wastebin[] wbs = vi.getWastebins();
//					Chargingstation[] cs = vi.getStations();
//					Cleaner[] cls = vi.getCleaners();
//		
//					// When an object is not seen any longer (not
//					// in actualvision, but in (near) beliefs), remove it.
////					List known = (List)getExpression("query_in_vision_objects").execute();
//					List<LocationObject> known = getInVisionObjects();
//					
//					for(int i=0; i<known.size(); i++)
//					{
//						Object object = known.get(i);
//						if(object instanceof Waste)
//						{
//							List tmp = SUtil.arrayToList(ws);
//							if(!tmp.contains(object))
//								capa.getWastes().remove(object);
//						}
//						else if(object instanceof Wastebin)
//						{
//							List tmp = SUtil.arrayToList(wbs);
//							if(!tmp.contains(object))
//								capa.getWastebins().remove(object);
//						}
//						else if(object instanceof Chargingstation)
//						{
//							List tmp = SUtil.arrayToList(cs);
//							if(!tmp.contains(object))
//								capa.getChargingStations().remove(object);
//						}
//						else if(object instanceof Cleaner)
//						{
//							List tmp = SUtil.arrayToList(cls);
//							if(!tmp.contains(object))
//								capa.getCleaners().remove(object);
//						}
//					}
//		
//					// Add new or changed objects to beliefs.
//					for(int i=0; i<ws.length; i++)
//					{
//						if(!capa.getWastes().contains(ws[i]))
//							capa.getWastes().add(ws[i]);
//					}
//					for(int i=0; i<wbs.length; i++)
//					{
//						// Remove contained wastes from knowledge.
//						// Otherwise the agent might think that the waste is still
//						// somewhere (outside its vision) and then it creates lots of
//						// cleanup goals, that are instantly achieved because the
//						// target condition (waste in wastebin) holds.
//						Waste[]	wastes	= wbs[i].getWastes();
//						for(int j=0; j<wastes.length; j++)
//						{
//							if(capa.getWastes().contains(wastes[j]))
//								capa.getWastes().remove(wastes[j]);
//						}
//		
//						// Now its safe to add wastebin to beliefs.
//						if(capa.getWastebins().contains(wbs[i]))
//						{
//							capa.getWastebins().remove(wbs[i]);
//							capa.getWastebins().add(wbs[i]);
////							bs.updateFact(wbs[i]);
////							Wastebin wb = (Wastebin)bs.getFact(wbs[i]);
////							wb.update(wbs[i]);
//						}
//						else
//						{
//							capa.getWastebins().add(wbs[i]);
//						}
//						//getBeliefbase().getBeliefSet("wastebins").updateOrAddFact(wbs[i]);
//					}
//					for(int i=0; i<cs.length; i++)
//					{
//						if(capa.getChargingStations().contains(cs[i]))
//						{
////								bs.updateFact(cs[i]);
////							Chargingstation stat = (Chargingstation)bs.getFact(cs[i]);
////							stat.update(cs[i]);
//							capa.getChargingStations().remove(cs[i]);
//							capa.getChargingStations().add(cs[i]);
//						}
//						else
//						{
//							capa.getChargingStations().add(cs[i]);
//						}
//						//getBeliefbase().getBeliefSet("chargingstations").updateOrAddFact(cs[i]);
//					}
//					for(int i=0; i<cls.length; i++)
//					{
//						if(!cls[i].equals(cl))
//						{
//							if(capa.getCleaners().contains(cls[i]))
//							{
////									bs.updateFact(cls[i]);
////								Cleaner clea = (Cleaner)bs.getFact(cls[i]);
////								clea.update(cls[i]);
//								capa.getCleaners().remove(cls[i]);
//								capa.getCleaners().add(cls[i]);
//							}
//							else
//							{
//								capa.getCleaners().add(cls[i]);
//							}
//							//getBeliefbase().getBeliefSet("cleaners").updateOrAddFact(cls[i]);
//						}
//					}
//		
//					//getBeliefbase().getBelief("???").setFact("allowed_to_move", new Boolean(true));
//				}
//				else
//				{
////					System.out.println("Error when updating vision! "+event.getGoal());
//				}
//				ret.setResult(null);
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 * 
//	 */
//	protected List<LocationObject> getInVisionObjects()
//	{
//		List<LocationObject> ret = new ArrayList<LocationObject>();
//		List<LocationObject> from = new ArrayList<LocationObject>();
//		from.addAll(capa.getWastes());
//		from.addAll(capa.getWastebins());
//		from.addAll(capa.getChargingStations());
//		from.addAll(capa.getCleaners());
//		for(LocationObject o: from)
//		{
//			if(capa.getMyLocation().isNear(o.getLocation(), capa.getMyVision()))
//			{
//				ret.add(o);
//			}
//		}
//		return ret;
//	}
	
//	<expression name="query_in_vision_objects">
//	select LocationObject $object
//	from SUtil.joinArbitraryArrays(new Object[]
//			{
//				$beliefbase.getBeliefSet("wastes").getFacts(),
//				$beliefbase.getBeliefSet("wastebins").getFacts(),
//				$beliefbase.getBeliefSet("chargingstations").getFacts(),
//				$beliefbase.getBeliefSet("cleaners").getFacts()
//		})
//	where $beliefbase.getBelief("my_location").getFact().isNear($object.getLocation(), $beliefbase.getBelief("my_vision").getFact())
//</expression>
}
