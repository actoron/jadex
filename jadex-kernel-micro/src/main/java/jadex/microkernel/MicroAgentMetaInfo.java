package jadex.microkernel;

import jadex.bridge.IArgument;

/**
 *  Meta info for micro agents.
 */
public class MicroAgentMetaInfo
{
	//-------- attributes --------
	
	/** The configurations. */
	protected String[] configs;
	
	/** The arguments. */
	protected IArgument[] args;
	
	//-------- constructors --------
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String[] configs, IArgument[] args)
	{
		this.configs = configs;
		this.args = args;
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
}
