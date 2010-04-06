package jadex.adapter.jade;

import jade.core.behaviours.CyclicBehaviour;

/**
 *  This behaviour is responsible for selecting an agenda
 *  entry and executing its action.
 */
public class ActionExecutionBehaviour extends CyclicBehaviour
{
	//-------- attributes --------

	/** The agent adapter. */
	protected JadeAgentAdapter adapter;

	//-------- constructor --------

	/**
	 *  Create a new agenda control behaviour.
	 *  @param agent The jadex agent.
	 */
//	public ActionExecutionBehaviour(IComponentInstance agent)
	public ActionExecutionBehaviour(JadeAgentAdapter adapter)
	{
		this.adapter = adapter;
	}

	//-------- methods --------

	/**
	 *  The behaviour implementation.
	 */
	public void action()
	{
//		if(!agent.executeStep())
		if(!adapter.execute())
			block();
	}
}
