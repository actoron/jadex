package jadex.commons.future;

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
}
