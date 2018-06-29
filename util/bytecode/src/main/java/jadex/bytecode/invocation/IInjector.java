package jadex.bytecode.invocation;

/**
 *  Interface for generated bean injectors.
 *
 */
public interface IInjector
{
	/**
	 *  Injects properties into a bean.
	 *  
	 *  @param object The target bean object.
	 *  @param properties The bean properties, names followed by values,
	 *  				  size must be even.
	 */
	public void inject(Object object, Object... properties);
}
