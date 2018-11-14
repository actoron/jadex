package jadex.bdiv3.examples.garbagecollector;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.application.EnvironmentService;
import jadex.bdiv3.annotation.Belief;
import jadex.bridge.IInternalAccess;
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
	// Annotation to inform FindBugs that the uninitialized field is not a bug.
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")

	@Agent
	protected IInternalAccess agent;
	
	/** The environment. */
	protected Grid2D env = (Grid2D)EnvironmentService.getSpace(agent, "mygc2dspace").get();
	
	/** The environment. */
	protected ISpaceObject myself = env.getAvatar(agent.getComponentDescription(), agent.getModel().getFullName());

	/** The garbages. */
	@Belief
	protected List<ISpaceObject> garbages = new ArrayList<ISpaceObject>();
	
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
	public IInternalAccess getAgent()
	{
		return agent;
	}
}
