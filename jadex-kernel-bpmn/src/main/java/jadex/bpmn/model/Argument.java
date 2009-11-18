package jadex.bpmn.model;

import jadex.bridge.IArgument;

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
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
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
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue(String configname)
	{
		// todo: support configurations
		
		return defaultvalue;	
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
}
