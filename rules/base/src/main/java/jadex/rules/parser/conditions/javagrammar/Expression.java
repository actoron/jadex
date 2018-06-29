package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;

/**
 *  Base class for all expressions.
 */
public abstract class Expression
{
	/**
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public abstract boolean	containsVariable(Variable var);
}
