package jadex.bdiv3.example.cleanerworld.cleaner;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.example.cleanerworld.world.Chargingstation;
import jadex.bdiv3.example.cleanerworld.world.Cleaner;
import jadex.bdiv3.example.cleanerworld.world.Environment;
import jadex.bdiv3.example.cleanerworld.world.IEnvironment;
import jadex.bdiv3.example.cleanerworld.world.Location;
import jadex.bdiv3.example.cleanerworld.world.LocationObject;
import jadex.bdiv3.example.cleanerworld.world.MapPoint;
import jadex.bdiv3.example.cleanerworld.world.Vision;
import jadex.bdiv3.example.cleanerworld.world.Waste;
import jadex.bdiv3.example.cleanerworld.world.Wastebin;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

@Agent
@Plans(
{
	@Plan(trigger=@Trigger(goals=CleanerBDI.PerformLookForWaste.class), body=LeastSeenWalkPlan.class),
	@Plan(trigger=@Trigger(goals={CleanerBDI.QueryWastebin.class, CleanerBDI.QueryChargingStation.class}), body=ExploreMapPlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.PerformPatrol.class), body=PatrolPlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.AchieveCleanup.class), body=CleanUpWastePlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.AchievePickupWaste.class), body=PickUpWastePlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.AchieveDropWaste.class), body=DropWastePlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.MaintainBatteryLoaded.class), body=LoadBatteryPlan.class),
	// <CONTEXTCONDITION>$BELIEFBASE.MY_CHARGESTATE &GT; 0</CONTEXTCONDITION>
	@Plan(trigger=@Trigger(goals=CleanerBDI.AchieveMoveTo.class), body=MoveToLocationPlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.PerformMemorizePositions.class), body=MemorizePositionsPlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.PickupWasteAction.class), priority=1, body=LocalPickUpWasteActionPlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.DropWasteAction.class), priority=1, body=LocalDropWasteActionPlan.class),
	@Plan(trigger=@Trigger(goals=CleanerBDI.GetVisionAction.class), priority=1, body=LocalGetVisionActionPlan.class)
})
public class CleanerBDI
{
	/** The bdi agent. */
	@Agent
	protected BDIAgent agent;
	
	@Belief
	protected IEnvironment environment;// = Environment.getInstance(); // problem: how to observe initial values, bdiagent is not available
	
	@Belief
	protected Set<Waste> wastes;
	
	@Belief
	protected Set<Wastebin> wastebins;
	
	@Belief
	protected Set<Chargingstation> chargingstations;
	
	@Belief
	protected Set<Cleaner> cleaners;
	
	@Belief
	protected Tuple2<Integer, Integer> raster = new Tuple2<Integer, Integer>(new Integer(10), new Integer(10));

	@Belief
	protected Set<MapPoint> visited_positions;

	@Belief
	protected boolean daytime;

	@Belief
	protected Location my_location = new Location(0.2, 0.2);
	
	@Belief 
	protected double my_speed = 3;
	
	@Belief
	protected double my_vision = 0.1;
	
	@Belief
	protected double my_chargestate = 1.0;
	
	@Belief
	protected Waste carriedwaste;
	
	@Belief
	protected List<Location> patrolpoints;
	
	@Belief
	protected CleanerGui gui;
	
	@Goal
	public class MaintainBatteryLoaded
	{
//		<deliberation>
//			<inhibits ref="performlookforwaste" inhibit="when_in_process"/>
//			<inhibits ref="achievecleanup" inhibit="when_in_process"/>
//			<inhibits ref="performpatrol" inhibit="when_in_process"/>
//		</deliberation>
		
		@GoalMaintainCondition
		public boolean checkMaintain()
		{
			return my_chargestate>0.2;
		}
		
		@GoalTargetCondition
		public boolean checkTarget()
		{
			return my_chargestate>=1.0;
		}
	}
	
	@Goal
	public class AchieveCleanup
	{
		// <unique/>
		// <deliberation cardinality="1">
		// <inhibits ref="performlookforwaste"/>
		// <inhibits ref="achievecleanup" language="jcl">
		// $beliefbase.my_location.getDistance($goal.waste.getLocation())
		// &lt; $beliefbase.my_location.getDistance($ref.waste.getLocation())
		
