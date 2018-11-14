package jadex.commons.future;

/**
 *  Callback interface for methods that should operate decoupled from caller thread. 
 */
// @Reference
// @FunctionalInterface // this is a functional interface in java 8
public interface IOnIntermediateResultListener<E>
{
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(E result);
	
}
