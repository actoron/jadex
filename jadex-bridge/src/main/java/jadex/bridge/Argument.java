package jadex.bridge;


/**
 *  Simple default implementation for an argument.
 */
public class Argument implements IArgument
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The description. */
	protected String description;
	
	/** The typename. */
	protected String typename;
	
	/** The default value. */
	protected Object defaultvalue;
	
	//-------- constructors --------
	
	/**
	 * @param name
	 * @param description
	 * @param typename
	 * @param defaultvalue
	 */
	public Argument()
	{
	}
	
	/**
	 * @param name
	 * @param description
	 * @param typename
	 * @param defaultvalue
	 */
	public Argument(String name, String description, String typename)
	{
		this.name = name;
		this.description = description;
		this.typename = typename;
	}
	
	/**
	 * @param name
	 * @param description
	 * @param typename
	 * @param defaultvalue
	 */
	public Argument(String name, String description, String typename, Object defaultvalue)
	{
		this.name = name;
		this.description = description;
		this.typename = typename;
		this.defaultvalue = defaultvalue;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
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
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 *  Get the typename.
	 *  @return The typename. 
	 */
	public String getTypename()
	{
		return typename;
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
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue(String configname)
	{
		// todo: support configurations
		
		return defaultvalue;	
	}
	
	/**
	 *  Set the defaultvalue.
	 *  @param defaultvalue The defaultvalue to set.
	 */
	public void setDefaultValue(Object defaultvalue)
	{
		this.defaultvalue = defaultvalue;
	}

	/**
	 *  Check the validity of an input.
	 *  @param input The input.
	 *  @return True, if valid.
	 */
	public boolean validate(String input)
	{
		// todo: support validation
		return true;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Argument(defaultvalue=" + this.defaultvalue + ", description="
			+ this.description + ", name=" + this.name + ", typename="
			+ this.typename + ")";
	}
}
