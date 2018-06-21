package jadex.gpmn.editor.model.gpmn;

/**
 * A context parameter.
 *
 */
public interface IParameter
{
	/** Parameter default name. */
	public static final String DEFAULT_NAME = "UnnamedParameter";
	
	/** Names of the properties. */
	public static final String[] PROPERTYNAMES = new String[] { "Name",
																"Type",
																"Value",
																"Set" };
	
	/** Types of the properties. */
	public static final Class[]  PROPERTYTYPES = new Class[] { String.class,
															   String.class,
															   String.class,
															   Boolean.class};
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName();

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name);

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType();

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type);

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public String getValue();

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(String value);

	/**
	 *  Get the set.
	 *  @return The set.
	 */
	public boolean isSet();

	/**
	 *  Set the set.
	 *  @param set The set to set.
	 */
	public void setSet(boolean set);
}
