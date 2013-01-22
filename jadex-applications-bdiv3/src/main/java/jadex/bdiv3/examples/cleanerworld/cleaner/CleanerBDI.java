package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalInhibit;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.cleanerworld.world.Chargingstation;
import jadex.bdiv3.examples.cleanerworld.world.Cleaner;
import jadex.bdiv3.examples.cleanerworld.world.Environment;
import jadex.bdiv3.examples.cleanerworld.world.IEnvironment;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.examples.cleanerworld.world.LocationObject;
import jadex.bdiv3.examples.cleanerworld.world.MapPoint;
import jadex.bdiv3.examples.cleanerworld.world.Vision;
import jadex.bdiv3.examples.cleanerworld.world.Waste;
import jadex.bdiv3.examples.cleanerworld.world.Wastebin;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.RPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
	protected IEnvironment environment = Environment.getInstance();
	
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
	protected Set<MapPoint> visited_positions =	new HashSet<MapPoint>(Arrays.asList(
		MapPoint.getMapPointRaster(raster.getFirstEntity().intValue(), 
		raster.getSecondEntity().intValue(), 1, 1)));

	@Belief
	protected boolean daytime;

	@Belief
	protected Location my_location = new Location(0.2, 0.2);
	
	@Belief 
	protected double my_speed = 3;
	
	@Belief
	protected double my_vision = 0.1;
	
	@Belief
	protected double my_chargestate = 0.21;
	
	@Belief
	protected Waste carriedwaste;
	
	@Belief
	protected List<Location> patrolpoints;
	
//	@Belief
//	protected CleanerGui gui = new CleanerGui(agent.getExternalAccess());
//	protected GuiCreator gc = new GuiCreator(CleanerGui.class, new Class[]{IExternalAccess.class}, new Object[]{agent.getExternalAccess()});
	
	@Goal(deliberation=@Deliberation(inhibits={PerformLookForWaste.class, AchieveCleanup.class, PerformPatrol.class}))
	public class MaintainBatteryLoaded
	{
		@GoalMaintainCondition(events="my_chargestate")
		public boolean checkMaintain()
		{
			return my_chargestate>0.2;
		}
		
		@GoalTargetCondition(events="my_chargestate")
		public boolean checkTarget()
		{
			return my_chargestate>=1.0;
		}
	}
	
	// currently creates too many goals and abandons them with during adopt with unique
