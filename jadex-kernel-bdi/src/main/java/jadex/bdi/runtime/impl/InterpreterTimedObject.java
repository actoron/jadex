package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.CheckedAction;
import jadex.rules.state.IOAVState;
import jadex.service.clock.ITimedObject;

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
	protected CheckedAction action;
		
	//-------- constructors --------
	
	/**
	 *  Create a new timed object.
	 */
	public InterpreterTimedObject(IOAVState state, CheckedAction runnable)
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
	public void timeEventOccurred(long currenttime)
	{
		BDIInterpreter	interpreter	= BDIInterpreter.getInterpreter(state);
		if(interpreter!=null)
		{
			try
			{
				interpreter.getComponentAdapter().invokeLater(action);
			}
			catch(ComponentTerminatedException e)
			{
			}
		}
		// else agent was terminated
	}
	
	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public CheckedAction getAction()
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
			name	= bdii.getComponentAdapter().getComponentIdentifier().getLocalName();
		return name + ": " + action; 
	}
}