package jadex.rules.parser.conditions.javagrammar;

/**
 *  A constraint compares two values.
 */
public class Constraint
{
	//-------- attributes --------
	
	/** The left value. */
	protected UnaryExpression	left;

	/** The right value. */
	protected UnaryExpression	right;
	
	/** The operator. */
	protected String	operator;

	//-------- constructors --------
	
	/**
	 *  Create a new constraint.
	 */
	public Constraint(UnaryExpression left, UnaryExpression right, String operator)
	{
		this.left	= left;
		this.right	= right;
		this.operator	= operator;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the left value.
	 */
	public UnaryExpression	getLeftValue()
	{
		return this.left;
	}
	
	/**
	 *  Get the right value.
	 */
	public UnaryExpression	getRightValue()
	{
		return this.right;
	}
	
	/**
	 *  Get the operator.
	 */
	public String	getOperator()
	{
		return this.operator;
	}
	
	/**
	 *  Get a string representation of this constraint.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(left.toString());
		ret.append(" ");
		ret.append(operator);
		ret.append(" ");
		ret.append(right.toString());
		return ret.toString();
	}

	/**
	 *  Test if this constraint is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof Constraint
			&& ((Constraint)o).getLeftValue().equals(getLeftValue())
			&& ((Constraint)o).getRightValue().equals(getRightValue())
			&& ((Constraint)o).getOperator().equals(getOperator());
	}
	
	/**
	 *  Get the hash code of this field access.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getLeftValue().hashCode();
		ret	= 31*ret + getRightValue().hashCode();
		ret	= 31*ret + getOperator().hashCode();
		return ret;
	}
}
