package jadex.jade;

import jade.core.Agent;

/**
 *  Representing a component on the JADE platform.
 */
public class ComponentAgent extends Agent
{
	//-------- arttributes --------
	
	/** The component adapter. */
	protected JadeComponentAdapter	adapter;
	
	//-------- constructors --------
	
	/**
	 *  Initialize the agent.
	 */
	protected void setup()
	{
		adapter	= (JadeComponentAdapter)getArguments()[0];
		adapter.setJadeAgent(this);
	}
}