		protected Waste waste;
		
		// todo: creation
		
//		@GoalCreationCondition(events="wastes")
//		public AchieveCleanup(Waste waste)
//		{
//			this.waste = waste;
//		}
		
//		@GoalCreationCondition
//		public static boolean checkCreate(@Event("wastes") Waste waste)
//		{
//			return true; // ??
//		}
		
		@GoalContextCondition(events="daytime")
		public boolean checkContext()
		{
			return daytime;
		}
		
		@GoalDropCondition(events={"carriedwaste", "wastes"})
		public boolean checkDrop()
		{
			return carriedwaste==null && wastes.contains(waste);
		}
		
		@GoalDropCondition
		public boolean checkTarget()
		{
			boolean ret = false;
			
			for(Wastebin wb: wastebins)
			{
				if(wb.contains(waste))
				{
					ret = true;
					break;
				}
			}
			return ret;
		}

		/**
		 *  Get the waste.
		 *  @return The waste.
		 */
		public Waste getWaste()
		{
			return waste;
		}
	}
	
	@Goal(excludemode=MGoal.EXCLUDE_NEVER, succeedonpassed=false)
	public class PerformLookForWaste
	{
		@GoalContextCondition(events="daytime")
		public boolean checkContext()
		{
			return daytime;
		}
	}
	
	@Goal(excludemode=MGoal.EXCLUDE_NEVER, succeedonpassed=false)
	public class PerformPatrol
	{
		@GoalContextCondition(events="daytime")
		public boolean checkContext()
		{
			return !daytime;
		}
	}
	
//	@Goal(retry=MPro)
	@Goal
	public class AchievePickupWaste
	{
		protected Waste waste;
		
		public AchievePickupWaste(Waste waste)
		{
			this.waste = waste;
		}
	}
	
	@Goal(excludemode=MGoal.EXCLUDE_NEVER)
	public class AchieveDropWaste
	{
		protected Wastebin wastebin;
		
		public AchieveDropWaste(Wastebin wastebin)
		{
			this.wastebin = wastebin;
		}
		
		@GoalDropCondition(events="wastebin") // todo: check when parameter value changes
		public boolean checkContext()
		{
			return wastebin.isFull();
		}
	}
	
	@Goal
	public class AchieveMoveTo
	{
		protected Location location;
		
		public AchieveMoveTo(Location location)
		{
			this.location = location;
		}
		
		@GoalTargetCondition(events="my_location")
		public boolean checkContext()
		{
			return my_location.isNear(location);
		}

		/**
		 *  Get the location.
		 *  @return The location.
		 */
		public Location getLocation()
		{
			return location;
		}
	}
	
	@Goal
	public class QueryWastebin
	{
		protected Wastebin wastebin;
		
		@GoalTargetCondition(events="my_location")
		public boolean checkTarget()
		{
			// todo: assign wastin to neareast nonfull wastebin and if not null return true??
			return getResult()!=null;
		}
		
//		@GoalResult
		public Wastebin getResult()
		{
			if(wastebin==null)
			{
			}
			return wastebin;
		}
	}
	
	@Goal
	public class QueryChargingStation
	{
		protected Chargingstation station;
		
		@GoalTargetCondition(events="my_location")
		public boolean checkTarget()
		{
			// todo: assign nearest charging station to station
			return false;
		}
	}

	@Goal
	public class GetVisionAction
	{
		protected Vision vision;
		
		public Vision getVision()
		{
			return vision;
		}

		public void setVision(Vision vision)
		{
			this.vision = vision;
		}
	}
	
	@Goal
	public class PickupWasteAction
	{
		protected Waste waste;
	}
	
	@Goal
	public class DropWasteAction
	{
		protected Waste waste;
		protected Wastebin wastebin;
	}
	
