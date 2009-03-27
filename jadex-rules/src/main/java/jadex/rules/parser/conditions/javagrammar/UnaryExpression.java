package jadex.rules.parser.conditions.javagrammar;


/**
 *  An unary operation operates on one value.
 */
public class UnaryExpression	extends Expression
{
	//-------- attributes --------
	
	/** The value expression. */
	protected Expression	value;

	/** The operator. */
	protected Object	operator;

	//-------- constructors --------
	
	/**
	 *  Create a new unary operation.
	 */
	public UnaryExpression(Expression value, Object operator)
	{
		this.value	= value;
		this.operator	= operator;
	}
	
	//-------- methods --------
	
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
		ret.append(getValue().toString());
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
