package jadex.tools.comanalyzer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Base class for agents and messages. This class provides the common elements
 * such as the parameter attribute and the setters and getters for the
 * parameters.
 */
public abstract class ParameterElement implements Serializable, Comparable
{
	//-------- constants --------

	// Names of fields for ElementPanel
	public static final String CLASS = "class";

	public static final String NAME = "name";

	// -------- attributes --------

	/** The visibility */
	protected boolean visible;

	/** The parameters. */
	protected Map<String, Object> parameters;

	// ------ constructors ------

	/**
	 * Default constructor
	 */
	public ParameterElement()
	{
		parameters = new HashMap();
	}

	//-------- ParameterElement methods --------


	/**
	 * Returns the parameter map.
	 * @return The parameter map.
	 */
	public Map getParameters()
	{
		return parameters;
	}

	/**
	 * Returns a parameter value.
	 * @param name The name of the parameter.
	 * @return The value.
	 */
	public Object getParameter(String name)
	{
		return parameters.get(name);
	}

	/**
	 * Replaces the existing parameter map.
	 * @param map The parameter map.
	 */
	public void setParameters(Map map)
	{
		parameters = map;
	}

	/**
	 * Sets a parameter value
	 * @param name The name of the parameter.
	 * @param value The value.
	 */
	public void setParameter(String name, Object value)
	{
		parameters.put(name, value);
	}

	/**
	 * Checks if a given parameter is contained by the parameter map.
	 * @param name The name of the parameter.
	 * @return <code>true</code> if the parameter is contained.
	 */
	public boolean hasParameter(String name)
	{
		return parameters.containsKey(name);
	}

	/**
	 * @return The visiblity.
	 */
	public boolean isVisible()
	{
		return visible;
	}
	
	/**
	 *  Set the visbile state.
	 *  @param visible The visible state.
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	/**
	 * @return The id of the element.
	 */
	public abstract String getId();

	// -------- Comparable interface --------

	/**
	 * Elements are compared by their ids.
	 */
	public int compareTo(Object o)
	{
		ParameterElement other = (ParameterElement)o;
		return this.getId().compareTo(other.getId());
	}

	// -------- Object methods --------

	/**
	 * Only messages with the same id are equal.
	 */
	public boolean equals(final Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;

		final ParameterElement other = (ParameterElement)obj;
		return getId().equals(other.getId());
	}

	/**
	 * @return The string representation for the element.
	 */
	public String toString()
	{
		return "ParameterElement(" + "id=" + getId() + ")";
	}

}
