package jadex.quickstart.cleanerworld.single;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.quickstart.cleanerworld.environment.IChargingstation;
import jadex.quickstart.cleanerworld.environment.ICleaner;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  Using deliberation settings for managing conflicting goals.
 */
@Agent(type="bdi")	// This annotation makes the java class and agent and enabled BDI features
public class CleanerBDIAgentB2
{
	//-------- fields holding agent data --------
	
	/** The sensor/actuator object gives access to the environment of the cleaner robot. */
	private SensorActuator	actsense	= new SensorActuator();
	
	/** Knowledge of the cleaner about itself (e.g. location and charge state). */
	@Belief
	private ICleaner	self	= actsense.getSelf();
	
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@AgentBody	// This annotation informs the Jadex platform to call this method once the agent is started
	private void	exampleBehavior(IBDIAgentFeature bdi)
	{
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);
		
		// Create and dispatch agent goals.
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
			return self.getChargestate()>=0.2; // Everything is fine as long as the charge state is above 20%, otherwise the cleaner needs to recharge.
		}
	}
	
	//-------- methods that represent plans (i.e. predefined recipes for working on certain goals) --------
	
	/**
	 *  Declare a plan for the PerformPatrol goal by using a method with @Plan and @Trigger annotation.
	 */
	@Plan(trigger=@Trigger(goals=PerformPatrol.class))	// The plan annotation makes a method or class a plan. The trigger states, when the plan should considered for execution.
	private void	performPatrolPlan()
	{
		// Follow a simple path around the four corners of the museum and back to the first corner.
		System.out.println("Starting performPatrolPlan()");
		actsense.moveTo(0.1, 0.1);
		actsense.moveTo(0.1, 0.9);
		actsense.moveTo(0.9, 0.9);
		actsense.moveTo(0.9, 0.1);
		actsense.moveTo(0.1, 0.1);
	}

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
	private void loadBattery()
	{
		System.out.println("Starting loadBattery() plan");
		
		// Move to first known charging station -> fails when no charging station known.
		IChargingstation	chargingstation	= actsense.getChargingstations().iterator().next();
		actsense.moveTo(chargingstation.getLocation());
		
		// Load until 100% (never reached, but plan is aborted when goal succeeds).
		actsense.recharge(chargingstation, 1.0);
	}
}
