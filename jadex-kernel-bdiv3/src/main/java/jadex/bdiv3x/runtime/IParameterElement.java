package jadex.bdiv3x.runtime;

/**
 *  The internal interface for all elements with parameters.
 */
public interface IParameterElement extends IElement
{
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[]	getParameters();

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[]	getParameterSets();

	/**
	 *  Get the parameter element.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IParameter getParameter(String name);

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IParameterSet getParameterSet(String name);

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(String name);

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(String name);

	/**
	 *  Get the element type (i.e. the name declared in the ADF).
	 *  @return The element type.
	 */
	public String	getType();
}