	@Goal(succeedonpassed=false)
	public class PerformMemorizePositions
	{
	}
	
//	@Plan(trigger=@Trigger(goals=PerformLookForWaste.class))
//	protected IFuture<Void> leastSeenWalkPlan(PerformLookForWaste goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals={QueryWastebin.class, QueryChargingStation.class}))
//	protected IFuture<Void> exploreMapPlan(Object goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals={PerformPatrol.class}))
//	protected IFuture<Void> patrolPlan(PerformPatrol goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=AchieveCleanup.class))
//	protected IFuture<Void> cleanUpWastePlan(AchieveCleanup goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=AchievePickupWaste.class))
//	protected IFuture<Void> pickUpWastePlan(AchievePickupWaste goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=AchieveDropWaste.class))
//	protected IFuture<Void> dropWastePlan(AchieveDropWaste goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=MaintainBatteryLoaded.class))
//	protected IFuture<Void> loadBatteryPlan(MaintainBatteryLoaded goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	// <CONTEXTCONDITION>$BELIEFBASE.MY_CHARGESTATE &GT; 0</CONTEXTCONDITION>
//	@Plan(trigger=@Trigger(goals=AchieveMoveTo.class))
//	protected IFuture<Void> moveToLocationPlan(AchieveMoveTo goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=PerformMemorizePositions.class))
//	protected IFuture<Void> memorizePositionsPlan(PerformMemorizePositions goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=PickupWasteAction.class), priority=1)
//	protected IFuture<Void> localPickUpWasteActionPlan(PickupWasteAction goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=DropWasteAction.class), priority=1)
//	protected IFuture<Void> localDropWasteActionPlan(DropWasteAction goal)
//	{
//		return IFuture.DONE;
//	}
//	
//	@Plan(trigger=@Trigger(goals=GetVisionAction.class), priority=1)
//	protected IFuture<Void> localGetVisionActionPlan(GetVisionAction goal)
//	{
//		return IFuture.DONE;
//	}
	
	@Plan(trigger=@Trigger(factchangeds={"environment", "my_location"}))
	protected IFuture<Void> updateVision(RPlan rplan)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Create a representation of myself.
		final Cleaner cl = new Cleaner(getMyLocation(),
			getAgent().getComponentIdentifier().getName(),
			getCarriedWaste(), getMyVision(),
			getMyChargestate());

