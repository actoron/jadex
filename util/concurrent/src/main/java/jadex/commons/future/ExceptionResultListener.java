package jadex.commons.future;

/**
 *  The exception listener is used for intercepting exceptions while ignoring results.
 *  This is useful for "fire-and-forget" method that still occasionally return exceptions.
 */
public abstract class ExceptionResultListener<E> implements IResultListener<E>
{
	/**
	 *  Called when the result is available, ignore.
	 *  @param result The result.
	 */
	public void resultAvailable(E result)
	{
	};
}
