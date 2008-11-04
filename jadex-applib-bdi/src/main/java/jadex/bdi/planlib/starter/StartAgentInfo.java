package jadex.bdi.planlib.starter;

import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;

import java.util.Map;

/**
 *  Struct for saving info about agents to start.
 */
public class StartAgentInfo
{
	//-------- attributes --------

	/** The Jadex agent type in dot notation. */
	protected String type;

	/** The Jadex agent configuration. */
	protected String config;

	/** The agent instance name prototype "%n" will
	  be replaced a counted number. */
	protected String nameprototype;

	/** The delay after starting the agent. */
	protected long delay;

	/** Other arguments. */
	protected Map args;

	/** Counter for agent instances. */
	protected static int agent_instance_counter;

	//-------- constructors --------

	/**
	 *  Create a new start agent info.
	 */
 	public StartAgentInfo(String type, String nameprototype, long delay, Map args)
	{
 		this(type, null, nameprototype, delay, args);
	}

	/**
	 *  Create a new start agent info.
	 */
 	public StartAgentInfo(String type, String config, String nameprototype, long delay, Map args)
	{
		this.type = type;
		this.config = config;
		this.nameprototype = nameprototype;
		this.delay = delay;
		this.args = args;
	}

 	/**
	 *  Create a new start agent info.
	 */
 	public StartAgentInfo(String type, String nameprototype, long delay, String[] argnames, Object[] argvals)
	{
 		this(type, null, nameprototype, delay, argnames, argvals);
	}
 	
	/**
	 *  Create a new start agent info.
	 */
 	public StartAgentInfo(String type, String config, String nameprototype, long delay, String[] argnames, Object[] argvals)
	{
 		this.type = type;
		this.config = config;
		this.nameprototype = nameprototype;
		this.delay = delay;
		
		this.args = SCollection.createHashMap();
		for(int i=0; argnames!=null && i<argnames.length; i++)
		{
			args.put(argnames[i], argvals[i]);
		}
	}
 	
	//-------- methods --------

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return config;
	}

	/**
	 *  Get the name.
	 *  Delivers a new one, when a namingschema is used.
	 *  @return The name.
	 */
	public String getName()
	{
		return SUtil.replace(nameprototype, "%n", ""+agent_instance_counter++);
	}

	/**
	 *  Get the name prototype.
	 *  @return The name.
	 */
	public String getNamePrototype()
	{
		return nameprototype;
	}

	/**
	 *  Get the delay.
	 *  @return The delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments()
	{
		return args;
	}
}