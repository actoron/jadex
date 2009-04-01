package jadex.rules.rulesystem.rules.functions;

import jadex.commons.SReflect;
import jadex.rules.state.IOAVState;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *  Subtract value(s) from the first value.
 */
public class Sub implements IFunction
{
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @param state The state.
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
				throw new IllegalArgumentException("Sum is undefined for null.");
			boolean	first	= true;
			while(it.hasNext())
			{
				if(first)
					ret = ((Number)it.next()).doubleValue();
				else
					ret -= ((Number)it.next()).doubleValue();
				first	= false;
			}
		}
		else if(paramvalues.length>1)
		{
			for(int i=0; i<paramvalues.length; i++)
			{
				if(i==0)
					ret = ((Number)paramvalues[i]).doubleValue();
				else
					ret -= ((Number)paramvalues[i]).doubleValue();					
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
		return "-";
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Sub;
	}
}
