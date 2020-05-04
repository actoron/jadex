package jadex.quickstart.cleanerworld.single;

import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.quickstart.cleanerworld.environment.IChargingstation;
import jadex.quickstart.cleanerworld.environment.ICleaner;
import jadex.quickstart.cleanerworld.environment.IWaste;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  Separate Maintain and Target Conditions.
 */
@Agent(type="bdi")	// This annotation makes the java class and agent and enabled BDI features
public class CleanerBDIAgentD2
{
	//-------- fields holding agent data --------
	
	/** The sensor/actuator object gives access to the environment of the cleaner robot. */
	private SensorActuator	actsense	= new SensorActuator();
	
	/** Knowledge of the cleaner about itself (e.g. location and charge state). */
	@Belief
	private ICleaner	self	= actsense.getSelf();
	
	/** Set of the known charging stations. Managed by SensorActuator object. */
	@Belief
	private Set<IChargingstation>	stations	= new LinkedHashSet<>();
	
	/** Set of the known waste items. Managed by SensorActuator object. */
	@Belief
	private Set<IWaste>	wastes	= new LinkedHashSet<>();
	
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@OnStart	// This annotation informs the Jadex platform to call this method once the agent is started
	private void	exampleBehavior(IBDIAgentFeature bdi)
	{
		// Tell the sensor to update the belief sets
		actsense.manageChargingstationsIn(stations);
		actsense.manageWastesIn(wastes);

		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);
		
		// Create and dispatch a goal.
		bdi.dispatchTopLevelGoal(new PerformPatrol());
		bdi.dispatchTopLevelGoal(new MaintainBatteryLoaded());
	}
	
	//-------- inner classes that represent agent goals --------
	
	/**
	 *  A goal to patrol around in the museum.
	 */
	@Goal(recur=true, orsuccess=false, recurdelay=3000)
//	@Goal(recur=true, orsuccess=false, retrydelay=3000)	// Goal flags: variation 1
//	@Goal(recur=true, randomselection=true, recurdelay=3000) // Goal flags: variation 2
//	@Goal(orsuccess=false, excludemode=ExcludeMode.WhenFailed, randomselection=true, retrydelay=3000) // Goal flags: variation 3
//	@Goal(recur=true, posttoall=true) // Goal flags: variation 4
	class PerformPatrol {}
	
	/**
	 *  A goal to recharge whenever the battery is low.
	 */
	@Goal(recur=true, recurdelay=3000,
		deliberation=@Deliberation(inhibits=PerformPatrol.class))	// Pause patrol goal while loading battery
	class MaintainBatteryLoaded
	{
		@GoalMaintainCondition	// The cleaner aims to maintain the following expression, i.e. act to restore the condition, whenever it changes to false.
		boolean isBatteryLoaded()
		{
			return self.getChargestate()>=0.4; // Everything is fine as long as the charge state is above 20%, otherwise the cleaner needs to recharge.
		}
			
		@GoalTargetCondition	// Only stop charging, when this condition is true
		boolean isBatteryFullyLoaded()
		{
			return self.getChargestate()>=0.9; // Charge until 90%
		}
	}
	
	/**
	 *  A goal to know a charging station.
	 */
	@Goal(excludemode=ExcludeMode.Never)
	class QueryChargingStation
	{
		// Remember the station when found
		IChargingstation	station;
		
		// Check if there is a station in the beliefs
		@GoalTargetCondition
		boolean isStationKnown()
		{
			station	= stations.isEmpty() ? null : stations.iterator().next();
			return station!=null;
		}
	}

	/**
	 *  A goal to cleanup waste.
	 */
	@Goal(recur=true, recurdelay=3000)
	class AchieveCleanupWaste
	{
		// Remember the waste item to clean up
		IWaste	waste;
		
		// Create a new goal instance for each new waste item
		@GoalCreationCondition(factadded="wastes")
		public AchieveCleanupWaste(IWaste waste)
		{
			System.out.println("Created achieve cleanup goal for "+waste);
			this.waste = waste;
		}
		
		// The goal is achieved, when the waste is gone.
		@GoalTargetCondition
		boolean	isClean()
		{
			return !wastes.contains(waste);
		}
	}
	
	//-------- methods that represent plans (i.e. predefined recipes for working on certain goals) --------
	
