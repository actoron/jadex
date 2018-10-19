package jadex.quickstart.cleanerworld.single;

import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalInhibit;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.quickstart.cleanerworld.environment.IChargingstation;
import jadex.quickstart.cleanerworld.environment.ICleaner;
import jadex.quickstart.cleanerworld.environment.IWaste;
import jadex.quickstart.cleanerworld.environment.IWastebin;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  More or less working solution for a BDI cleaner.
 *  @author Alexander Pokahr
 *  @version 1.0 (2017/10/19)
 *
 */
@Agent(type="bdi")
public class CleanerBDIAgentFinal
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
	private SensorActuator	actsense	= new SensorActuator();
	
	/** Knowledge about myself. Managed by SensorActuator object. */
	@Belief
	private ICleaner	self	= actsense.getSelf();
	
	/** Day or night?. Use updaterate to re-check every second. */
	@Belief(updaterate=1000)
	private boolean	daytime	= actsense.isDaytime();
	
	@AgentArgument
	private boolean	sensorgui	= true;
	
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@AgentBody
	private void	exampleBehavior(IBDIAgentFeature bdifeature)
	{
		// Tell the sensor to update the belief sets
		actsense.manageWastesIn(wastes);
		actsense.manageWastebinsIn(wastebins);
		actsense.manageChargingstationsIn(stations);
		actsense.manageCleanersIn(others);
		
		// Open a window showing the agent's perceptions
		if(sensorgui)
			new SensorGui(actsense).setVisible(true);
		
		// Create a goal
		bdifeature.dispatchTopLevelGoal(new PerformLookForWaste());
	}
	
	//-------- look for waste --------
		
	/**
	 *  Declare a goal using an inner class with @Goal annotation.
	 *  Use ExcludeMode.Never and orsuccess=false to keep executing the same plan(s) over and over.
	 */
	@Goal(excludemode=ExcludeMode.Never, orsuccess=false)
	private class PerformLookForWaste
	{
		/**
		 *  Monitor day time and restart moving when night is gone.
		 */
		@GoalContextCondition
		private boolean context()
		{
			return daytime;
		}
	}
	
	/**
	 *  Declare a plan using an inner class with @Plan anmd @Trigger annotation
	 *  and a method with @PlanBody annotation.
	 */
	@Plan(trigger=@Trigger(goals= {PerformLookForWaste.class, QueryWastebin.class}))
	private class moveAround
	{
		@PlanBody
		private void doMoveAround()
		{
			// move to random location in the area (0.0, 0.0) - (1.0, 1.0). 
			actsense.moveTo(Math.random(), Math.random());
		}
	}
	
	//-------- cleanup waste --------
	
	@Goal(deliberation=@Deliberation(inhibits= {PerformLookForWaste.class, AchieveCleanupWaste.class}), unique=true)
	class AchieveCleanupWaste
	{
		private IWaste	waste;
		
		@GoalCreationCondition(beliefs="wastes")
		AchieveCleanupWaste(@CheckNotNull IWaste waste)
		{
			System.out.println("achieve cleanup: "+waste);
			this.waste	= waste;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof AchieveCleanupWaste && ((AchieveCleanupWaste)obj).waste.equals(waste);
		}
		
		@Override
		public int hashCode()
		{
			return 31+waste.hashCode();
		}
		
		@GoalInhibit(AchieveCleanupWaste.class)
		private boolean	inhibitOther(AchieveCleanupWaste other)
		{
			return waste.equals(self.getCarriedWaste()) || 
				! other.waste.equals(self.getCarriedWaste())
					&& waste.getLocation().getDistance(self.getLocation())
						< other.waste.getLocation().getDistance(self.getLocation());
		}
	}
	
	@Plan(trigger=@Trigger(goals=AchieveCleanupWaste.class))
	private void	achieveCleanupPlan(AchieveCleanupWaste goal, IInternalAccess bdifeature)
	{
		System.out.println("Moving to: "+goal.waste);
		actsense.moveTo(goal.waste.getLocation().getX(), goal.waste.getLocation().getY());
		
		System.out.println("Pickung up: "+goal.waste);
		actsense.pickUpWaste(goal.waste);
		
		System.out.println("Querying waste bin: "+goal.waste);
		IWastebin	wastebin	= (IWastebin)bdifeature.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new QueryWastebin()).get();

		System.out.println("Moving to waste bin: "+goal.waste+", "+wastebin);		
		actsense.moveTo(wastebin.getLocation().getX(), wastebin.getLocation().getY());
		
		System.out.println("Dropping in waste bin: "+goal.waste+", "+wastebin);		
		actsense.dropWasteInWastebin(goal.waste, wastebin);
		System.out.println("Dropped in waste bin: "+goal.waste+", "+wastebin);		
	}
	
	@Goal(excludemode=ExcludeMode.Never)
	private class QueryWastebin
	{
		@GoalResult
		protected IWastebin wastebin;
		
		@GoalTargetCondition(beliefs="wastebins")
		public boolean checkTarget()
		{
			for(IWastebin wb: wastebins)
			{
				if(!wb.isFull())
				{
					if(wastebin==null)
					{
						wastebin = wb;
					}
					else if(self.getLocation().getDistance(wb.getLocation())
						<self.getLocation().getDistance(wastebin.getLocation()))
					{
						wastebin = wb;
					}
				}
			}
			return wastebin!=null;
		}
	}

}
