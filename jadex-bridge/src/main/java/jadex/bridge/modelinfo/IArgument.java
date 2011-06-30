package jadex.bridge.modelinfo;


/**
 *  Interface for start arguments.
 */
public interface IArgument
{
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription();
	
	/**
	 *  Get the class name.
	 *  @return The class name. 
	 */
	public String getClassname();
	
	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public Class getClazz(ClassLoader classloader, String[] imports);
	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue(String configname);
	
	/**
	 *  Check the validity of an input.
	 *  @param input The input.
	 *  @return True, if valid.
	 */
	public boolean validate(String input);
}
