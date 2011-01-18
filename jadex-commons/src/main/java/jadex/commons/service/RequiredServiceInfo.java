package jadex.commons.service;

/**
 *  Struct for information about a required service.
 */
public class RequiredServiceInfo
{
	//-------- constants --------
	
	/** Local component scope. */
	public static final String LOCAL_SCOPE = "local";
	
	/** Component scope. */
	public static final String COMPONENT_SCOPE = "component";
	
	/** Application scope. */
	public static final String APPLICATION_SCOPE = "application";

	/** Platform scope. */
	public static final String PLATFORM_SCOPE = "platform";

	/** Global scope. */
	public static final String GLOBAL_SCOPE = "global";
	
	//-------- attributes --------
	
	/** The component internal service name. */
	protected String name;
	
	/** The service interface type. */
	protected Class type;
	
	/** Flag if binding is dynamic. */
	protected boolean dynamic;

	/** Flag if multiple services should be returned. */
	protected boolean multiple;

	/** The search scope. */
	protected String scope;
	
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
		this(name, type, false);
	}

	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean upwards)
	{
		this(name, type, false, false, APPLICATION_SCOPE, upwards);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple)
	{
		this(name, type, dynamic, multiple, APPLICATION_SCOPE);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple, String scope)
	{
		this(name, type, dynamic, multiple, scope, false);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean dynamic, boolean multiple, String scope, boolean upwards)
	{
		this.name = name;
		this.type = type;
		this.dynamic = dynamic;
		this.multiple = multiple;
		this.scope = scope;
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
	
	/**
	 *  Get the scope.
	 *  @return the scope.
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set.
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}
}
