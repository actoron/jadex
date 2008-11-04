package jadex.rules.rulesystem.rules.functions;

import jadex.rules.state.IOAVState;

import java.util.Collections;
import java.util.Set;

/**
 *  Divide two values.
 */
public class Div implements IFunction
{
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		double ret = 0;
		if(paramvalues==null || paramvalues.length!=2)
			throw new IllegalArgumentException("Function needs two parameters: "+paramvalues);
			
		Number num1 = (Number)paramvalues[0];
		Number num2 = (Number)paramvalues[1];
		
		ret = num1.doubleValue()/num2.doubleValue();
		
		return new Double(ret);
	}
	
	/**
	 *  Get the return type of this function.
	 */
	public Class getReturnType()
	{
		return Number.class;
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
		return "div";
	}
	
	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Div;
	}
}
