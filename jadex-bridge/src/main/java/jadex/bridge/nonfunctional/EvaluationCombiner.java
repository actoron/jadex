package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class EvaluationCombiner implements IServiceEvaluator
{
	/** The evaluators. */
	protected List<Tuple2<IServiceEvaluator, Double>> evaluators;
	
	/**
	 *  Creates the combiner.
	 */
	public EvaluationCombiner()
	{
		evaluators = new ArrayList<Tuple2<IServiceEvaluator,Double>>();
	}
	
	/**
	 *  Adds a new evaluator with a weight of 1.0.
	 *  
	 *  @param evaluator The new evaluator.
	 */
	public void addEvaluator(IServiceEvaluator evaluator)
	{
		addEvaluator(evaluator, 1.0);
	}
	
	/**
	 *  Adds a new evaluator.
	 *  
	 *  @param evaluator The new evaluator.
	 *  @param weight The weight of the evaluator relative to other evaluators.
	 */
	public void addEvaluator(IServiceEvaluator evaluator, double weight)
	{
		evaluators.add(new Tuple2<IServiceEvaluator, Double>(evaluator, weight));
	}
	
	/**
	 *  Removes an evaluator.
	 *  
	 *  @param evaluator The evaluator.
	 */
	public void removeEvaluator(IServiceEvaluator evaluator)
	{
		for (int i = 0; i < evaluators.size(); ++i)
		{
			if (evaluators.get(i).equals(evaluator))
			{
				evaluators.remove(i);
				break;
			}
		}
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
	public double evaluateService(IService service)
	{
		double totalweight = 0;
		double evaluation = 0;
		for (Tuple2<IServiceEvaluator, Double> eval : evaluators)
		{
			evaluation = Math.min(1.0, Math.max(0.0, eval.getFirstEntity().evaluateService(service) * eval.getSecondEntity()));
			totalweight += eval.getSecondEntity();
		}
		evaluation /= totalweight;
		
		return evaluation;
	}
}
