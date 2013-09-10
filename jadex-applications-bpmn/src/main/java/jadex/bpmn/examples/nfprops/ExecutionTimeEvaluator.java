package jadex.bpmn.examples.nfprops;

import jadex.bridge.nonfunctional.search.BasicEvaluator;
import jadex.bridge.sensor.service.ExecutionTimeProperty;
import jadex.commons.MethodInfo;

/**
 *  Execution time evaluator.
 *  Re-scales execution times from [0,unlimited] to [1,0].
 */
public class ExecutionTimeEvaluator extends BasicEvaluator<Long>
{
	/**
	 *  Create a new evaluator.
	 */
	public ExecutionTimeEvaluator() throws Exception
	{
		super(ExecutionTimeProperty.NAME, new MethodInfo(IAService.class.getMethod("test", new Class[]{})));
	}
	
	/**
	 *  Calculate the value with 1 best and 0 worst.
	 *  Re-scales waiting times from [0,unlimited] to [1,0].
	 */
	public double calculateEvaluation(Long value)
	{
		double ret = 1; // try out new services
		if(value!=null)
		{
			// Using a scaled e^(-ax) function with a=0.01 or similar to stretch the lowering
			double a = 0.01;
			ret = Math.exp(-value*a);
		}
		return ret;
	}
}