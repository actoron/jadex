package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;


/**
 *  An expression consisting of a variable value.
 */
public class VariableExpression	extends Expression
{
	//-------- attributes --------
	
	/** The variable. */
	protected Variable	variable;
	
	//-------- constructors --------
	
	/**
	 *  Create a new variable expression.
	 */
	public VariableExpression(Variable value)
	{
		this.variable	= value;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return var.equals(variable);
	}
	
	/**
	 *  Get the variable.
	 */
	public Variable	getVariable()
	{
		return this.variable;
	}
	
	/**
	 *  Get a string representation of this variable.
	 */
	public String	toString()
	{
		return getVariable().toString();
	}

	/**
	 *  Test if this variable is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof VariableExpression
			&& ((VariableExpression)o).getVariable().equals(getVariable());
	}
	
	/**
	 *  Get the hash code of this variable.
	 */
	public int	hashCode()
	{
		return 31 + getVariable().hashCode();
	}
}