		rplan.dispatchSubgoal(new GetVisionAction())
			.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.GetVisionAction, Void>(ret)
		{
			public void customResultAvailable(GetVisionAction gva)
			{
				Vision vi = gva.getVision();
			
				if(vi!=null)
				{
					setDaytime(vi.isDaytime());
					
					Waste[] ws = vi.getWastes();
					Wastebin[] wbs = vi.getWastebins();
					Chargingstation[] cs = vi.getStations();
					Cleaner[] cls = vi.getCleaners();
		
					// When an object is not seen any longer (not
					// in actualvision, but in (near) beliefs), remove it.
	//				List known = (List)getExpression("query_in_vision_objects").execute();
					List<LocationObject> known = getInVisionObjects();
					
					for(int i=0; i<known.size(); i++)
					{
						Object object = known.get(i);
						if(object instanceof Waste)
						{
							List tmp = SUtil.arrayToList(ws);
							if(!tmp.contains(object))
								getWastes().remove(object);
						}
						else if(object instanceof Wastebin)
						{
							List tmp = SUtil.arrayToList(wbs);
							if(!tmp.contains(object))
								getWastebins().remove(object);
						}
						else if(object instanceof Chargingstation)
						{
							List tmp = SUtil.arrayToList(cs);
							if(!tmp.contains(object))
								getChargingStations().remove(object);
						}
						else if(object instanceof Cleaner)
						{
							List tmp = SUtil.arrayToList(cls);
							if(!tmp.contains(object))
								getCleaners().remove(object);
						}
					}
		
					// Add new or changed objects to beliefs.
					for(int i=0; i<ws.length; i++)
					{
						if(!getWastes().contains(ws[i]))
							getWastes().add(ws[i]);
					}
					for(int i=0; i<wbs.length; i++)
					{
						// Remove contained wastes from knowledge.
						// Otherwise the agent might think that the waste is still
						// somewhere (outside its vision) and then it creates lots of
						// cleanup goals, that are instantly achieved because the
						// target condition (waste in wastebin) holds.
						Waste[]	wastes	= wbs[i].getWastes();
						for(int j=0; j<wastes.length; j++)
						{
							if(getWastes().contains(wastes[j]))
								getWastes().remove(wastes[j]);
						}
		
						// Now its safe to add wastebin to beliefs.
						if(getWastebins().contains(wbs[i]))
						{
							getWastebins().remove(wbs[i]);
							getWastebins().add(wbs[i]);
	//						bs.updateFact(wbs[i]);
	//						Wastebin wb = (Wastebin)bs.getFact(wbs[i]);
	//						wb.update(wbs[i]);
						}
						else
						{
							getWastebins().add(wbs[i]);
						}
						//getBeliefbase().getBeliefSet("wastebins").updateOrAddFact(wbs[i]);
					}
					for(int i=0; i<cs.length; i++)
					{
						if(getChargingStations().contains(cs[i]))
						{
	//							bs.updateFact(cs[i]);
	//						Chargingstation stat = (Chargingstation)bs.getFact(cs[i]);
	//						stat.update(cs[i]);
							getChargingStations().remove(cs[i]);
							getChargingStations().add(cs[i]);
						}
						else
						{
							getChargingStations().add(cs[i]);
						}
						//getBeliefbase().getBeliefSet("chargingstations").updateOrAddFact(cs[i]);
					}
					for(int i=0; i<cls.length; i++)
					{
						if(!cls[i].equals(cl))
						{
							if(getCleaners().contains(cls[i]))
							{
	//								bs.updateFact(cls[i]);
	//							Cleaner clea = (Cleaner)bs.getFact(cls[i]);
	//							clea.update(cls[i]);
								getCleaners().remove(cls[i]);
								getCleaners().add(cls[i]);
							}
							else
							{
								getCleaners().add(cls[i]);
							}
							//getBeliefbase().getBeliefSet("cleaners").updateOrAddFact(cls[i]);
						}
					}
		
					//getBeliefbase().getBelief("???").setFact("allowed_to_move", new Boolean(true));
				}
				else
				{
	//				System.out.println("Error when updating vision! "+event.getGoal());
				}
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected List<LocationObject> getInVisionObjects()
	{
		List<LocationObject> ret = new ArrayList<LocationObject>();
		List<LocationObject> from = new ArrayList<LocationObject>();
		from.addAll(getWastes());
		from.addAll(getWastebins());
		from.addAll(getChargingStations());
		from.addAll(getCleaners());
		for(LocationObject o: from)
		{
			if(getMyLocation().isNear(o.getLocation(), getMyVision()))
			{
				ret.add(o);
			}
		}
		return ret;
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		environment = Environment.getInstance();
		 
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui = new CleanerGui(agent.getExternalAccess());
			}
		});
		
