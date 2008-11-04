package jadex.rules.rulesystem;

import jadex.rules.state.IOAVState;


/**
 *  Action to be called when a rule triggers.
 */
public interface IAction
{
	/**
	 *  Execute the action on the given state using the given variable
	 *  assignments.
	 */
	public void	execute(IOAVState state, IVariableAssignments assignments);
}