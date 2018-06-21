package jadex.bdiv3x.runtime;



/**
 *  Interface for all parameter sets.
 */
public interface IParameterSet extends IElement
{
	//-------- methods --------

	/**
	 *  Add a value to a parameter set.
	 *  @param value The new value.
	 */
	public void addValue(Object value);

	/**
	 *  Remove a value to a parameter set.
	 *  @param value The new value.
	 */
	public void removeValue(Object value);

	/**
	 *  Add values to a parameter set.
	 */
	public void addValues(Object[] values);

	/**
	 *  Remove all values from a parameter set.
	 */
	public void removeValues();

	/**
	 *  Get a value equal to the given object.
	 *  @param oldval The old value.
	 */
//	public Object	getValue(Object oldval);

	/**
	 *  Test if a value is contained in a parameter.
	 *  @param value The value to test.
	 *  @return True, if value is contained.
	 */
	public boolean containsValue(Object value);

	/**
	 *  Get the values of a parameterset.
	 *  @return The values.
	 */
	public Object[]	getValues();

	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public int size();

	/**
	 *  Get the value class.
	 *  @return The value class.
	 * /
	public Class	getClazz();*/

	/**
	 *  Update or add a value. When the value is already
	 *  contained it will be updated to the new value.
	 *  Otherwise the value will be added.
	 *  @param value The new or changed value.
	 * /
	public void updateOrAddValue(Object value);*/

	/**
	 *  Update a value to a new value. Searches the old
	 *  value with equals, removes it and stores the new value.
	 *  @param newvalue The new value.
	 * /
	public void updateValue(Object newvalue);*/

	/**
	 *  Replace a value with another one.
	 *  @param oldvalue The old value.
	 *  @param newvalue The new value.
	 * /
	public void replaceValue(Object oldvalue, Object newvalue);*/
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName();
}
