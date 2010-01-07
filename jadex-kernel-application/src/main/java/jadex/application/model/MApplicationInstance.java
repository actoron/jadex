package jadex.application.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Application instance representation.
 */
public class MApplicationInstance
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of spaces. */
	protected List spaces;
	
	/** The list of contained agents. */
	protected List agents;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application.
	 */
	public MApplicationInstance()
	{
		this.spaces = new ArrayList();
		this.agents = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add an space.
	 *  @param space The space.
	 */
	public void addMSpaceInstance(MSpaceInstance space)
	{
		this.spaces.add(space);
	}
	
	/**
	 *  Get all spaces.
	 *  @return The spaces.
	 */
	public List getMSpaceInstances()
	{
		return spaces;
	}
	
	/**
	 *  Add an agent.
	 *  @param agent The agent.
	 */
	public void addMAgentInstance(MAgentInstance agent)
	{
		this.agents.add(agent);
	}
	
	/**
	 *  Get all agents.
	 *  @return The agents.
	 */
	public List getMAgentInstances()
	{
		return agents;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
}
