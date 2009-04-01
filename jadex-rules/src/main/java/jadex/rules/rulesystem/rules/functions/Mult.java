package jadex.rules.rulesystem.rules.functions;

import jadex.commons.SReflect;
import jadex.rules.state.IOAVState;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

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
			throw new IllegalArgumentException("Function needs one parameter: "+paramvalues);
			
		if(paramvalues.length==1)
		{
			Iterator it = SReflect.getIterator(paramvalues[0]);
			if(it==null)
				throw new IllegalArgumentException("Mult is undefined for null.");
			while(it.hasNext())
				ret *= ((Number)it.next()).doubleValue();
		}
		else if(paramvalues.length>1)
		{
			for(int i=0; i<paramvalues.length; i++)
			{
				ret *= ((Number)paramvalues[i]).doubleValue();
			}
		}
		
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
