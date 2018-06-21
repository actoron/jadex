package jadex.tools.comanalyzer;

/**
 * A filter for Agents.
 */
public class ComponentFilter extends ParameterElementFilter
{
	// -------- constants ---------

	/** The empty agent filter */
	public static final ComponentFilter EMPTY = new ComponentFilter();

	// -------- constructors --------

	/**
	 * Create an agent filter.
	 */
	public ComponentFilter()
	{
	}

	
	/**
	 * Create an agent filter with given arguments.
	 * 
	 * @param name The attribute name.
	 * @param value The attribute value.
	 */
	public ComponentFilter(String name, Object value)
	{
		addValue(name, value);
	}

}
