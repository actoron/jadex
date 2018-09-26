package quickstart.cleanerworld;

import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.quickstart.cleanerworld.environment.IChargingstation;
import jadex.quickstart.cleanerworld.environment.ICleaner;
import jadex.quickstart.cleanerworld.environment.IWaste;
import jadex.quickstart.cleanerworld.environment.IWastebin;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  Simple example of using the environment sensor.
 *  @author Alexander Pokahr
 *  @version 1.0 (2017/10/19)
 *
 */
@Agent(type="bdi")
public class SimpleCleanerBDIAgent
{
	//-------- beliefs that can be used in plan and goal conditions --------
	
	/** Set of the known wastes. Managed by SensorActuator object. */
	@Belief
	private Set<IWaste>	wastes	= new LinkedHashSet<>();
	
	/** Set of the known waste bins. Managed by SensorActuator object. */
	@Belief
	private Set<IWastebin>	wastebins	= new LinkedHashSet<>();
	
	/** Set of the known charging stations. Managed by SensorActuator object. */
	@Belief
	private Set<IChargingstation>	stations	= new LinkedHashSet<>();
	
	/** Set of the known other cleaners. Managed by SensorActuator object. */
	@Belief
	private Set<ICleaner>	others	= new LinkedHashSet<>();
	
	/** The sensor gives access to the environment. */
	private SensorActuator	actsense	= new SensorActuator(wastes, wastebins, stations, others);
	
	/** Knowledge about myself. Managed by SensorActuator object. */
	@Belief
	private ICleaner	self	= actsense.getSelf();
	
	/** Day or night?. Use updaterate to re-check every second. */
	@Belief(updaterate=1000)
	private boolean	daytime	= actsense.isDaytime();
	

	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@AgentBody
	private void	exampleBehavior(IBDIAgentFeature bdifeature)
	{
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);
		
		// Create a goal
		bdifeature.dispatchTopLevelGoal(new MoveGoal());
	}
	
	//-------- simple examples of using belief and goal events --------
	
	/**
	 *  Declare a plan using a method with @Plan and @Trigger annotation.
	 *  @param waste The new fact that triggered the plan execution.
	 */
	@Plan(trigger=@Trigger(factaddeds="wastes"))
	private void	examplePlanMethod(IWaste waste)
	{
		System.out.println("New waste seen: "+waste);
	}
	
	/**
	 *  Declare a goal using an inner class with @Goal annotation.
	 *  Use ExcludeMode.Never and orsuccess=false to keep executing the same plan(s) over and over.
	 */
	@Goal(excludemode=ExcludeMode.Never, orsuccess=false, recur=true)
	private class MoveGoal
	{
		/**
		 *  Monitor the charge state and drop the goal when battery below 30%. 
		 *  @return true to drop the goal.
		 */
		@GoalDropCondition
		private boolean drop()
		{
			return self.getChargestate() < 0.3;
		}
		
		/**
		 *  Monitor day time and restart moving when night is gone.
		 */
		@GoalRecurCondition
		private boolean move()
		{
			return daytime;
		}
	}
	
	/**
	 *  Declare a plan using an inner class with @Plan anmd @Trigger annotation
	 *  and a method with @PlanBody annotation.
	 */
	@Plan(trigger=@Trigger(goals=MoveGoal.class))
	private class examplePlanClass
	{
		@PlanBody
		private void examplePlanBodyMethod()
		{
			// move to random location in the area (0.0, 0.0) - (1.0, 1.0). 
			actsense.moveTo(Math.random(), Math.random());
		}
		
		/**
		 *  Stop moviong at night
		 *  @return true when moving still OK.
		 */
		@PlanContextCondition
		private boolean checkMove()
		{
			return daytime;
		}
	}
}
