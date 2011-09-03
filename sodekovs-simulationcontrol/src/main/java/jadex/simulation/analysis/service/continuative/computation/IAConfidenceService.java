package jadex.simulation.analysis.service.continuative.computation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAMultiValueParameter;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

/**
 * Service to compute a confidence interval
 * 
 * @author 5Haubeck
 */
public interface IAConfidenceService extends IAnalysisSessionService
{
	/**
	 * Compute a confidence interval for given parameters and probability
	 * 
	 * @param parameter
	 *            parameters to test with
	 * @param probability
	 *            probability to test with
	 * @return probability value of confidence interval
	 */
	public IFuture computeTTest(IAMultiValueParameter parameter, Double probability);

	/**
	 * Tests, if alpha <= probability value of confidence interval (computeTTest())
	 * 
	 * @param parameters
	 *            parameters to test with
	 * @param alpha
	 *            target probability
	 * @param probability
	 *            probability to test with
	 * @return probability value of confidence interval
	 */
	public IFuture testTTest(IAMultiValueParameter parameters, Double alpha, Double probability);
}
