package jadex.bridge.sensor.service;

import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.search.BasicEvaluator;
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
	public WaitqueueEvaluator(IExternalAccess component, MethodInfo mi) throws Exception
	{
		this(component, mi, 10);
	}
	
	/**
	 *  Create a new evaluator.
	 */
	public WaitqueueEvaluator(IExternalAccess component, MethodInfo mi, int max) throws Exception
	{
		super(component, WaitqueueProperty.NAME, mi);
		this.max = max;
	}
	
	/**
	 *  Calculate the value with 1 best and 0 worst.
	 *  Re-scales waiting times from [0,unlimited] to [1,0].
	 *  Uses linear scale with 0=1, 1=0.9, 10=0, 11=0
	 */
	public double calculateEvaluation(Integer value)
	{
		double ret = 1; // try out new services by ranking them first time with 1
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