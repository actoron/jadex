package jadex.jade;

import jade.core.Agent;

/**
 *  Representing a component on the JADE platform.
 */
public class ComponentAgent extends Agent
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected JadeComponentAdapter	adapter;
	
	/** The execution behavior. */
	protected ActionExecutionBehaviour	execution;
	
	//-------- constructors --------
	
	/**
	 *  Initialize the agent.
	 */
	protected void setup()
	{
		adapter	= (JadeComponentAdapter)getArguments()[0];
		adapter.setJadeAgent(this);
		
		execution	= new ActionExecutionBehaviour(adapter); 
		addBehaviour(execution);
	}
	
	//-------- methods --------
	
	/**
	 *  Resume agent execution.
	 */
	public void	wakeup()
	{
		execution.restart();
	}
}
