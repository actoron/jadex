package jadex.rules.rulesystem.rules.functions;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.state.IOAVState;

/**
 *  Wrapper that allows operators being used as functions.
 */
public class OperatorFunction implements IFunction
{
	//-------- attributes --------
	
	/** The operator. */
	protected IOperator operator;
	
	//-------- constructors --------
	
	/**
	 *  Create create new function.
	 */
	public OperatorFunction(IOperator operator)
	{
		this.operator = operator;
	}
	
	//-------- methods --------
	
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @param state The state.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		if(paramvalues==null || paramvalues.length!=2)
			throw new IllegalArgumentException("Operator function needs two parameters: "+SUtil.arrayToString(paramvalues));

		return Boolean.valueOf(operator.evaluate(state, paramvalues[0], paramvalues[1]));
	}
	
	/**
	 *  Get the return type of this function.
	 */
	public Class getReturnType()
	{
		return Boolean.class;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet getRelevantAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}
	
	/**
	 *  Get the operator.
	 *  @return The operator.
	 */
	public IOperator getOperator()
	{
		return operator;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return operator.toString();
	}
	
	/**
	 *  Test for equality.
	 *  @param obj The object.
	 *  @return True if operator.
	 */
	public boolean equals(Object obj)
	{
		return (obj instanceof OperatorFunction) 
			&& ((OperatorFunction)obj).getOperator().equals(operator);
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return 31 + operator.hashCode();
	}
}
