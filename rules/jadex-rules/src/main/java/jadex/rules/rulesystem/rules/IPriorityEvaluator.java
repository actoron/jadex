package jadex.rules.rulesystem.rules;

import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.state.IOAVState;

/**
 *  Has the purpose to evaluate the priority of a rule.
 *  An evaluator can be either static (returning a constant value)
 *  or dynamic (calculating the value new for each activation).
 */
public interface IPriorityEvaluator
{
	//-------- constants --------

	public static IPriorityEvaluator PRIORITY_1 = new IPriorityEvaluator()
	{
		public int getPriority(IOAVState state, IVariableAssignments assignments)
		{
			return 1;
		}
	};
	
	public static IPriorityEvaluator PRIORITY_2 = new IPriorityEvaluator()
	{
		public int getPriority(IOAVState state, IVariableAssignments assignments)
		{
			return 2;
		}
	};

	//-------- methods --------
	
	/**
	 *  Execute the action on the given state using the given variable
	 *  assignments.
	 */
	public int	getPriority(IOAVState state, IVariableAssignments assignments);
}
