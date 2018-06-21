package jadex.rules.rulesystem.rules;

import java.util.List;

/**
 *  A return value constraint assures that 
 *  (slot|var|<value> <op> f(var1, var2, ...))
 */
public abstract class ReturnValueConstraint implements IConstraint
{
	//-------- attributes --------
	
	/** The function call. */
	protected FunctionCall funcall;
	
	/** The operator. */
	protected IOperator operator;
	
	//-------- constructors --------
	
	/**
	 *  Create a new return value constraint.
	 */
	public ReturnValueConstraint(FunctionCall funcall, IOperator operator)
	{
		this.funcall = funcall;
		this.operator = operator;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the operator.
	 *  @return The operator.
	 */
	public IOperator getOperator()
	{
		return operator;
	}
	
	/**
	 *  Get the function call.
	 *  @return The function call.
	 */
	public FunctionCall getFunctionCall()
	{
		return funcall;
	}

	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		return funcall.getVariables();
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 * /
	public String toString()
	{
		return "("+attr+getOperator()+":("+funcall+"))";
	}*/
}
