package jadex.commons.future;

import java.util.Collection;

/**
 *  The subscription intermediate future does not save the results but
 *  instead uses a fire and forget semantics.
 *  
 *  A listener gets current intermediate results in intermediateResultAvailable().
 *  In result available null is returned.
 *  In getIntermediateResults() null is returned.
 */
public interface ISubscriptionIntermediateFuture<E> extends ITerminableIntermediateFuture<E>
{
	/**
	 *  Add a listener which does not consume the initial results.
	 *  I.e. even if it is the first listener to be added to this future,
	 *  a second listener will still receive the initial results.
	 */
	public void	addQuietListener(IResultListener<Collection<E>> listener);
}
