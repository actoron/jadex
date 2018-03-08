package jadex.commons.transformation.traverser;

/**
 *  Provider for supplying Bean Accessor Delegates.
 *
 */
public interface IBeanDelegateProvider
{
	/**
	 *  Returns an accessor delegate.
	 */
	public IBeanAccessorDelegate getDelegate(Class<?> clazz);
}
