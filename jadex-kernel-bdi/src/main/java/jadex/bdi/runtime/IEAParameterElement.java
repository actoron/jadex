package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 * 
 */
public interface IEAParameterElement extends IEAElement
{
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IFuture	getParameters();

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IFuture	getParameterSets();

	/**
	 *  Get the parameter element.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IFuture getParameter(String name);

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IFuture getParameterSet(String name);

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public IFuture hasParameter(String name);

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public IFuture hasParameterSet(String name);

	/**
	 *  Get the element type (i.e. the name declared in the ADF).
	 *  @return The element type.
	 */
	public IFuture getType();
}
