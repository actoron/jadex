package jadex.commons.future;

import java.util.Collection;

/**
 *  Interface that extends both, result listener and future command.
 */
public interface IIntermediateFutureCommandResultListener<E>
	extends IIntermediateResultListener<E>, IFutureCommandResultListener<Collection<E>>
{
}

