package jadex.commons.future;

/**
 *  Callback interface for methods that should operate decoupled from caller thread. 
 */
// @Reference
public interface IResultListener<E> extends IFunctionalResultListener<E>, IFunctionalExceptionListener
{
}
