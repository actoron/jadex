package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Application
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of contained agents. */
	protected List agents;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public Application()
	{
		this.agents = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public void addAgent(Agent agent)
	{
		this.agents.add(agent);
	}
	
	/**
	 * 
	 */
	public List getAgents()
	{
		return agents;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
}
