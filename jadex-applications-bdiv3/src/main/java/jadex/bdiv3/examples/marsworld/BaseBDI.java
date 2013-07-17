package jadex.bdiv3.examples.marsworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.examples.marsworld.movement.MovementCapability;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * 
 */
@Agent
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
	
//	@Plan(trigger=@Trigger(factchangeds="movecapa.missionend"))
//	public void missionend()
//	{
//		System.out.println("missionend: "+((Long)movecapa.getEnvironment().getSpaceObjectsByType("homebase")[0].getProperty("missiontime")));
//	}
}
