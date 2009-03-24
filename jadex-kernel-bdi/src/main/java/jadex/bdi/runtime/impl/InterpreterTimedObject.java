package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bridge.AgentTerminatedException;
import jadex.bridge.ITimedObject;
import jadex.rules.state.IOAVState;

/**
 *  This timed object ensures that timed objects are executed
 *  correctly within the interpreter.
 */
public class InterpreterTimedObject implements ITimedObject
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The runnable. */
	protected InterpreterTimedObjectAction action;
		
	//-------- constructors --------
	
	/**
	 *  Create a new timed object.
	 */
	public InterpreterTimedObject(IOAVState state, InterpreterTimedObjectAction runnable)
	{
		this.state = state;
		this.action = runnable;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the submitted timepoint was reached.
	 *  // todo: will be enhanced with a TimerEvent when
	 *  // we enhance the time service 
	 */
	public void timeEventOccurred()
	{
		BDIInterpreter	interpreter	= BDIInterpreter.getInterpreter(state);
		if(interpreter!=null)
		{
			try
			{
				interpreter.invokeLater(action);
			}
			catch(AgentTerminatedException e)
			{
			}
		}
		// else agent was terminated
	}
	
	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public InterpreterTimedObjectAction getAction()
	{
		return action;
	}

	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		String	name	= null;
		BDIInterpreter	bdii	= BDIInterpreter.getInterpreter(state);
		if(bdii!=null)
			name	= bdii.getAgentAdapter().getAgentIdentifier().getLocalName();
		return name + ": " + action; 
	}
}