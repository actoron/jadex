package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVObjectType;


/**
 *  An unary operation operates on one value.
 */
public class CastExpression	extends Expression
{
	//-------- attributes --------
	
	/** The cast type. */
	protected OAVObjectType	type;

	/** The value expression. */
	protected Expression	value;

	//-------- constructors --------
	
	/**
	 *  Create a new unary operation.
	 */
	public CastExpression(OAVObjectType	type, Expression value)
	{
		this.type	= type;
		this.value	= value;
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
	 *  Get the type.
	 */
	public OAVObjectType	getType()
	{
		return this.type;
	}
	
	/**
	 *  Get a string representation of this expression.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append("((");
		ret.append(getType());
		ret.append(")");
		ret.append(getValue());
		ret.append(")");
		return ret.toString();
	}

	/**
	 *  Test if this expression is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof CastExpression
		&& ((CastExpression)o).getType().equals(getType())
			&& ((CastExpression)o).getValue().equals(getValue());
	}
	
	/**
	 *  Get the hash code of this expression.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getType().hashCode();
		ret	= 31*ret + getValue().hashCode();
		return ret;
	}
}
