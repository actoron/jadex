package jadex.bridge.component;

import java.util.Map;

/**
 *  This features provides arguments.
 */
public interface IArgumentsResultsFeature extends IExternalArgumentsResultsFeature
{
	/**
	 *  Get the arguments.
	 *  @return The arguments (if any).
	 */
	public Map<String, Object> getArguments();
	
	/**
	 *  Get the current results.
	 *  @return The current result values (if any).
	 */
	public Map<String, Object> getResults();
	
	// todo: allow for setResult() or must use a monitored map to be able to send out intermediate results
	
//	/**
//	 * Subscribe to receive results.
//	 */
//	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults();
}
