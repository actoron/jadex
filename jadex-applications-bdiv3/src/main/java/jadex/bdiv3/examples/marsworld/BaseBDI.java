package jadex.bdiv3.examples.marsworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
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
public abstract class BaseBDI implements ITargetAnnouncementService
{
	@Agent 
	protected BDIAgent agent;
	
	/** The customer capability. */
	@Capability
	protected MovementCapability movecapa = new MovementCapability();
	
	/**
	 * 
	 */
	public IFuture<Void> announceNewTarget(ISpaceObject target)
	{
		movecapa.addTarget(target);
		return IFuture.DONE;
	}

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
}
