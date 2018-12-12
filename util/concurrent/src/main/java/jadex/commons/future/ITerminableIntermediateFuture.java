package jadex.commons.future;

import java.util.Collection;

/**
 *  Interface for an intermediate future that can be terminated
 *  from caller side. A termination request leads
 *  to setException() being called with a FutureTerminatedException.
 */
public interface ITerminableIntermediateFuture<E> extends IIntermediateFuture<E>, ITerminableFuture<Collection<E>>
{
}