		patrolpoints.add(new Location(0.1, 0.1));
		patrolpoints.add(new Location(0.1, 0.9));
		patrolpoints.add(new Location(0.3, 0.9));
		patrolpoints.add(new Location(0.3, 0.1));
		patrolpoints.add(new Location(0.5, 0.1));
		patrolpoints.add(new Location(0.5, 0.9));
		patrolpoints.add(new Location(0.7, 0.9));
		patrolpoints.add(new Location(0.7, 0.1));
		patrolpoints.add(new Location(0.9, 0.1));
		patrolpoints.add(new Location(0.9, 0.9));
		
//		agent.dispatchTopLevelGoal(new PerformLookForWaste());
		agent.dispatchTopLevelGoal(new PerformPatrol());
//		agent.dispatchTopLevelGoal(new MaintainBatteryLoaded());
//		agent.dispatchTopLevelGoal(new PerformMemorizePositions());
	}

	/**
	 *  Get the environment.
	 *  @return The environment.
	 */
	public IEnvironment getEnvironment()
	{
		return environment;
	}

	/**
	 *  Get the wastes.
	 *  @return The wastes.
	 */
	public Set<Waste> getWastes()
	{
		return wastes;
	}

	/**
	 *  Get the wastebins.
	 *  @return The wastebins.
	 */
	public Set<Wastebin> getWastebins()
	{
		return wastebins;
	}

	/**
	 *  Get the chargingstations.
	 *  @return The chargingstations.
	 */
	public Set<Chargingstation> getChargingStations()
	{
		return chargingstations;
	}

	/**
	 *  Get the cleaners.
	 *  @return The cleaners.
	 */
	public Set<Cleaner> getCleaners()
	{
		return cleaners;
	}

	/**
	 *  Get the raster.
	 *  @return The raster.
	 */
	public Tuple2<Integer, Integer> getRaster()
	{
		return raster;
	}

	/**
	 *  Get the visited_positions.
	 *  @return The visited_positions.
	 */
	public Set<MapPoint> getVisitedPositions()
	{
		return visited_positions;
	}

	/**
	 *  Get the daytime.
	 *  @return The daytime.
	 */
	public boolean isDaytime()
	{
		return daytime;
	}
	
	/**
	 *  Set the daytime.
	 *  @param daytime The daytime to set.
	 */
	public void setDaytime(boolean daytime)
	{
		this.daytime = daytime;
	}

	/**
	 *  Get the my_location.
	 *  @return The my_location.
	 */
	public Location getMyLocation()
	{
		return my_location;
	}
	
	/**
	 *  Set the my_location.
	 *  @param my_location The my_location to set.
	 */
	public void setMyLocation(Location mylocation)
	{
//		System.out.println("mypos: "+mylocation);
		this.my_location = mylocation;
	}

	/**
	 *  Get the my_speed.
	 *  @return The my_speed.
	 */
	public double getMySpeed()
	{
		return my_speed;
	}

	/**
	 *  Get the my_vision.
	 *  @return The my_vision.
	 */
	public double getMyVision()
	{
		return my_vision;
	}

	/**
	 *  Get the my_chargestate.
	 *  @return The my_chargestate.
	 */
	public double getMyChargestate()
	{
		return my_chargestate;
	}
	
	/**
	 *  Set the my_chargestate.
	 *  @param my_chargestate The my_chargestate to set.
	 */
	public void setMyChargestate(double mychargestate)
	{
		this.my_chargestate = mychargestate;
	}

	/**
	 *  Get the carriedwaste.
	 *  @return The carriedwaste.
	 */
	public Waste getCarriedWaste()
	{
		return carriedwaste;
	}

	/**
	 *  Get the patrolpoints.
	 *  @return The patrolpoints.
	 */
	public List<Location> getPatrolPoints()
	{
		return patrolpoints;
	}

	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public BDIAgent getAgent()
	{
		return agent;
	}
}

//<expressions>
//	<!-- Query all objects from the beliefs that are currently in sight.-->
//	<expression name="query_in_vision_objects">
//		select LocationObject $object
//		from SUtil.joinArbitraryArrays(new Object[]
//			{
//				$beliefbase.getBeliefSet("wastes").getFacts(),
//				$beliefbase.getBeliefSet("wastebins").getFacts(),
//				$beliefbase.getBeliefSet("chargingstations").getFacts(),
//				$beliefbase.getBeliefSet("cleaners").getFacts()
//			})
//		where $beliefbase.getBelief("my_location").getFact().isNear($object.getLocation(), $beliefbase.getBelief("my_vision").getFact())
//	</expression>
//
//	<!-- Query the max quantity map point. -->
//	<expression name="query_max_quantity">
//		select one MapPoint $mp
//		from $beliefbase.getBeliefSet("visited_positions").getFacts()
//		order by $mp.getQuantity() desc
//	</expression>
//
//	<!-- Query the map points ordered by their quantity
//		(least ones first). -->
//	<expression name="query_min_quantity">
//		select MapPoint $mp
//		from $beliefbase.getBeliefSet("visited_positions").getFacts()
//		order by $mp.getQuantity()
//	</expression>
//
//	<!-- Query the map points ordered by their seen value
//		(least ones first). -->
//	<expression name="query_min_seen">
//		select MapPoint $mp
//		from $beliefbase.getBeliefSet("visited_positions").getFacts()
//		order by $mp.getSeen()
//	</expression>
//</expressions>
