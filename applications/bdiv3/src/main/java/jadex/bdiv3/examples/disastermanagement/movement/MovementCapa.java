package jadex.bdiv3.examples.disastermanagement.movement;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.application.EnvironmentService;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Capability
@Plans(@Plan(trigger=@Trigger(goals={MovementCapa.Move.class}), body=@Body(MoveToLocationPlan.class)))
@RequiredServices(@RequiredService(name="clockser", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class MovementCapa implements IEnvAccess
{
	//-------- attributes --------

	// Annotation to inform FindBugs that the uninitialized field is not a bug.
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")
	
	/** The capability. */
	@Agent
	protected ICapability capa;
	
	/** The environment. */
	protected ContinuousSpace2D env = (ContinuousSpace2D)EnvironmentService.getSpace(capa.getAgent(), "my2dspace").get();
	
	/** The environment. */
	protected ISpaceObject myself = env.getAvatar(capa.getAgent().getComponentDescription(), capa.getAgent().getModel().getFullName());

	/** The home position (=first position). */
	protected IVector2 homepos = myself!=null? (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION): null;
	
	/**
	 *  The move goal.
	 *  Move to a certain location.
	 */
	@Goal
	public class Move implements IDestinationGoal
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
	 * 
	 */
	public IVector2 getPosition()
	{
		return (IVector2)getMyself().getProperty("position");
	}

	/**
	 * 
	 */
	public IVector2 getHomePosition()
	{
		return homepos;
//		return (IVector2)getMyself().getProperty(Space2D.PROPERTY_POSITION);
	}
	

//	/**
//	 * 
//	 */
//	public Object getHomebasePosition()
//	{
//		return env.getSpaceObjectsByType("homebase")[0].getProperty("position");
//	}
//	
//	/**
//	 * 
//	 */
//	public ISpaceObject getHomebase()
//	{
//		return env.getSpaceObjectsByType("homebase")[0];
//	}
	
//	/**
//	 * 
//	 */
//	protected long getTime()
//	{
//		// todo:
//		IClockService cs =  (IClockService)capa.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("clockser").get();
//		return cs.getTime();
//	}
	
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
}

