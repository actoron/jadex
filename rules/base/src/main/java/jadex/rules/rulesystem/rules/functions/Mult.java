package jadex.rules.rulesystem.rules.functions;

import java.util.Iterator;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.state.IOAVState;

/**
 *  Multiply two or more values.
 */
public class Mult implements IFunction
{
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		double ret = 0;
		if(paramvalues==null || paramvalues.length==0)
			throw new IllegalArgumentException("Function needs one parameter: "+SUtil.arrayToString(paramvalues));
			
		if(paramvalues.length==1)
		{
			Object val1 = paramvalues[0] instanceof ILazyValue? ((ILazyValue)paramvalues[0]).getValue(): paramvalues[0]; 

			Iterator it = SReflect.getIterator(val1);
			if(it==null)
				throw new IllegalArgumentException("Mult is undefined for null.");
			while(it.hasNext())
				ret *= ((Number)it.next()).doubleValue();
		}
		else if(paramvalues.length>1)
		{
			for(int i=0; i<paramvalues.length; i++)
			{
				Object val = paramvalues[i] instanceof ILazyValue? ((ILazyValue)paramvalues[i]).getValue(): paramvalues[i]; 
				ret *= ((Number)val).doubleValue();
			}
		}
		
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
		return "*";
	}
	
	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Mult;
	}
}
