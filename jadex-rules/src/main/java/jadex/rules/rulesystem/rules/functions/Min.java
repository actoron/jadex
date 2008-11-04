package jadex.rules.rulesystem.rules.functions;

import jadex.rules.state.IOAVState;

import java.util.Collections;
import java.util.Set;

/**
 *  Find the lowest value of the arguments.
 */
public class Min implements IFunction
{
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @param state The state.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		Comparable ret = null;
		if(paramvalues.length > 0)
		{
			ret = (Comparable)paramvalues[0];
		
			for(int i=1; i<paramvalues.length; i++)
			{
				Comparable tmp = (Comparable)paramvalues[i];
				if(tmp.compareTo(ret)<0)
					ret = tmp;
			}
		}
		return ret;
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
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Min;
	}
}