//	/**
//	 *  Declare a plan for the PerformPatrol goal by using a method with @Plan and @Trigger annotation.
//	 */
//	@Plan(trigger=@Trigger(goals=PerformPatrol.class))	// The plan annotation makes a method or class a plan. The trigger states, when the plan should considered for execution.
//	private void	performPatrolPlan()
//	{
//		// Follow a simple path around the four corners of the museum and back to the first corner.
//		System.out.println("Starting performPatrolPlan()");
//		actsense.moveTo(0.1, 0.1);
//		actsense.moveTo(0.1, 0.9);
//		actsense.moveTo(0.9, 0.9);
//		actsense.moveTo(0.9, 0.1);
//		actsense.moveTo(0.1, 0.1);
//	}

	/**
	 *  Declare a second plan for the PerformPatrol goal.
	 */
	@Plan(trigger=@Trigger(goals=PerformPatrol.class))
	private void	performPatrolPlan2()
	{
		// Follow another path around the middle of the museum.
		System.out.println("Starting performPatrolPlan2()");
		actsense.moveTo(0.3, 0.3);
		actsense.moveTo(0.3, 0.7);
		actsense.moveTo(0.7, 0.7);
		actsense.moveTo(0.7, 0.3);
		actsense.moveTo(0.3, 0.3);
	}
	
	/**
	 *  Declare a third plan for the PerformPatrol goal.
	 */
	@Plan(trigger=@Trigger(goals=PerformPatrol.class))
	private void	performPatrolPlan3()
	{
		// Follow a zig-zag path in the museum.
		System.out.println("Starting performPatrolPlan3()");
		actsense.moveTo(0.3, 0.3);
		actsense.moveTo(0.7, 0.7);
		actsense.moveTo(0.3, 0.7);
		actsense.moveTo(0.7, 0.3);
		actsense.moveTo(0.3, 0.3);
	}
	
	/**
	 *  Move to charging station and load battery.
	 */
	@Plan(trigger=@Trigger(goals=MaintainBatteryLoaded.class))
	private void loadBattery(IPlan plan)
	{
		System.out.println("Starting loadBattery() plan");
		
//		// Move to first known charging station -> fails when no charging station known.
//		IChargingstation	chargingstation	= actsense.getChargingstations().iterator().next();	// from Exercise B1
//		IChargingstation	chargingstation	= stations.iterator().next();	// from Exercise C0
		
		// Dispatch a subgoal to find a charging station (from Exercise C1)
		QueryChargingStation	querygoal	= new QueryChargingStation();
		plan.dispatchSubgoal(querygoal).get();
		IChargingstation	chargingstation	= querygoal.station;
		
		// Move to charging station as provided by subgoal
		actsense.moveTo(chargingstation.getLocation());
		
		// Load until 100% (never reached, but plan is aborted when goal succeeds).
		actsense.recharge(chargingstation, 1.0);
	}
	
	/**
	 *  A plan to move randomly in the environment.
	 */
	@Plan(trigger=@Trigger(goals=QueryChargingStation.class))
	private void	moveAround()
	{
		// Choose a random location and move there.
		System.out.println("Starting moveAround() plan");
		actsense.moveTo(Math.random(), Math.random());
	}
	
	/**
	 *  A plan to boast about achieved goals.
	 */
	@Plan(trigger=@Trigger(goalfinisheds=AchieveCleanupWaste.class))
	private void	finishedCleanup(IGoal goal, AchieveCleanupWaste cleanup)
	{
		String	state	= goal.getProcessingState().toString();
		System.out.println("finished goal with state "+state+" for "+cleanup.waste);
	}
}
