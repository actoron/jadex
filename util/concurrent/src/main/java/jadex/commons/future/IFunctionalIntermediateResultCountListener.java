package jadex.commons.future;

/**
 *  Listener for the number of results.
 */
// @Reference
// @FunctionalInterface // this is a functional interface in java 8
public interface IFunctionalIntermediateResultCountListener
{
	/**
	 *  Declare that the future result count is available.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method will be called as
	 *  often as the result count indicates.
	 */
	public void maxResultCountAvailable(int max);
}
