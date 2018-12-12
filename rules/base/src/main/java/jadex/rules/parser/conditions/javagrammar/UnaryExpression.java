package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;


/**
 *  An unary operation operates on one value.
 */
public class UnaryExpression	extends Expression
{
	//-------- constants --------

	/** The not operator "!". */
	public static final String	OPERATOR_NOT	= "!";
	
	/** The (unary) minus operator "-". */
	// unary plus is ignored by parser.
	public static final String	OPERATOR_MINUS	= "-";
	
	/** The bitwise not operator "~". */
	public static final String	OPERATOR_BNOT	= "~";
	
	//-------- attributes --------
	
	/** The value expression. */
	protected Expression	value;

	/** The operator. */
	protected String	operator;

	//-------- constructors --------
	
	/**
	 *  Create a new unary operation.
	 */
	public UnaryExpression(Expression value, String operator)
	{
		this.value	= value;
		this.operator	= operator;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return value.containsVariable(var);
	}
	
	/**
	 *  Get the value.
	 */
	public Expression	getValue()
	{
		return this.value;
	}
	
	/**
	 *  Get the operator.
	 */
	public Object	getOperator()
	{
		return this.operator;
	}
	
	/**
	 *  Get a string representation of this expression.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(getOperator());
		ret.append("(");
		ret.append(getValue().toString());
		ret.append(")");
		return ret.toString();
	}

	/**
	 *  Test if this expression is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof UnaryExpression
			&& ((UnaryExpression)o).getValue().equals(getValue())
			&& ((UnaryExpression)o).getOperator().equals(getOperator());
	}
	
	/**
	 *  Get the hash code of this expression.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getValue().hashCode();
		ret	= 31*ret + getOperator().hashCode();
		return ret;
	}
}
