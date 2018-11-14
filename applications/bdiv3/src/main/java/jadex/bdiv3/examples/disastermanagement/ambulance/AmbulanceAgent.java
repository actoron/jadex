package jadex.bdiv3.examples.disastermanagement.ambulance;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Publish;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.disastermanagement.ITreatVictimsService;
import jadex.bdiv3.examples.disastermanagement.movement.IDestinationGoal;
import jadex.bdiv3.examples.disastermanagement.movement.IEnvAccess;
import jadex.bdiv3.examples.disastermanagement.movement.MoveToLocationPlan;
import jadex.bdiv3.examples.disastermanagement.movement.MovementCapa;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IGoal.GoalLifecycleState;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

/**
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@Service
//@ProvidedServices(@ProvidedService(type=ITreatVictimsService.class, implementation=@Implementation(TreatVictimsService.class)))
@Plans(
{
	@Plan(trigger=@Trigger(goals=AmbulanceAgent.TreatVictims.class), body=@Body(TreatVictimPlan.class)),
	@Plan(trigger=@Trigger(goals=AmbulanceAgent.GoHome.class), body=@Body(MoveToLocationPlan.class))
})
@Configurations({@Configuration(name="do_nothing"), @Configuration(name="default")})
public class AmbulanceAgent implements IEnvAccess
{
//	public static final String GOH = GoHome.class.getName();
	
	/** The capa. */
	@Capability
	protected MovementCapa movecapa = new MovementCapa();
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		if("default".equals(agent.getConfiguration()))
		{
			agent.getFeature(IBDIAgentFeature.class).adoptPlan(new AmbulancePlan());
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
		// todo: GoHome.class.getName();
		@GoalCreationCondition(rawevents={@RawEvent(ChangeEvent.GOALADOPTED),
			@RawEvent(ChangeEvent.GOALDROPPED)})
		public static GoHome checkCreate(AmbulanceAgent ag)
		{
			MovementCapa capa = ag.getMoveCapa();
			if(capa.getCapability().getAgent().getFeature(IBDIAgentFeature.class).getGoals().size()==0 && capa.getHomePosition()!=null && capa.getPosition()!=null
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
		@GoalDropCondition(rawevents={@RawEvent(ChangeEvent.GOALADOPTED),
			@RawEvent(ChangeEvent.GOALDROPPED)})
		public boolean checkDrop(AmbulanceAgent ag)
		{
			MovementCapa capa = ag.getMoveCapa();
			boolean ret = capa.getCapability().getAgent().getFeature(IBDIAgentFeature.class).getGoals().size()>1;
//			System.out.println("dropping: "+this);
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
	@Goal(deliberation=@Deliberation(cardinalityone=true),
		publish=@Publish(type=ITreatVictimsService.class, method="treatVictims"))
//	public static class TreatVictims
	public class TreatVictims
	{
		/** The disaster. */
		protected ISpaceObject disaster;
//		protected Object disasterid;

//		/**
//		 *  Create a new TreatVictims. 
//		 */
//		public TreatVictims(ISpaceObject disaster)
//		{
////			System.out.println("created treat victims");
//			this.disaster = disaster;
//		}
		
		/**
		 *  Create a new TreatVictims. 
		 */
		public TreatVictims(Object disasterid)
		{
//			System.out.println("created treat victims goal: "+disasterid);
			this.disaster = movecapa.getEnvironment().getSpaceObject(disasterid);
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
		@GoalDropCondition(rawevents={@RawEvent(value=ChangeEvent.GOALOPTION, secondc=TreatVictims.class)})
		public boolean checkDrop(AmbulanceAgent ag, IGoal goal)
		{
			MovementCapa capa = ag.getMoveCapa();
			boolean ret = GoalLifecycleState.OPTION.equals(goal.getLifecycleState()) &&
				capa.getCapability().getAgent().getFeature(IBDIAgentFeature.class).getGoals(TreatVictims.class).size()>1;
			if(ret)
				System.out.println("dropping treat victim: "+disaster);
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
	public IInternalAccess getAgent()
	{
		return agent;
	}
	
	/**
	 *  Get the env.
	 *  @return The env.
	 */
	public ContinuousSpace2D getEnvironment()
	{
		return getMoveCapa().getEnvironment();
	}

	/**
	 *  Get the myself.
	 *  @return The myself.
	 */
	public ISpaceObject getMyself()
	{
		return getMoveCapa().getMyself();
	}
}



