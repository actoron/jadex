package jadex.commons.service;

/**
 *  Struct for information about a required service.
 */
public class RequiredServiceInfo
{
	//-------- attributes --------
	
	/** The component internal service name. */
	protected String name;
	
	/** The service interface type. */
	protected Class type;
	
	/** Flag if binding is dynamic. */
	protected boolean dynamic;

	/** Flag if multiple services should be returned. */
	protected boolean multiple;

	/** Flag to indicate if cached results are allowed. */
	protected boolean forced;

	/** Flag to indicate if remote search should be performed. */
	protected boolean remote;
	
	/** Flag to indicate only a declared (local) service should be used. */
	protected boolean declared;
	
	/** Flag to indicate that only upwards search should be used. */
	protected boolean upwards;


		
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo()
	{
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type)
	{
		this(name, type, false, false);
	}

	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean upwards)
	{
		this(name, type, false, false, false, false, false, upwards);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple)
	{
		this(name, type, dynamic, multiple, false, false, false, false);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple, boolean declared)
	{
		this(name, type, dynamic, multiple, false, false, declared, false);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple, 
		boolean remote, boolean forced)
	{
		this(name, type, dynamic, multiple, remote, forced, false, false);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple, 
		boolean remote, boolean forced, boolean declared)
	{
		this(name, type, dynamic, multiple, remote, forced, false, false);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple, 
		boolean remote, boolean forced, boolean declared, boolean upwards)
	{
		this.name = name;
		this.type = type;
		this.dynamic = dynamic;
		this.multiple = multiple;
		this.forced = forced;
		this.remote = remote;
		this.declared = declared;
		this.upwards = upwards;
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class type)
	{
		this.type = type;
	}

	/**
	 *  Get the dynamic.
	 *  @return the dynamic.
	 */
	public boolean isDynamic()
	{
		return dynamic;
	}

	/**
	 *  Set the dynamic.
	 *  @param dynamic The dynamic to set.
	 */
	public void setDynamic(boolean dynamic)
	{
		this.dynamic = dynamic;
	}

	/**
	 *  Get the multiple.
	 *  @return the multiple.
	 */
	public boolean isMultiple()
	{
		return multiple;
	}

	/**
	 *  Set the multiple.
	 *  @param multiple The multiple to set.
	 */
	public void setMultiple(boolean multiple)
	{
		this.multiple = multiple;
	}

	/**
	 *  Get the forced.
	 *  @return the forced.
	 */
	public boolean isForced()
	{
		return forced;
	}

	/**
	 *  Set the forced.
	 *  @param forced The forced to set.
	 */
	public void setForced(boolean forced)
	{
		this.forced = forced;
	}

	/**
	 *  Get the remote.
	 *  @return the remote.
	 */
	public boolean isRemote()
	{
		return remote;
	}

	/**
	 *  Set the remote.
	 *  @param remote The remote to set.
	 */
	public void setRemote(boolean remote)
	{
		this.remote = remote;
	}

	/**
	 *  Get the declared.
	 *  @return the declared.
	 */
	public boolean isDeclared()
	{
		return declared;
	}

	/**
	 *  Set the declared.
	 *  @param declared The declared to set.
	 */
	public void setDeclared(boolean declared)
	{
		this.declared = declared;
	}

	/**
	 *  Get the upwards.
	 *  @return The upwards.
	 */
	public boolean isUpwards()
	{
		return upwards;
	}

	/**
	 *  Set the upwards.
	 *  @param upwards The upwards to set.
	 */
	public void setUpwards(boolean upwards)
	{
		this.upwards = upwards;
	}
	
	
}
