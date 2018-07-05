package jadex.bdiv3.examples.marsworld;

import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * 
 */
@Agent
public abstract class BaseBDI 
{
	@Agent 
	protected IInternalAccess agent;
	
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
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(movecapa.new WalkAround());
	}

	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public IInternalAccess getAgent()
	{
		return agent;
	}
	
//	@Plan(trigger=@Trigger(factchangeds="movecapa.missionend"))
//	public void missionend()
//	{
//		System.out.println("missionend: "+((Long)movecapa.getEnvironment().getSpaceObjectsByType("homebase")[0].getProperty("missiontime")));
//	}
}
