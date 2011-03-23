package jadex.bridge.service;

/**
 *  Required service binding information.
 */
public class RequiredServiceBinding
{
	//-------- attributes --------
	
	/** The service name. */
	protected String name;
	
	/** The component name. */
	protected String componentname;
	
	/** The component type, i.e. the model name. */
	protected String componenttype;

	/** The component filename. */
//	protected String componentfilename;
	
	/** Flag if binding is dynamic. */
	protected boolean dynamic;

	/** The search scope. */
	protected String scope;
	
	/** The create flag. */
	protected boolean create;
	
	// todo:
	/** The recover flag. */

	//-------- constructors --------

	/**
	 *  Create a new binding. 
	 */
	public RequiredServiceBinding()
	{
	}
	
	/**
	 *  Create a new binding. 
	 */
	public RequiredServiceBinding(String name, String scope)
	{
		this(name, null, null, false, scope, false);
	}
	
	/**
	 *  Create a new binding. 
	 */
	public RequiredServiceBinding(String name, String scope, boolean dynamic)
	{
		this(name, null, null, dynamic, scope, false);
	}

	/**
	 *  Create a new binding.
	 */
	public RequiredServiceBinding(String name, String componentname,
			String componenttype, boolean dynamic, String scope, boolean create)//, String componentfilename)
	{
		this.name = name;
		this.componentname = componentname;
		this.componenttype = componenttype;
		this.dynamic = dynamic;
		this.scope = scope;
		this.create = create;
//		this.componentfilename = componentfilename;
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
	 *  Get the componentname.
	 *  @return the componentname.
	 */
	public String getComponentName()
	{
		return componentname;
	}

	/**
	 *  Set the componentname.
	 *  @param componentname The componentname to set.
	 */
	public void setComponentName(String componentname)
	{
		this.componentname = componentname;
	}

	/**
	 *  Get the componenttype.
	 *  @return the componenttype.
	 */
	public String getComponentType()
	{
		return componenttype;
	}

	/**
	 *  Set the componenttype.
	 *  @param componenttype The componenttype to set.
	 */
	public void setComponentType(String componenttype)
	{
		this.componenttype = componenttype;
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

	/**
	 *  Get the create.
	 *  @return The create.
	 */
	public boolean isCreate()
	{
		return create;
	}

	/**
	 *  Set the create.
	 *  @param create The create to set.
	 */
	public void setCreate(boolean create)
	{
		this.create = create;
	}

//	/**
//	 *  Get the componentfilename.
//	 *  @return The componentfilename.
//	 */
//	public String getComponentFilename()
//	{
//		return componentfilename;
//	}
//
//	/**
//	 *  Set the componentfilename.
//	 *  @param componentfilename The componentfilename to set.
//	 */
//	public void setComponentFilename(String componentfilename)
//	{
//		this.componentfilename = componentfilename;
//	}
}
