package jadex.bdiv3.examples.marsworld.movement;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.application.EnvironmentService;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Capability
@Plans({
	@Plan(trigger=@Trigger(goals={MovementCapability.Move.class, MovementCapability.Missionend.class}), body=@Body(MoveToLocationPlan.class)),
	@Plan(trigger=@Trigger(goals=MovementCapability.WalkAround.class), body=@Body(RandomWalkPlan.class))
})
@RequiredServices(@RequiredService(name="clockser", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class MovementCapability
{
	//-------- attributes --------

	// Annotation to inform FindBugs that the uninitialized field is not a bug.
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")
	
	/** The capability. */
	@Agent
	protected ICapability	capa;
	
	/** The environment. */
	protected AbstractEnvironmentSpace env = (AbstractEnvironmentSpace)EnvironmentService.getSpace(capa.getAgent(), "myspace").get();
	
	/** The environment. */
	protected ISpaceObject myself = env.getAvatar(capa.getAgent().getComponentDescription(), capa.getAgent().getModel().getFullName());

	/** The mission end. */
//	@Belief(dynamic=true, updaterate=1000) 
	@Belief(updaterate=1000) 
	protected boolean missionend = ((Long)env.getSpaceObjectsByType("homebase")[0].getProperty("missiontime")).longValue()<=getTime();

	/** The targets. */
	@Belief
	protected List<ISpaceObject> mytargets = new ArrayList<ISpaceObject>();
	
	/**
	 *  The move goal.
	 *  Move to a certain location.
	 */
	@Goal
	public class Move implements IDestinationGoal
	{
		/** The destination. */
		protected Object destination;

		/**
		 *  Create a new Move. 
		 */
		public Move(Object destination)
		{
			this.destination = destination;
		}

		/**
		 *  Get the destination.
		 *  @return The destination.
		 */
		public Object getDestination()
		{
			return destination;
		}
	}
	
	/**
	 *  The walk goal.
	 *  Walk around without target when nothing else to do.
	 */
	@Goal(orsuccess=false, excludemode=ExcludeMode.Never)
	public class WalkAround
	{
		/**
		 *  Drop condition.
		 *  @return True if should be dropped.
		 */
		@GoalDropCondition(beliefs="missionend")
		public boolean checkDrop()
		{
			return missionend;
		}
	}
	
	/**
	 *  The mission end goal.
	 *  Move to homebase on end.
	 */
	@Goal(unique=true)
	public static class Missionend implements IDestinationGoal
	{
		/** The movement capability. */
		protected MovementCapability capa;
		
		/**
		 *  Create a new goal.
		 */
		public Missionend(MovementCapability capa)
		{
			this.capa = capa;
		}
		
		/**
		 *  Create a new Move. 
		 */
		@GoalCreationCondition(beliefs="missionend")
		public static boolean checkCreate(MovementCapability capa)
		{
			return capa.missionend && !capa.myself.getProperty("position").equals(capa.getHomebasePosition());
		}
		
		/**
		 *  Get the destination.
		 *  @return The destination.
		 */
		public Object getDestination()
		{
			return capa.getHomebasePosition();
		}
	}

	/**
	 * 
	 */
	public Object getHomebasePosition()
	{
		return env.getSpaceObjectsByType("homebase")[0].getProperty("position");
	}
	
	/**
	 * 
	 */
	public ISpaceObject getHomebase()
	{
		return env.getSpaceObjectsByType("homebase")[0];
	}
	
	/**
	 * 
	 */
	protected long getTime()
	{
		IClockService cs = SServiceProvider.getLocalService(capa.getAgent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		// todo: capa.getAgent().getComponentFeature().getRequiredService() does not work in init expressions only from plans :-(
//		IClockService cs =  (IClockService)capa.getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("clockser").get();
		return cs.getTime();
	}
	
	/**
	 *  Get the env.
	 *  @return The env.
	 */
	public AbstractEnvironmentSpace getEnvironment()
	{
		return env;
	}

	/**
	 *  Get the myself.
	 *  @return The myself.
	 */
	public ISpaceObject getMyself()
	{
		return myself;
	}

	/**
	 *  Get the capa.
	 *  @return The capa.
	 */
	public ICapability getCapability()
	{
		return capa;
	}

	/**
	 *  Get the my_targets.
	 *  @return The my_targets.
	 */
	public List<ISpaceObject> getMyTargets()
	{
		return mytargets;
	}

	/**
	 *  Get the missionend.
	 *  @return The missionend.
	 */
	public boolean isMissionend()
	{
		return missionend;
	}
	
	/**
	 * 
	 */
	public void addTarget(ISpaceObject target)
	{
		if(!mytargets.contains(target))
		{
//			System.out.println("added target: "+capa.getAgent().getAgentName()+" "+target);
			mytargets.add(target);
		}
	}
}
