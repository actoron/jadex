package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Application
{
	//-------- attributes --------
	
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
}
