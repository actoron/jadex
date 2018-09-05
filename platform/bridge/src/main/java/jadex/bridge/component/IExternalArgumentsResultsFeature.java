package jadex.bridge.component;

import java.util.Map;

import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * 
 */
public interface IExternalArgumentsResultsFeature extends IExternalComponentFeature
{
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public IFuture<Map<String, Object>> getResultsAsync();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IFuture<Map<String, Object>> getArgumentsAsync();
	
	/**
	 * Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults();

}
