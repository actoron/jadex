package jadex.bdiv3.examples.marsworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.bdiv3.examples.marsworld.sentry.ITargetAnnouncementService;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="targetser", multiple=true, type=ITargetAnnouncementService.class))
public abstract class BaseBDI 
{
	@Agent 
	protected BDIAgent agent;
	
	/** The customer capability. */
	@Capability
	protected MovementCapability movecapa = new MovementCapability();

	/**
	 *  Get the movecapa.
	 *  @return The movecapa.
	 */
	public MovementCapability getMoveCapa()
	{
		return movecapa;
	}
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		agent.dispatchTopLevelGoal(movecapa.new WalkAround());
	}

	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public BDIAgent getAgent()
	{
		return agent;
	}
	
	@Plan(trigger=@Trigger(factchangeds="movecapa.missionend"))
	public void missionend()
	{
		System.out.println("missionend: "+((Long)movecapa.getEnvironment().getSpaceObjectsByType("homebase")[0].getProperty("missiontime")));
	}
}
