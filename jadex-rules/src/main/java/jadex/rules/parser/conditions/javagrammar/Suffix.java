package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;

/**
 *  Suffix to a value (e.g. method or field access).
 */
public abstract class Suffix
{
	/**
	 *  Test if a variable is contained in the suffix.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public abstract boolean	containsVariable(Variable var);
}
