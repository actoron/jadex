package jadex.tools.comanalyzer;

/**
 * A filter for Agents.
 */
public class AgentFilter extends ParameterElementFilter
{
	// -------- constants ---------

	/** The empty agent filter */
	public static final AgentFilter EMPTY = new AgentFilter();

	// -------- constructors --------

	/**
	 * Create an agent filter.
	 */
	public AgentFilter()
	{
	}

	
	/**
	 * Create an agent filter with given arguments.
	 * 
	 * @param name The attribute name.
	 * @param value The attribute value.
	 */
	public AgentFilter(String name, Object value)
	{
		addValue(name, value);
	}

}
