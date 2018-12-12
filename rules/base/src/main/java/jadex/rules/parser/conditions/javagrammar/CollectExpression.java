package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;


/**
 *  A collect expression collects values.
 */
public class CollectExpression	extends Expression
{
	//-------- attributes --------
	
	/** The collect variable. * /
	protected Variable	variable;*/ 

	/** The collect expression. */
	protected Expression	expression;

	//-------- constructors --------
	
	/**
	 *  Create a new unary operation.
	 */
	public CollectExpression(/*Variable var, */Expression expression)
	{
//		this.variable	= var;
		this.expression	= expression;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return expression.containsVariable(var);
	}
	
	/**
	 *  Get the expression.
	 */
	public Expression	getExpression()
	{
		return this.expression;
	}
	
//	/**
//	 *  Get the type.
//	 */
//	public Variable	getVariable()
//	{
//		return this.variable;
//	}
	
	/**
	 *  Get a string representation of this expression.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append("collect(");
//		ret.append(getVariable());
//		ret.append(", ");
		ret.append(getExpression());
		ret.append(")");
		return ret.toString();
	}

	/**
	 *  Test if this expression is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof CollectExpression
//		&& ((CollectExpression)o).getVariable().equals(getVariable())
			&& ((CollectExpression)o).getExpression().equals(getExpression());
	}
	
	/**
	 *  Get the hash code of this expression.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getExpression().hashCode();
//		ret	= 31*ret + getVariable().hashCode();
		return ret;
	}
}
