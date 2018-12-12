package jadex.bridge.sensor.service;

import jadex.bridge.nonfunctional.search.IServiceEvaluator;
import jadex.bridge.service.IService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class AverageEvaluator implements IServiceEvaluator
{
	/** The contained evaluator. */
	protected IServiceEvaluator evaluator;
	
	/** The last average value. */
	protected Double value;
	
	/** The avg period. */
	protected int period;
	
	/**
	 *  Create a new AverageEvaluator. 
	 */
	public AverageEvaluator(IServiceEvaluator evaluator)
	{
		this(evaluator, 10); // 10 periods per default
	}
	
	/**
	 *  Create a new AverageEvaluator. 
	 */
	public AverageEvaluator(IServiceEvaluator evaluator, int period)
	{
		this.evaluator = evaluator;
		this.period = period;
	}

	/**
	 *  Evaluates the service in detail. This method
	 *  must return an evaluation of the service in
	 *  the range between 0 (worst/unacceptable) to
	 *  1 (best/preferred).
	 * 
	 *  @param service The service being evaluated.
	 * 
	 *  @return An evaluation of the service in a
	 *  		 range between 0 and 1 (inclusive).
	 */
	public IFuture<Double> evaluate(IService service)
	{
		final Future<Double> ret = new Future<Double>();
		
		// ema calculation: EMAt = EMAt-1 +(SF*(Ct-EMAt-1)) SF=2/(n+1)
		evaluator.evaluate(service).addResultListener(new DelegationResultListener<Double>(ret)
		{
			public void customResultAvailable(Double value)
			{
				if(AverageEvaluator.this.value!=null)
				{
					double sf = 2d/(period+1); 
					double delta = value-AverageEvaluator.this.value;
					AverageEvaluator.this.value = Double.valueOf((Double)(AverageEvaluator.this.value+sf*delta));
				}
				else
				{
					AverageEvaluator.this.value = value;
				}
				ret.setResult(AverageEvaluator.this.value);
			}
		});
		
		return ret;
	}
}
