package jadex.microkernel;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;

/**
 *  Meta info for micro agents.
 */
public class MicroAgentMetaInfo
{
	//-------- attributes --------
	
	/** The description. */
	protected String description;
	
	/** The configurations. */
	protected String[] configs;
	
	/** The arguments. */
	protected IArgument[] args;
	
	/** The results. */
	protected IArgument[] results;
	
	/** The breakpoints. */
	protected String[] breakpoints;
	
	//-------- constructors --------
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String description, String[] configs, IArgument[] args, IArgument[] results, String[] breakpoints)
	{
//		this.name = name;
		this.description = description;
		this.configs = configs == null? SUtil.EMPTY_STRING: configs;
		this.args = args == null? new IArgument[0]: args;
		this.results = results == null? new IArgument[0]: results;
		this.breakpoints = breakpoints == null? new String[0]: breakpoints;
	}

	//-------- methods --------
	
	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public String[] getConfigurations()
	{
		return configs;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{
		return args;
	}
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults()
	{
		return results;
	}

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 *  Get the breakpoints.
	 *  @return The breakpoints.
	 */
	public String[] getBreakpoints()
	{
		return breakpoints;
	}
}
