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
	protected boolean	suspend;

	/** The master flag (default: false). */
	protected boolean	master;
	
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
		this(config, args, parent, false, false);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 *  @param parent	The parent of the component to be created.
	 *  @param suspend	The suspend flag.
	 *  @param master	The master flag.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, boolean suspend, boolean master)
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
	 *  @param imports	The imports.
	 */
	public CreationInfo(String config, Map args, IComponentIdentifier parent, boolean suspend, boolean master, String[] imports)
	{
		this.config	= config;
		this.args	= args;
		this.parent	= parent;
		this.suspend	= suspend;
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
	public boolean isSuspend()
	{
		return suspend;
	}

	/**
	 *  Set the suspend flag.
	 *  @param suspend the suspend to set flag
	 */
	public void setSuspend(boolean suspend)
	{
		this.suspend = suspend;
	}

	/**
	 *  Get the master flag.
	 *  @return the master flag.
	 */
	public boolean isMaster()
	{
		return master;
	}

	/**
	 *  Set the master flag.
	 *  @param master the master to set
	 */
	public void setMaster(boolean master)
	{
		this.master = master;
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
