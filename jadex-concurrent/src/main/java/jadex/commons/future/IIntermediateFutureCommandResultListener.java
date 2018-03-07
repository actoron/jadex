package jadex.commons.future;

/**
 *  Interface that extends both, result listener and future command.
 */
public interface IIntermediateFutureCommandResultListener<E> extends IIntermediateResultListener<E>, IFutureCommandListener
{
}

