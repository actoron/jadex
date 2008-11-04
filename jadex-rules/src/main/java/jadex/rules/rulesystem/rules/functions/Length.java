package jadex.rules.rulesystem.rules.functions;


import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 *  Length of a multislot.
 */
public class Length implements IFunction
{
	//-------- constants --------
	
	/** Static 0 integer. */
	public static final Integer ZERO = new Integer(0);
	
	//-------- methods --------

	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @param state The state.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
//		System.out.println("length of: "+SUtil.arrayToString(paramvalues));
		
		if(paramvalues==null || paramvalues.length!=1)
			throw new IllegalArgumentException("Function needs one parameter: "+paramvalues);

		Collection col = (Collection)paramvalues[0];
		return col==null? ZERO: new Integer(col.size());
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
		return "length";
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Length;
	}
}