//	@Goal(unique=true, deliberation=@Deliberation(cardinality=1, inhibits={PerformLookForWaste.class, AchieveCleanup.class}))
	@Goal(unique=true, deliberation=@Deliberation(inhibits={PerformLookForWaste.class, AchieveCleanup.class}))
	public class AchieveCleanup
	{
		protected Waste waste;
		
		@GoalCreationCondition(events="wastes")
		public AchieveCleanup(Waste waste)
		{
			System.out.println("new achieve cleanup: "+waste);
			this.waste = waste;
		}
		
		// todo: conditional creation?
//		@GoalCreationCondition
//		public static boolean checkCreate(@Event("wastes") Waste waste)
//		{
//			return true; // ??
//		}
		
		@GoalContextCondition(events="daytime")
		public boolean checkContext()
		{
			return isDaytime();
		}
		
		@GoalDropCondition(events={"carriedwaste", "wastes"})
		public boolean checkDrop()
		{
			System.out.println("drop triggerd: "+getWaste());
			return getCarriedWaste()==null && !getWastes().contains(waste);
		}
		
		public boolean checkTarget()
		{
			boolean ret = false;
			
			for(Wastebin wb: getWastebins())
			{
				if(wb.contains(waste))
				{
					ret = true;
					break;
				}
			}
			
			return ret;
		}
		
		@GoalInhibit(AchieveCleanup.class)
		protected boolean inhibitAchieveCleanUp(AchieveCleanup other)
		{
			boolean ret = getWaste().equals(getCarriedWaste());
			
			if(!ret)
			{
				double d1 = getMyLocation().getDistance(waste.getLocation());
				double d2 = getMyLocation().getDistance(other.getWaste().getLocation());
				ret = d1<d2;
				if(!ret && d1==d2)
				{
					ret = hashCode()<other.hashCode();
				}
			}
				
//			System.out.println("inh methodb: "+this+" "+other+" "+ret);
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

		// hashcode and equals implementation for unique flag
		
		/**
		 * 
		 */
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((waste == null) ? 0 : waste.hashCode());
			return result;
		}

		/**
		 * 
		 */
		public boolean equals(Object obj)
		{
			boolean ret = false;
			if(obj instanceof AchieveCleanup)
			{
				AchieveCleanup other = (AchieveCleanup)obj;
				ret = getOuterType().equals(other.getOuterType()) && SUtil.equals(waste, other.getWaste());
			}
			return ret;
		}

		private CleanerBDI getOuterType()
		{
			return CleanerBDI.this;
		}
		
		public String toString()
		{
			return "AchieveCleanup: "+getWaste();
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
	
	@Goal(retry=false)
	public class AchievePickupWaste
	{
		protected Waste waste;
		
		public AchievePickupWaste(Waste waste)
		{
			this.waste = waste;
		}
		
		public Waste getWaste()
		{
			return waste;
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

		public Wastebin getWastebin()
		{
			return wastebin;
		}
	}
	
	@Goal
	public class AchieveMoveTo
	{
		protected Location location;
		
		public AchieveMoveTo(Location location)
		{
//			System.out.println("created: "+location);
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
	
	@Goal(excludemode=MGoal.EXCLUDE_NEVER)
	public class QueryWastebin
	{
		protected Wastebin wastebin;
		
		@GoalTargetCondition(events="wastebins")
		public boolean checkTarget()
		{
			wastebin = getNearestNonFullWastebin();
			return wastebin!=null;
		}
		
		/**
		 * 
		 */
		protected Wastebin getNearestNonFullWastebin()
		{
			Wastebin ret = null;
			for(Wastebin wb: wastebins)
			{
				if(!wb.isFull())
				{
					if(ret==null)
					{
						ret = wb;
					}
					else if(getMyLocation().getDistance(wb.getLocation())
						<getMyLocation().getDistance(ret.getLocation()))
					{
						ret = wb;
					}
				}
			}
			return ret;
		}

		/**
		 *  Get the wastebin.
		 *  @return The wastebin.
		 */
//		@GoalResult
		public Wastebin getWastebin()
		{
			return wastebin;
		}
	}
	
	@Goal(excludemode=MGoal.EXCLUDE_NEVER)
	public class QueryChargingStation
	{
		protected Chargingstation station;
		
		@GoalTargetCondition(events="chargingstations")
		public boolean checkTarget()
		{
			station = getNearestChargingStation();
			return station!=null;
		}
		
		/**
		 * 
		 */
		protected Chargingstation getNearestChargingStation()
		{
			Chargingstation ret = null;
			for(Chargingstation cg: chargingstations)
			{
				if(ret==null)
				{
					ret = cg;
				}
				else if(getMyLocation().getDistance(cg.getLocation())
					<getMyLocation().getDistance(ret.getLocation()))
				{
					ret = cg;
				}
			}
			return ret;
		}
		
		
		/**
		 *  Get the station.
		 *  @return The station.
		 */
		public Chargingstation getStation()
		{
			return station;
		}

		/**
		 *  Set the station.
		 *  @param station The station to set.
		 */
		public void setStation(Chargingstation station)
		{
			this.station = station;
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

		public PickupWasteAction(Waste waste)
		{
			this.waste = waste;
		}

		public Waste getWaste()
		{
			return waste;
		}
	}
	
	@Goal
	public class DropWasteAction
	{
		protected Waste waste;
		
		protected Wastebin wastebin;
		
		public DropWasteAction(Waste waste, Wastebin wastebin)
		{
			this.waste = waste;
			this.wastebin = wastebin;
		}

		/**
		 *  Get the waste.
		 *  @return The waste.
		 */
		public Waste getWaste()
		{
			return waste;
		}

		/**
		 *  Get the wastebin.
		 *  @return The wastebin.
		 */
		public Wastebin getWastebin()
		{
			return wastebin;
		}
	}
	
	@Goal(excludemode=MGoal.EXCLUDE_NEVER, succeedonpassed=false, retrydelay=300)
	public class PerformMemorizePositions
	{
	}
	
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
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new CleanerGui(agent.getExternalAccess());
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
		
		agent.dispatchTopLevelGoal(new PerformLookForWaste());
		agent.dispatchTopLevelGoal(new PerformPatrol());
		agent.dispatchTopLevelGoal(new MaintainBatteryLoaded());
		agent.dispatchTopLevelGoal(new PerformMemorizePositions());
		
		agent.waitFor(100, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				setMyChargestate(0.1);
				return IFuture.DONE;
			}
		});
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
	 *  Set the carriedwaste.
	 *  @param carriedwaste The carriedwaste to set.
	 */
	public void setCarriedwaste(Waste carriedwaste)
	{
		this.carriedwaste = carriedwaste;
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
	
	/**
	 * 
	 */
	protected List<MapPoint> getMaxQuantity()
	{
		Set<MapPoint> locs = getVisitedPositions();
		List<MapPoint> ret = new ArrayList<MapPoint>(locs);
		Collections.sort(ret, new Comparator<MapPoint>()
		{
			public int compare(MapPoint o1, MapPoint o2)
			{
				return o2.getQuantity()-o1.getQuantity();
			}
		});
		return ret;
	}
}
