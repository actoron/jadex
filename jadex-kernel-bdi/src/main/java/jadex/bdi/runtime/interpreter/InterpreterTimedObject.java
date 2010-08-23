package jadex.bdi.runtime.interpreter;

import jadex.bridge.CheckedAction;
import jadex.commons.service.clock.ITimedObject;

/**
 *  This timed object ensures that timed objects are executed
 *  correctly within the interpreter.
 */
public class InterpreterTimedObject implements ITimedObject
{
	//-------- attributes --------
		
	/** The component instance. */
	protected BDIInterpreter interpreter;
	
	/** The runnable. */
	protected CheckedAction action;
	
	//-------- constructors --------
	
	/**
	 *  Create a new timed object.
	 */
	public InterpreterTimedObject(BDIInterpreter interpreter, CheckedAction action)
	{
		this.interpreter = interpreter;
		this.action = action;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the submitted timepoint was reached.
	 *  // todo: will be enhanced with a TimerEvent when
	 *  // we enhance the time service 
	 */
	public void timeEventOccurred(long currenttime)
	{
		interpreter.scheduleStep(action);
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
		return interpreter.getAgentAdapter().getComponentIdentifier().getLocalName() + ": " + action; 
	}
}