package jadex.rules.rulesystem;

import jadex.commons.ICommand;

/**
 *  Interface for components that can be executed in stepmode.
 */
public interface ISteppable
{
	/**
	 *  Execute a step.
	 */
	public void doStep();
	
	/**
	 *  Set the stepmode.
	 *  @param stepmode True for stepmode.
	 */
	public void setStepmode(boolean stepmode);
	
	/**
	 *  Test if in stepmode.
	 *  @return True, if in stepmode.
	 */
	public boolean isStepmode();


	/**
	 *  Add a breakpoint to the interpreter.
	 */
	public void	addBreakpoint(IRule rule);
	
	/**
	 *  Remove a breakpoint from the interpreter.
	 */
	public void	removeBreakpoint(IRule rule);
	
	/**
	 *  Check if a rule is a breakpoint for the interpreter.
	 */
	public boolean	isBreakpoint(IRule rule);
	
	/**
	 *  Add a command to be executed, when a breakpoint is reached.
	 */
	public void	addBreakpointCommand(ICommand command);
}
