package jadex.bdiv3.examples.cleanerworld.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
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
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

@Agent(type=BDIAgentFactory.TYPE)
@Plans(
{
	@Plan(trigger=@Trigger(goals=CleanerAgent.PerformLookForWaste.class), body=@Body(LeastSeenWalkPlan.class)),
	@Plan(trigger=@Trigger(goals={CleanerAgent.QueryWastebin.class, CleanerAgent.QueryChargingStation.class}), body=@Body(ExploreMapPlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.PerformPatrol.class), body=@Body(PatrolPlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.AchieveCleanup.class), body=@Body(CleanUpWastePlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.AchievePickupWaste.class), body=@Body(PickUpWastePlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.AchieveDropWaste.class), body=@Body(DropWastePlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.MaintainBatteryLoaded.class), body=@Body(LoadBatteryPlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.AchieveMoveTo.class), body=@Body(MoveToLocationPlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.PerformMemorizePositions.class), body=@Body(MemorizePositionsPlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.PickupWasteAction.class), priority=1, body=@Body(LocalPickUpWasteActionPlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.DropWasteAction.class), priority=1, body=@Body(LocalDropWasteActionPlan.class)),
	@Plan(trigger=@Trigger(goals=CleanerAgent.GetVisionAction.class), priority=1, body=@Body(LocalGetVisionActionPlan.class))
})
@Description("<h1>Cleaner Robot Agent</h1>")
public class CleanerAgent
{
	/** The bdi agent. Automatically injected */
	@Agent
	protected IInternalAccess agent;
	
	/** The virtual environment of the cleaner. */
	@Belief
	protected IEnvironment environment = Environment.getInstance();
	
	/** The set of wastes. */
	@Belief
	protected Set<Waste> wastes = new HashSet<Waste>();
	
	/** The known set of wastebins. */
	@Belief
	protected Set<Wastebin> wastebins = new HashSet<Wastebin>();
	
	/** The known set of chargingstation. */
	@Belief
	protected Set<Chargingstation> chargingstations = new HashSet<Chargingstation>();
	
	/** The known set of other cleaners. */
	@Belief
	protected Set<Cleaner> cleaners = new HashSet<Cleaner>();
	
	/** The raster for memorizing positions. */
	@Belief
	protected Tuple2<Integer, Integer> raster = new Tuple2<Integer, Integer>(Integer.valueOf(10), Integer.valueOf(10));

	/** The visited positions. */
	@Belief
	protected Set<MapPoint> visited_positions =	new HashSet<MapPoint>(Arrays.asList(
		MapPoint.getMapPointRaster(raster.getFirstEntity().intValue(), 
		raster.getSecondEntity().intValue(), 1, 1)));

	/** The flag if it is daytime or night. */
	@Belief
	protected boolean daytime;

	/** The location of the cleaner. */
	@Belief
	protected Location my_location = new Location(0.2, 0.2);
	
	/** The speed of the cleaner. */
	@Belief 
	protected double my_speed = 3;
	
	/** The vision. */
	@Belief
	protected double my_vision = 0.1;
	
	/** The chargestate. */
	@Belief
//	protected double my_chargestate = 0.21;
	protected double my_chargestate = 1.0;
	
	/** The carried waste (or null). */
	@Belief
	protected Waste carriedwaste;
	
	/** The patrol points. */
	@Belief
	protected List<Location> patrolpoints = new ArrayList<Location>();
	
//	@Belief
//	protected CleanerGui gui = new CleanerGui(agent.getExternalAccess());
//	protected GuiCreator gc = new GuiCreator(CleanerGui.class, new Class[]{IExternalAccess.class}, new Object[]{agent.getExternalAccess()});
	
	/**
	 *  Goal for keeping the battery loaded.
	 */
	@Goal(deliberation=@Deliberation(inhibits={PerformLookForWaste.class, AchieveCleanup.class, PerformPatrol.class}))
	public class MaintainBatteryLoaded
	{
		/**
		 *  When the chargestate is below 0.2
		 *  the cleaner will activate this goal.
		 */
		@GoalMaintainCondition//(beliefs="my_chargestate")
		public boolean checkMaintain()
		{
			return my_chargestate>0.2;
		}
		
