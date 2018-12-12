package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.List;

/**
 *  Test if the function result equals a variable.
 *  (var <op> f(var1, var2, ...))
 */
public class VariableReturnValueConstraint extends ReturnValueConstraint
{
	//-------- attributes --------
	
	/** The variable. */
	protected Variable var;
	
	//-------- constructors --------
	
	/**
	 *  Create a new return value constraint.
	 */
	public VariableReturnValueConstraint(Variable var, FunctionCall funcall)
	{
		this(var, funcall, IOperator.EQUAL);
	}
	
	/**
	 *  Create a new return value constraint.
	 */
	public VariableReturnValueConstraint(Variable var, FunctionCall funcall, IOperator operator)
	{
		super(funcall, operator);
		this.var = var;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Variable getVariable()
	{
		return var;
	}
	
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		List ret = new ArrayList();
		ret.addAll(super.getVariables());
		ret.add(var);
		return ret;
	}
}

