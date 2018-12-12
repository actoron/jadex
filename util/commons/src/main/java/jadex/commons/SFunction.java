package jadex.commons;

import java.util.Iterator;

/**
 *  Static helper class for calculating statistical functions.
 */
public class SFunction
{
	//-------- methods --------
	
	/**
	 *  Calculate the mean value.
	 *  @param numbers The numbers (as some form of iterable element).
	 */
	public static double mean(Object numbers)
	{
		double ret = 0;
		
		if(numbers!=null)
		{
			int cnt = 0;
			for(Iterator it=SReflect.getIterator(numbers); it.hasNext(); )
			{
				ret += ((Number)it.next()).doubleValue();
				cnt++;
			}
			
			ret = ret / cnt;
		}
		
		return ret;
	}
	
	/**
	 *  Calculate the sum of values.
	 *  @param numbers The numbers (as some form of iterable element).
	 */
	public static double sum(Object numbers)
	{
		double ret = 0;
		
		if(numbers!=null)
		{
			for(Iterator it=SReflect.getIterator(numbers); it.hasNext(); )
			{
				ret += ((Number)it.next()).doubleValue();
			}
		}
		
		return ret;
	}
}
