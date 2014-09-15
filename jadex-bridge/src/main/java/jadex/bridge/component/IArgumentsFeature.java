package jadex.bridge.component;

import jadex.commons.Tuple2;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.Map;

/**
 *  This features provides arguments.
 */
public interface IArgumentsFeature
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
	
	/**
	 * Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults();
}
