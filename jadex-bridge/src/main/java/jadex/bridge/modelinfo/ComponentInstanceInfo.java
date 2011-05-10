package jadex.bridge.modelinfo;

import jadex.bridge.service.RequiredServiceBinding;

/**
 * 
 */
public class ComponentInstanceInfo extends Startable
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type name. */
	protected String typename;
	
	/** The configuration. */
	protected String configuration;

	/** The number of components. */
	protected String number;
	
	/** The list of contained arguments. */
	protected UnparsedExpression[] arguments;
	
	/** The list of required service binding infos. */
	protected RequiredServiceBinding[] bindings;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component.
	 */
	public ComponentInstanceInfo()
	{
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
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
	 *  Get the type name.
	 *  @return The type name.
	 */
	public String getTypeName()
	{
		return this.typename;
	}

	/**
	 *  Set the typename.
	 *  @param typename The typename to set.
	 */
	public void setTypename(String typename)
	{
		this.typename = typename;
	}

	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.configuration;
	}
	
	/**
	 *  Set the configuration.
	 *  @param configuration The configuration to set.
	 */
	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}

	/**
	 *  Get the number (expression).
	 *  @return The number.
	 */
	public String getNumber()
	{
		return this.number;
	}
	
	/**
	 *  Set the number.
	 *  @param number The number to set.
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}

	/**
	 *  Get the list of arguments.
	 *  @return The arguments.
	 */
	public UnparsedExpression[] getArguments()
	{
		return arguments!=null? arguments: new UnparsedExpression[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 */
	public void setArguments(UnparsedExpression[] arguments)
	{
		this.arguments = arguments;
	}

	/**
	 *  Get the bindings.
	 *  @return the bindings.
	 */
	public RequiredServiceBinding[] getBindings()
	{
		return bindings!=null? bindings: new RequiredServiceBinding[0];
	}
	
	/**
	 *  Set the bindings.
	 *  @param bindings The bindings to set.
	 */
	public void setBindings(RequiredServiceBinding[] bindings)
	{
		this.bindings = bindings;
	}
	
	/**
	 *  Get the model of the component instance.
	 *  @param apptype The application type this component is used in.
	 *  @return The name of the component type.
	 */
	public SubcomponentTypeInfo getType(IModelInfo model)
	{
		SubcomponentTypeInfo ret = null;
		SubcomponentTypeInfo[] componenttypes = model.getSubcomponentTypes();
		for(int i=0; ret==null && i<componenttypes.length; i++)
		{
			if(componenttypes[i].getName().equals(getTypeName()))
				ret = componenttypes[i];
		}
		return ret;
	}

	/**
	 *  A string of this object.
	 */
	public String toString()
	{
		return "ComponentInstanceInfo(typename="+typename+
			(number!=null ? ", number="+number: "")+")";
	}
}
