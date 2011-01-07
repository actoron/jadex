package jadex.jade;

import jade.core.behaviours.SimpleBehaviour;

/**
 *  This behavior is responsible for executing the component.
 */
public class ActionExecutionBehaviour extends SimpleBehaviour
{
	//-------- attributes --------

	/** The component adapter. */
	protected JadeComponentAdapter adapter;
	
	/** Flag indicating that execution is cancelled (i.e. agent is cleaned up). */
	protected boolean cancelled;

	//-------- constructor --------

	/**
	 *  Create a new execution behavior.
	 *  @param agent The component adapter.
	 */
	public ActionExecutionBehaviour(JadeComponentAdapter adapter)
	{
		this.adapter = adapter;
	}

	//-------- methods --------

	/**
	 *  The behaviour implementation.
	 */
	public void action()
	{
		if(!adapter.execute())
			block();
	}
	
	/**
	 *  Contine execution unless cancelled.
	 */
	public boolean done()
	{
		return cancelled;
	}

	/**
	 *  Cancel agent execution.
	 */
	public void cancel()
	{
		cancelled	= true;
	}
}
