package jadex.bdi.runtime;

import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.commons.Future;
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
	public String getType();
	
	//-------- convenience methods --------
	
	/**
	 *  Get the value of a parameter.
	 *  @return The value.
	 */
	public IFuture getParameterValue(String parameter);
	
	/**
	 *  Set the parameter value.
	 *  @param parameter The parameter name.
	 *  @param value The value.
	 */
	public IFuture setParameterValue(String parameter, Object value);
	
	/**
	 *  Get the values of a parameterset.
	 *  @return The values.
	 */
	public IFuture getParameterSetValues(String parameterset);
	
	/**
	 *  Add a value to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param value The value.
	 */
	public IFuture addParameterSetValue(String parameterset, Object value);
	
	/**
	 *  Add values to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param values The values.
	 */
	public IFuture addParameterSetValues(String parameterset, Object[] values);
	
	/**
	 *  Remove a value to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param value The value.
	 */
	public IFuture removeParameterSetValue(final String parameterset, final Object value);
	
	/**
	 *  Remove all values from a parameterset.
	 *  @param parameterset The parameterset name.
	 */
	public IFuture removeParameterSetValues(final String parameterset);
	
	/**
	 *  Remove a value to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param value The value.
	 */
	public IFuture containsParameterSetValue(final String parameterset, final Object value);
	
	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public IFuture getParameterSetSize(final String parameterset);
}
