package jadex.commons.future;

/**
 *  Callback interface for methods that should operate decoupled from caller thread. 
 */
// @Reference
// @FunctionalInterface // this is a functional interface in java 8
public interface IFunctionalIntermediateFinishedListener<E>
{
	/**
	 *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
	 */
	public void finished();
	
}