		/**
		 *  The target condition determines when
		 *  the goal goes back to idle. 
		 */
		@GoalTargetCondition//(beliefs="my_chargestate")
		public boolean checkTarget()
		{
			return my_chargestate>=1.0;
		}
	}
	
	/**
	 *  Achieve cleanup goals are created for every piece
	 *  of waste the agent notices.
	 *  
	 *  Avoids having multiple goals for the same piece of
	 *  waste by setting goal to unique. For this purpose
	 *  the hashcode and equals method need to be implemented. 
	 */
	// currently creates too many goals and abandons them with during adopt with unique
//	@Goal(unique=true, deliberation=@Deliberation(cardinality=1, inhibits={PerformLookForWaste.class, AchieveCleanup.class}))
	@Goal(excludemode=ExcludeMode.Never, unique=true, deliberation=@Deliberation(inhibits={PerformLookForWaste.class, AchieveCleanup.class}))
	public class AchieveCleanup
	{
		/** The waste. */
		protected Waste waste;
		
		/**
		 *  Create a new goal.
		 */
//		@GoalCreationCondition(beliefs="wastes")
		// Must not create achieve goals for null wastes (e.g. when the whole set is initialized to a hashset)
//		@GoalCreationCondition(rawevents={@RawEvent(value=ChangeEvent.FACTADDED, second="wastes")})
//		public AchieveCleanup(Waste waste)
		@GoalCreationCondition(beliefs="wastes")
		public AchieveCleanup(@CheckNotNull Waste waste)
		{
//			System.out.println("chargestate: "+my_chargestate);
//			if(waste==null)
//				System.out.println("new achieve cleanup: "+waste);
			this.waste = waste;
		}
		
		// todo: conditional creation?
//		@GoalCreationCondition
//		public static boolean checkCreate(@Event("wastes") Waste waste)
//		{
//			return true; // ??
//		}

		/**
		 *  Suspend the goal when night.
		 */
		@GoalContextCondition//(beliefs="daytime")
		public boolean checkContext()
		{
			return isDaytime();
		}
		
		/**
		 *  Drop the goal when waste is not seen
		 *  anymore and the agent agent does not carry it.
		 */
		@GoalDropCondition(beliefs={"carriedwaste", "wastes"})
		public boolean checkDrop()
		{
//			System.out.println("drop triggerd: "+getWaste());
			return getCarriedWaste()==null && !getWastes().contains(waste);
		}
		
		/**
		 *  Check if the waste is contained
		 *  in a wastebin.
		 */
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
		
