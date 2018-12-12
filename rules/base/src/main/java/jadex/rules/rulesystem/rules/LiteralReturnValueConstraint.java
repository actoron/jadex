package jadex.rules.rulesystem.rules;

/**
 *  Test if the function result equals a value.
 *  (<value> <op> f(var1, var2, ...))
 */
public class LiteralReturnValueConstraint extends ReturnValueConstraint
{
	//-------- attributes --------
	
	/** The attribute type. */
	protected Object value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new return value constraint.
	 */
	public LiteralReturnValueConstraint(Object value, FunctionCall funcall)
	{
		this(value, funcall, IOperator.EQUAL);
	}
	
	/**
	 *  Create a new return value constraint.
	 */
	public LiteralReturnValueConstraint(Object value, FunctionCall funcall, IOperator operator)
	{
		super(funcall, operator);
		this.value = value;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return value;
	}
}

