package jadex.rules.rulesystem.rules.functions;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.state.IOAVState;

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
			throw new IllegalArgumentException("Function needs two parameters: "+SUtil.arrayToString(paramvalues));
		
		Object val1 = paramvalues[0] instanceof ILazyValue? ((ILazyValue)paramvalues[0]).getValue(): paramvalues[0]; 
		Object val2 = paramvalues[1] instanceof ILazyValue? ((ILazyValue)paramvalues[1]).getValue(): paramvalues[1]; 

		Number num1 = (Number)val1;
		Number num2 = (Number)val2;
		
		ret = num1.doubleValue()/num2.doubleValue();
		
		return Double.valueOf(ret);
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
	public AttributeSet getRelevantAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return "/";
	}
	
	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Div;
	}
}
