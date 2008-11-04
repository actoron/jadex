package jadex.rules.rulesystem.rules.functions;

import jadex.rules.state.IOAVState;

import java.util.Collections;
import java.util.Set;

/**
 *  Return the given value.
 */
// Hack!!! Required to add new values into conditions.
public class Identity implements IFunction
{
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		if(paramvalues==null || paramvalues.length!=1)
			throw new IllegalArgumentException("Function needs one parameter: "+paramvalues);
			
		return paramvalues[0];
	}
	
	/**
	 *  Get the return type of this function.
	 */
	public Class getReturnType()
	{
		return Object.class;
	}

	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public Set	getRelevantAttributes()
	{
		return Collections.EMPTY_SET;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return "identity";
	}
	
	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Identity;
	}
}
