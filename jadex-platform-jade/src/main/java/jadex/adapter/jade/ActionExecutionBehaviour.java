package jadex.adapter.jade;

import jade.core.behaviours.CyclicBehaviour;
import jadex.bridge.IKernelAgent;

/**
 *  This behaviour is responsible for selecting an agenda
 *  entry and executing its action.
 */
public class ActionExecutionBehaviour extends CyclicBehaviour
{
	//-------- attributes --------

	/** The jadex agent. */
	protected IKernelAgent agent;

	//-------- constructor --------

	/**
	 *  Create a new agenda control behaviour.
	 *  @param agent The jadex agent.
	 */
	public ActionExecutionBehaviour(IKernelAgent agent)
	{
		this.agent	= agent;
	}

	//-------- methods --------

	/**
	 *  The behaviour implementation.
	 */
	public void action()
	{
		if(!agent.executeAction())
			block();
	}
}
