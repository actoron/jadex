package jadex.jade;

import jade.core.behaviours.CyclicBehaviour;

/**
 *  This behavior is responsible for executing the component.
 */
public class ActionExecutionBehaviour extends CyclicBehaviour
{
	//-------- attributes --------

	/** The component adapter. */
	protected JadeComponentAdapter adapter;

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
}
