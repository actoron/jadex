package jadex.rules.rulesystem.rules;

import java.util.List;

/**
 * PredicateConstraint assures that (true == p(var1, var2, ...)) 
 */
public class PredicateConstraint implements IConstraint
{
	//-------- attributes --------
	
	/** The function call. */
	protected FunctionCall funcall;
	
	//-------- constructors --------
	
	/**
	 *  Create a new predicate constraint.
	 */
	public PredicateConstraint(FunctionCall funcall)
	{
		this.funcall = funcall;
	}
	
	//-------- methods --------
	
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
	 */
	public String toString()
	{
		return "("+funcall+")";
	}
}
