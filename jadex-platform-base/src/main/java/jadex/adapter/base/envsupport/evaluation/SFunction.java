package jadex.adapter.base.envsupport.evaluation;

import java.util.List;

/**
 * 
 */
public class SFunction
{
	/**
	 *  Calculate the mean value.
	 */
	public static double mean(List numbers)
	{
		double ret = 0;
		
		if(numbers!=null)
		{
			for(int i=0; i<numbers.size(); i++)
			{
				ret += ((Number)numbers.get(i)).doubleValue();
			}
			
			ret = ret / numbers.size();
		}
		
		return ret;
	}
}
