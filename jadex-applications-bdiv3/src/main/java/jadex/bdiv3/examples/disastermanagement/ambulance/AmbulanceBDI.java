package jadex.bdiv3.examples.disastermanagement.ambulance;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.disastermanagement.ITreatVictimsService;
import jadex.bdiv3.examples.disastermanagement.movement.IDestinationGoal;
import jadex.bdiv3.examples.disastermanagement.movement.MoveToLocationPlan;
import jadex.bdiv3.examples.disastermanagement.movement.MovementCapa;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IGoal.GoalLifecycleState;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bridge.service.annotation.Service;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITreatVictimsService.class, implementation=@Implementation(TreatVictimsService.class)))
@Plans(
{
	@Plan(trigger=@Trigger(goals=AmbulanceBDI.TreatVictims.class), body=@Body(TreatVictimPlan.class)),
	@Plan(trigger=@Trigger(goals=AmbulanceBDI.GoHome.class), body=@Body(MoveToLocationPlan.class))
})
@Configurations({@Configuration(name="do_nothing"), @Configuration(name="default")})
public class AmbulanceBDI
{
	/** The capa. */
	@Capability
	protected MovementCapa movecapa = new MovementCapa();
	
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		if("default".equals(agent.getConfiguration()))
		{
			agent.adoptPlan(new AmbulancePlan());
		}
	}
	
	/**
	 * 
	 */
	@Goal
	public static class GoHome implements IDestinationGoal
	{
		/** The home position. */
		protected IVector2 home;
		
		/**
		 *  Create a new CarryOre. 
		 */
		public GoHome(IVector2 home)
		{
			this.home = home;
		}
		
		/**
		 *  Create a new Move. 
		 */
		@GoalCreationCondition(rawevents={ChangeEvent.GOALADOPTED, ChangeEvent.GOALDROPPED})
		public static GoHome checkCreate(AmbulanceBDI ag)
		{
			MovementCapa capa = ag.getMoveCapa();
			if(capa.getCapability().getAgent().getGoals().size()==0 && capa.getHomePosition()!=null && capa.getPosition()!=null
				&& capa.getEnvironment().getDistance(capa.getHomePosition(), capa.getPosition()).getAsDouble()>0.001)
			{
				return new GoHome(capa.getHomePosition());
			}
			else
			{
				return null;
			}
		}
		
		/**
		 *  Drop if there is another goal.
		 */
		@GoalDropCondition(rawevents={ChangeEvent.GOALADOPTED, ChangeEvent.GOALDROPPED})
		public boolean checkDrop(AmbulanceBDI ag)
		{
			MovementCapa capa = ag.getMoveCapa();
			boolean ret = capa.getCapability().getAgent().getGoals().size()>1;
			System.out.println("dropping: "+this);
			return ret;
		}
		
		/**
		 *  Get the destination.
		 *  @return The destination.
		 */
		public IVector2 getDestination()
		{
			return home;
		}
	}
	
	/**
	 * 
	 */
	@Goal(deliberation=@Deliberation(inhibits=TreatVictims.class, cardinalityone=true))
	public static class TreatVictims
	{
		/** The disaster. */
		protected ISpaceObject disaster;

		/**
		 *  Create a new TreatVictims. 
		 */
		public TreatVictims(ISpaceObject disaster)
		{
			this.disaster = disaster;
		}

		/**
		 *  Get the disaster.
		 *  @return The disaster.
		 */
		public ISpaceObject getDisaster()
		{
			return disaster;
		}

		/**
		 *  Drop if this goal is only option and there are others.
		 */
		@GoalDropCondition
		public boolean checkDrop(AmbulanceBDI ag, RGoal goal)
		{
			System.out.println(GoalLifecycleState.OPTION.getClass().getClassLoader());
			System.out.println(GoalLifecycleState.OPTION.getClass().getClassLoader());
			MovementCapa capa = ag.getMoveCapa();
			boolean ret = GoalLifecycleState.OPTION.equals(goal.getLifecycleState()) &&
				capa.getCapability().getAgent().getGoals(TreatVictims.class).size()>1;
			return ret;
		}
	}

	/**
	 *  Get the movecapa.
	 *  @return The movecapa.
	 */
	public MovementCapa getMoveCapa()
	{
		return movecapa;
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



