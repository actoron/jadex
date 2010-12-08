package jadex.bridge;

import java.util.Map;

/**
 *  A parameter object to capture
 *  extra information for component creation.
 *  All of the information is optional, i.e.
 *  may be null.
 */
public class CreationInfo
{
	//-------- attributes --------
	
	/** The configuration. */
	protected String	config;
	
	/** The arguments (map with name/value pairs). */
	protected Map	args;
	
	/** The parent component. */
	protected IComponentIdentifier	parent;
	
	/** The suspend flag (default: false). */
	protected Boolean	suspend;

	/** The master flag (default: false). */
	protected Boolean	master;
	
	/** The daemon flag (default: false). */
	protected Boolean daemon;
	
	/** The auto shutdown flag (default: false). */
	protected Boolean autoshutdown;
	
	/** The imports. */
	protected String[]	imports;
	
	//-------- constructors --------
	
	/**
	 *  Create a new creation info. 
	 */
	public CreationInfo()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new creation info.
	 *  @param parent	The parent of the component to be created.
	 */
	public CreationInfo(IComponentIdentifier parent)
	{
		this(null, parent);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param args	The arguments.
	 */
	public CreationInfo(Map args)
	{
		this(null, args);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 */
	public CreationInfo(String config, Map args)
	{
		this(config, args, null);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 */
	public CreationInfo(Map args, IComponentIdentifier parent)
	{
		this(null, args, parent);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent)
	{
		this(config, args, parent, null, (String[])null);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 *  @param suspend	The suspend flag.
	 *  @param master	The master flag.
	 *  @param imports	The imports.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, boolean suspend)
	{
		this(config, args, parent, null, (String[])null);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 *  @param suspend	The suspend flag.
	 *  @param master	The master flag.
	 *  @param imports	The imports.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, Boolean suspend, String[] imports)
	{
		this(config, args, parent, null, null, null, null, imports);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 *  @param suspend	The suspend flag.
	 *  @param master	The master flag.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, Boolean suspend, Boolean master)
	{
		this(config, args, parent, suspend, master, null);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 *  @param suspend	The suspend flag.
	 *  @param master	The master flag.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, Boolean suspend, Boolean master, Boolean daemon)
	{
		this(config, args, parent, suspend, master, daemon, null);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 *  @param suspend	The suspend flag.
	 *  @param master	The master flag.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, Boolean suspend, 
		Boolean master, Boolean daemon, Boolean autoshutdown)
	{
		this(config, args, parent, suspend, master, daemon, autoshutdown, null);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 *  @param suspend	The suspend flag.
	 *  @param master	The master flag.
	 *  @param imports	The imports.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, 
		Boolean suspend, Boolean master, Boolean daemon, Boolean autoshutdown, String[] imports)
	{
		this.config	= config;
		this.args	= args;
		this.parent	= parent;
		this.suspend	= suspend;
		this.master = master;
		this.daemon = daemon;
		this.autoshutdown = autoshutdown;
		this.imports	= imports;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the configuration.
	 *  @return the config.
	 */
	public String getConfiguration()
	{
		return config;
	}

	/**
	 *  Set the configuration.
	 *  @param config the config to set.
	 */
	public void setConfiguration(String config)
	{
		this.config = config;
	}

	/**
	 *  Get the arguments.
	 *  @return the args.
	 */
	public Map getArguments()
	{
		return args;
	}

	/**
	 *  Set the arguments.
	 *  @param args the args to set
	 */
	public void setArguments(Map args)
	{
		this.args = args;
	}

	/**
	 *  Get the parent.
	 *  @return the parent
	 */
	public IComponentIdentifier getParent()
	{
		return parent;
	}

	/**
	 *  Set the parent.
	 *  @param parent the parent to set
	 */
	public void setParent(IComponentIdentifier parent)
	{
		this.parent = parent;
	}

	/**
	 *  Get the suspend flag.
	 *  @return the suspend flag
	 */
	public Boolean getSuspend()
	{
		return suspend;
	}

	/**
	 *  Set the suspend flag.
	 *  @param suspend the suspend to set flag
	 */
	public void setSuspend(Boolean suspend)
	{
		this.suspend = suspend;
	}

	/**
	 *  Get the master.
	 *  @return The master.
	 */
	public Boolean getMaster()
	{
		return master;
	}

	/**
	 *  Set the master.
	 *  @param master The master to set.
	 */
	public void setMaster(Boolean master)
	{
		this.master = master;
	}

	/**
	 *  Get the daemon.
	 *  @return The daemon.
	 */
	public Boolean getDaemon()
	{
		return daemon;
	}

	/**
	 *  Set the daemon.
	 *  @param daemon The daemon to set.
	 */
	public void setDaemon(Boolean daemon)
	{
		this.daemon = daemon;
	}

	/**
	 *  Get the autoshutdown.
	 *  @return The autoshutdown.
	 */
	public Boolean getAutoShutdown()
	{
		return autoshutdown;
	}

	/**
	 *  Set the autoshutdown.
	 *  @param autoshutdown The autoshutdown to set.
	 */
	public void setAutoshutdown(Boolean autoshutdown)
	{
		this.autoshutdown = autoshutdown;
	}

	/**
	 *  Get the imports.
	 *  @return the imports.
	 */
	public String[] getImports()
	{
		return imports;
	}

	/**
	 *  Set the imports
	 *  @param imports The imports to set.
	 */
	public void setImports(String[] imports)
	{
		this.imports = imports;
	}
}
