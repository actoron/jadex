package jadex.commons.future;

/**
 *  Interface that extends both, result listener and future command.
 */
public interface IFutureCommandResultListener<E> extends IResultListener<E>, IFutureCommandListener
{
}
