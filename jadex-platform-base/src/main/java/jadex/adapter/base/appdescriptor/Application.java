package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 *  Application instance representation.
 */
public class Application
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of structurings. */
	protected List structurings;
	
	/** The list of contained agents. */
	protected List agents;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application.
	 */
	public Application()
	{
		this.structurings = new ArrayList();
		this.agents = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add an structuring.
	 *  @param structuring The structuring.
	 */
	public void addstructuring(Structuring structuring)
	{
		this.structurings.add(structuring);
	}
	
	/**
	 *  Get all structurings.
	 *  @return The structurings.
	 */
	public List getStructurings()
	{
		return structurings;
	}
	
	/**
	 *  Add an agent.
	 *  @param agent The agent.
	 */
	public void addAgent(Agent agent)
	{
		this.agents.add(agent);
	}
	
	/**
	 *  Get all agents.
	 *  @return The agents.
	 */
	public List getAgents()
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
