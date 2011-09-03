package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.IADataObject;

/**
 * Basic Interface for parameters
 * @author 5Haubeck
 *
 */
public interface IAParameter extends IADataObject
{
	/**
	 * Returns the name of the parameter
	 * @return name of parameter
	 */
	public String getName();

	/**
	 * Returns the class of the value
	 * @return class of the parameter value
	 */
	public Class getValueClass();

	/**
	 * Checks if parameter is feasible
	 * @return true, if parameter is feasible
	 */
	public boolean isFeasable();

	/**
	 * Sets the value of the parameter
	 * @param value the parameter Value
	 */
	public void setValue(Object value);

	/**
	 * Returns the value of the parameter
	 * @returnvalue value (class=ValueClasss)
	 */
	public Object getValue();

	/**
	 * Sets the class of the value
	 * @param type class of the value
	 */
	public void setValueClass(Class type);
	
	/**
	 * Sets the value editable
	 * @param editable true, if parmater should be editable
	 */
	public void setValueEditable(Boolean editable);

	/**
	 * Sets only the value editbale
	 * @param editable true, if value should be editable
	 */
	public Boolean isValueEditable();

	
}
