package jadex.bdiv3.examples.disastermanagement.firebrigade;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Publish;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.disastermanagement.IClearChemicalsService;
import jadex.bdiv3.examples.disastermanagement.IExtinguishFireService;
import jadex.bdiv3.examples.disastermanagement.movement.IDestinationGoal;
import jadex.bdiv3.examples.disastermanagement.movement.IEnvAccess;
import jadex.bdiv3.examples.disastermanagement.movement.MoveToLocationPlan;
import jadex.bdiv3.examples.disastermanagement.movement.MovementCapa;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
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
//@ProvidedServices(
//{
//	@ProvidedService(type=IExtinguishFireService.class, implementation=@Implementation(ExtinguishFireService.class)),
//	@ProvidedService(type=IClearChemicalsService.class, implementation=@Implementation(ClearChemicalsService.class))
//})
@Plans(
{
	@Plan(trigger=@Trigger(goals=FireBrigadeAgent.ExtinguishFire.class), body=@Body(ExtinguishFirePlan.class)),
	@Plan(trigger=@Trigger(goals=FireBrigadeAgent.ClearChemicals.class), body=@Body(ClearChemicalsPlan.class)),
	@Plan(trigger=@Trigger(goals=FireBrigadeAgent.GoHome.class), body=@Body(MoveToLocationPlan.class))
})
@Configurations({@Configuration(name="do_nothing"), @Configuration(name="default")})
public class FireBrigadeAgent implements IEnvAccess
{
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
			agent.getFeature(IBDIAgentFeature.class).adoptPlan(new FireBrigadePlan());
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
		@GoalCreationCondition(rawevents=@RawEvent(ChangeEvent.GOALDROPPED))
		public static GoHome checkCreate(FireBrigadeAgent ag)
		{
			MovementCapa capa = ag.getMoveCapa();
//			System.out.println("check create go home: "+capa.getCapability().getAgent().getGoals().size()+" "+capa.getCapability().getAgent().getAgentName());
			
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
		@GoalDropCondition(rawevents={@RawEvent(ChangeEvent.GOALADOPTED), @RawEvent(ChangeEvent.GOALDROPPED)})
		public boolean checkDrop(FireBrigadeAgent ag)
		{
			MovementCapa capa = ag.getMoveCapa();
			boolean ret = capa.getCapability().getAgent().getFeature(IBDIAgentFeature.class).getGoals().size()>1;
//			System.out.println("check drop fire brigade: "+this+" "+capa.getCapability().getAgent().getGoals());
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
	@Goal(excludemode=ExcludeMode.WhenFailed, deliberation=@Deliberation(cardinalityone=true, inhibits=ExtinguishFire.class), 
		publish=@Publish(type=IExtinguishFireService.class, method="extinguishFire"))
//	public static class ExtinguishFire
	public class ExtinguishFire
	{
		/** The disaster. */
		@GoalParameter
		protected ISpaceObject disaster;

		/**
		 *  Create a new ExtinguishFire. 
		 */
		public ExtinguishFire(ISpaceObject disaster)
		{
			this.disaster = disaster;
		}
		
		/**
		 *  Create a new ExtinguishFire. 
		 */
		public ExtinguishFire(Object disasterid)
		{
			this.disaster = movecapa.getEnvironment().getSpaceObject(disasterid);
		}
		
		/**
		 * 
		 */
		@GoalTargetCondition(parameters = "disaster")
		public boolean checkTarget()
		{
			Integer cnt = (Integer)getDisaster().getProperty("fire");
			boolean ret = cnt != null && cnt.intValue() == 0;
//			System.out.println("checkTarget: " + ret + " fires: " + cnt);
			return ret;
		}

		/**
		 *  Drop if this goal is only option and there are others.
		 */
		@GoalDropCondition(rawevents={@RawEvent(ChangeEvent.GOALOPTION), @RawEvent(ChangeEvent.GOALADOPTED)})
		public boolean checkDrop(FireBrigadeAgent ag, IGoal goal)
		{
			MovementCapa capa = ag.getMoveCapa();
			boolean ret = GoalLifecycleState.OPTION.equals(goal.getLifecycleState()) &&
				capa.getCapability().getAgent().getFeature(IBDIAgentFeature.class).getGoals(ExtinguishFire.class).size()>1;
//			if(ret)
//				System.out.println("dropping ext fire: "+disaster);
			return ret;
		}
		
		/**
		 *  Get the disaster.
		 *  @return The disaster.
		 */
		public ISpaceObject getDisaster()
		{
			return disaster;
		}
	}
	
	/**
	 * 
	 */
	@Goal(excludemode=ExcludeMode.WhenFailed, deliberation=@Deliberation(cardinalityone=true, inhibits={ExtinguishFire.class, ClearChemicals.class}), 
		publish=@Publish(type=IClearChemicalsService.class, method="clearChemicals"))
//	public static class ClearChemicals
	public class ClearChemicals
	{
		/** The disaster. */
		@GoalParameter
		protected ISpaceObject disaster;

		/**
		 *  Create a new ExtinguishFire. 
		 */
		public ClearChemicals(ISpaceObject disaster)
		{
			this.disaster = disaster;
		}
		
		/**
		 *  Create a new ExtinguishFire. 
		 */
		public ClearChemicals(Object disasterid)
		{
			this.disaster = movecapa.getEnvironment().getSpaceObject(disasterid);
		}
		
		/**
		 * 
		 */
		@GoalTargetCondition(parameters = "disaster")
		public boolean checkTarget()
		{
			Integer cnt = (Integer)getDisaster().getProperty("chemicals");
			return  cnt!=null && cnt.intValue()==0;
		}
		
		/**
		 *  Drop if this goal is only option and there are others.
		 */
		@GoalDropCondition(rawevents={@RawEvent(ChangeEvent.GOALOPTION), @RawEvent(ChangeEvent.GOALADOPTED)})
		public boolean checkDrop(FireBrigadeAgent ag, IGoal goal)
		{
			MovementCapa capa = ag.getMoveCapa();
			boolean ret = GoalLifecycleState.OPTION.equals(goal.getLifecycleState()) &&
				capa.getCapability().getAgent().getFeature(IBDIAgentFeature.class).getGoals(ClearChemicals.class).size()>1;
//			if(ret)
//				System.out.println("dropping clear chemicals: "+disaster);
			return ret;
		}

		/**
		 *  Get the disaster.
		 *  @return The disaster.
		 */
		public ISpaceObject getDisaster()
		{
			return disaster;
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



