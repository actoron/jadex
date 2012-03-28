package jadex.bridge.modelinfo;



/**
 *  Simple default implementation for an argument.
 */
public class Argument	extends UnparsedExpression	implements IArgument
{
	//-------- attributes --------
	
	/** The description. */
	protected String	description;
	
	//-------- constructors --------
	
	/**
	 *  Create a new argument.
	 */
	public Argument()
	{
	}
	
	/**
	 *  Create a new argument.
	 */
	public Argument(String name, String description, String classname, String defaultvalue)
	{
		super(name, classname, defaultvalue, null);
		this.description = description;
	}
	
	//-------- methods --------
	
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
	 *  Get the default value.
	 *  @return The default value.
	 */
	public UnparsedExpression	getDefaultValue()
	{
		return this;
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
		return "Argument(defaultvalue=" + this.value + ", description="
			+ this.description + ", name=" + this.name + ", typename="
			+ this.clazz + ")";
	}
}
