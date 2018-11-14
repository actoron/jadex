package jadex.commons.future;

/**
 *  Callback interface for methods that should operate decoupled from caller thread. 
 */
// @Reference
// @FunctionalInterface // this is a functional interface in java 8
public interface IFunctionalResultListener<E>
{
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(E result);
	
}
