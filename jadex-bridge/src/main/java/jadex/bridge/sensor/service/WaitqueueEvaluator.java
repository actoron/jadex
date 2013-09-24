package jadex.bridge.sensor.service;

import jadex.bridge.nonfunctional.search.BasicEvaluator;
import jadex.bridge.sensor.service.WaitqueueProperty;
import jadex.commons.MethodInfo;

/**
 *  Execution time evaluator.
 *  Re-scales execution times from [0,unlimited] to [1,0].
 */
public class WaitqueueEvaluator extends BasicEvaluator<Integer>
{
	/** The wait queue cutoff len. */
	protected int max;
	
	/**
	 *  Create a new evaluator.
	 */
	public WaitqueueEvaluator(MethodInfo mi) throws Exception
	{
		this(mi, 10);
	}
	
	/**
	 *  Create a new evaluator.
	 */
	public WaitqueueEvaluator(MethodInfo mi, int max) throws Exception
	{
		super(WaitqueueProperty.NAME, mi);
		this.max = max;
	}
	
	/**
	 *  Calculate the value with 1 best and 0 worst.
	 *  Re-scales waiting times from [0,unlimited] to [1,0].
	 *  Uses linear scale with 0=1, 1=0.9, 10=0, 11=0
	 */
	public double calculateEvaluation(Integer value)
	{
		double ret = 1; // try out new services
		if(value!=null)
		{
			int val = value.intValue();
			if(val<max)
			{
				ret = -0.1*val+1;
			}
			else
			{
				ret = 0;
			}
		}
		return ret;
	}
}