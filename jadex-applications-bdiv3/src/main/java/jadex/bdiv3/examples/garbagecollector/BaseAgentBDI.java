package jadex.bdiv3.examples.garbagecollector;

import java.util.List;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;

/**
 * 
 */
@Agent
public abstract class BaseAgentBDI
{
	@Agent
	protected BDIAgent agent;
	
	/** The environment. */
	protected Grid2D env = (Grid2D)agent.getParentAccess().getExtension("mygc2dspace").get();
	
	/** The environment. */
	protected ISpaceObject myself = env.getAvatar(agent.getComponentDescription(), agent.getModel().getFullName());

	/** The garbages. */
	@Belief
	protected List<ISpaceObject> garbages;
	
	/**
	 *  Get the env.
	 *  @return The env.
	 */
	public Grid2D getEnvironment()
	{
		return env;
	}

	/**
	 *  Get the pos.
	 *  @return The pos.
	 */
	public IVector2 getPosition()
	{
		return (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
//		return pos;
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
	 *  Get the agent.
	 *  @return The agent.
	 */
	public BDIAgent getAgent()
	{
		return agent;
	}
}
