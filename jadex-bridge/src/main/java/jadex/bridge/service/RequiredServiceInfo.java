package jadex.bridge.service;

/**
 *  Struct for information about a required service.
 */
public class RequiredServiceInfo
{
	//-------- constants --------
	
	/** Local component scope. */
	public static final String SCOPE_LOCAL = "local";
	
	/** Component scope. */
	public static final String SCOPE_COMPONENT = "component";
	
	/** Application scope. */
	public static final String SCOPE_APPLICATION = "application";

	/** Platform scope. */
	public static final String SCOPE_PLATFORM = "platform";

	/** Global scope. */
	public static final String SCOPE_GLOBAL = "global";
	
	/** Upwards scope. */
	public static final String SCOPE_UPWARDS = "upwards";
	
	/** Parent scope. */
	public static final String SCOPE_PARENT = "parent";
	
	//-------- attributes --------

	// service description
	
	/** The component internal service name. */
	protected String name;
	
	/** The service interface type. */
	protected Class type;

	/** Flag if multiple services should be returned. */
	protected boolean multiple;

	// binding specification
	
	/** The default binding. */
	protected RequiredServiceBinding binding;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type)
	{
		this(name, type, RequiredServiceInfo.SCOPE_APPLICATION);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, String scope)
	{
		this(name, type, false, new RequiredServiceBinding(name, scope));
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class type, boolean multiple, RequiredServiceBinding binding)
	{
		this.name = name;
		this.type = type;
		this.multiple = multiple;
		this.binding = binding;
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
	 *  Get the binding.
	 *  @return the binding.
	 */
	public RequiredServiceBinding getDefaultBinding()
	{
		return binding;
	}

	/**
	 *  Set the binding.
	 *  @param binding The binding to set.
	 */
	public void setDefaultBinding(RequiredServiceBinding binding)
	{
		this.binding = binding;
	}
}
