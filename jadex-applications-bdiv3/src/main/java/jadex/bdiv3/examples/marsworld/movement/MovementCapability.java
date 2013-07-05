package jadex.bdiv3.examples.marsworld.movement;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Capability
@Plans({
	@Plan(trigger=@Trigger(goals=MovementCapability.Missionend.class), body=@Body(MoveToLocationPlan.class)),
	@Plan(trigger=@Trigger(goals=MovementCapability.WalkAround.class), body=@Body(RandomWalkPlan.class))
})
@RequiredServices(@RequiredService(name="clockser", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class MovementCapability
{
	//-------- attributes --------

	/** The capability. */
	@Agent
	protected ICapability	capa;
	
	/** The environment. */
	protected ContinuousSpace2D env = (ContinuousSpace2D)capa.getAgent().getParentAccess().getExtension("my2dspace").get();
	
	/** The environment. */
	protected ISpaceObject myself = env.getAvatar(capa.getAgent().getComponentDescription(), capa.getAgent().getModel().getFullName());

	/** The mission end. */
	@Belief(dynamic=true) // update rate 1000
	protected boolean missionend = ((Long)env.getSpaceObjectsByType("homebase")[0].getProperty("missiontime")).longValue()<=getTime();

	/** The targets. */
	@Belief
	protected List<ISpaceObject> mytargets = new ArrayList<ISpaceObject>();
	
	/**
	 *  The move goal.
	 *  Move to a certain location.
	 */
	@Goal
	public class Move
	{
		/** The destination. */
		protected IVector2 destination;

		/**
		 *  Create a new Move. 
		 */
		public Move(IVector2 destination)
		{
			this.destination = destination;
		}

		/**
		 *  Get the destination.
		 *  @return The destination.
		 */
		public IVector2 getDestination()
		{
			return destination;
		}
	}
	
	/**
	 *  The walk goal.
	 *  Walk around without target when nothing else to do.
	 */
	@Goal
	public class WalkAround
	{
		/**
		 *  Drop condition.
		 *  @return True if should be dropped.
		 */
		@GoalDropCondition(events="missionend")
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
	public static class Missionend
	{
		/**
		 *  Create a new Move. 
		 */
		@GoalCreationCondition(events="missionend")
		public static boolean checkCreate(MovementCapability capa)
		{
			return capa.missionend && !capa.myself.getProperty("position").equals(capa.getHomebasePosition());
		}
	}

	/**
	 * 
	 */
	public IVector2 getHomebasePosition()
	{
		return (IVector2)env.getSpaceObjectsByType("homebase")[0].getProperty("position");
	}
	
	/**
	 * 
	 */
	protected long getTime()
	{
		IClockService cs =  (IClockService)capa.getAgent().getServiceContainer().getRequiredService("clockser").get();
		return cs.getTime();
	}
	
	/**
	 *  Get the env.
	 *  @return The env.
	 */
	public ContinuousSpace2D getEnvironment()
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
			mytargets.add(target);
	}
}
