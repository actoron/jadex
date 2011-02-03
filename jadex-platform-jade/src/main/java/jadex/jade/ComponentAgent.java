package jadex.jade;

import jade.core.Agent;
import jadex.jade.service.message.MessageReceiverBehaviour;

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
		
		execution	= new ActionExecutionBehaviour(adapter); 
		addBehaviour(execution);
		execution.block();	// Do not execute before first wakeup.

		adapter.setJadeAgent(this);

		addBehaviour(new MessageReceiverBehaviour(adapter));
	}
	
	//-------- methods --------
	
	/**
	 *  Resume agent execution.
	 */
	public void	wakeup()
	{
		if(execution!=null)
			execution.restart();
	}

	/**
	 *  Cancel agent execution.
	 */
	public void cancel()
	{
		execution.cancel();
	}
}