		/**
		 *  Inhibit other achieve cleanup goals that 
		 *  are farer away from the cleaner.
		 */
		@GoalInhibit(AchieveCleanup.class)
		protected boolean inhibitAchieveCleanUp(AchieveCleanup other)
		{
			// this goal inhibits other if its waste is currently transported
			boolean ret = getWaste().equals(getCarriedWaste());

			if(!ret)
			{
				// if other goal is cur active
				if(other.getWaste().equals(getCarriedWaste()))
				{
					// must not interfere with that
					ret = false;
				}
				else
				{
					double d1 = getMyLocation().getDistance(waste.getLocation());
					double d2 = getMyLocation().getDistance(other.getWaste().getLocation());
					ret = d1<d2;
					if(!ret && d1==d2)
					{
						ret = hashCode()<other.hashCode();
					}
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
		 *  Get the hashcode.
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
		 *  Test if equal to other goal.
		 *  @param obj The other object.
		 *  @return True, if equal.
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

		/**
		 *  Get the outer type.
		 *  @return The outer type.
		 */
		private CleanerAgent getOuterType()
		{
			return CleanerAgent.this;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "AchieveCleanup: "+getWaste();
		}
	}
	
	/**
	 *  Goal that lets the cleaner look for waste.
	 */
	@Goal(excludemode=ExcludeMode.Never, orsuccess=false)
	public class PerformLookForWaste
	{
		/**
		 *  Suspend the goal at night.
		 */
		@GoalContextCondition//(beliefs="daytime")
		public boolean checkContext()
		{
			return daytime;
		}
	}
	
	/**
	 *  Goal that lets the agent perform patrol rounds.
	 */
	@Goal(excludemode=ExcludeMode.Never, orsuccess=false)
	public class PerformPatrol
	{
		/**
		 *  Suspend the goal when daytime.
		 */
		@GoalContextCondition//(beliefs="daytime")
		public boolean checkContext()
		{
			return !daytime;
		}
	}
	
	/**
	 *  Goal for picking up a piece of waste.
	 */
	@Goal(retry=false)
	public class AchievePickupWaste
	{
		/** The waste. */
		protected Waste waste;
		
		/**
		 *  Create a new goal.
		 */
		public AchievePickupWaste(Waste waste)
		{
			this.waste = waste;
		}
		
		/**
		 *  Get the piece of waste.
		 *  @return The piece of waste.
		 */
		public Waste getWaste()
		{
			return waste;
		}
	}
	
	/**
	 *  
	 */
	@Goal//(excludemode=ExcludeMode.Never)
	public class AchieveDropWaste
	{
		/** The wastebin. */
		protected Wastebin wastebin;
		
		/**
		 *  Create a new goal.
		 *  @param wastebin The wastebin.
		 */
		public AchieveDropWaste(Wastebin wastebin)
		{
			this.wastebin = wastebin;
		}
		
		/**
		 *  Drop the goal when the wastebin is full.
		 */
//		@GoalDropCondition(events="wastebin") // todo: check when parameter value changes
		public boolean checkContext()
		{
			return wastebin.isFull();
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
	
	/**
	 *  The goal is used to move to a specific location.
	 */
	@Goal
	public class AchieveMoveTo
	{
		/** The location. */
		protected Location location;
		
		/**
		 *  Create a new goal.
		 *  @param location The location.
		 */
		public AchieveMoveTo(Location location)
		{
//			System.out.println("created: "+location);
			this.location = location;
		}
		
		/**
		 *  The goal is achieved when the position
		 *  of the cleaner is near to the target position.
		 */
		@GoalTargetCondition//(beliefs="my_location")
		public boolean checkTarget()
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

	/**
	 *  
	 */
	@Goal(excludemode=ExcludeMode.Never)
	public class QueryWastebin
	{
		/** The wastebin. */
		protected Wastebin wastebin;
		
		@GoalTargetCondition(beliefs="wastebins")
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
	
	@Goal(excludemode=ExcludeMode.Never)
	public class QueryChargingStation
	{
		protected Chargingstation station;
		
		@GoalTargetCondition(beliefs="chargingstations")
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
	
	@Goal(excludemode=ExcludeMode.Never, orsuccess=false, retrydelay=300)
	public class PerformMemorizePositions
	{
	}
	
	@Plan(trigger=@Trigger(factchangeds={"environment", "my_location"}))
	protected IFuture<Void> updateVision(IPlan rplan)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Create a representation of myself.
		final Cleaner cl = new Cleaner(getMyLocation(),
			getAgent().getId().getLocalName(),
			getCarriedWaste(), getMyVision(),
			getMyChargestate());

		IFuture<GetVisionAction> fut = rplan.dispatchSubgoal(new GetVisionAction());
		fut.addResultListener(new ExceptionDelegationResultListener<CleanerAgent.GetVisionAction, Void>(ret)
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
		
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new PerformLookForWaste());
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new PerformPatrol());
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new MaintainBatteryLoaded());
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new PerformMemorizePositions());
		
//		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(100, new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				setMyChargestate(0.1);
//				return IFuture.DONE;
//			}
//		});
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
	public IInternalAccess getAgent()
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
