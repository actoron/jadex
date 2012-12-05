package jadex.bridge.service.types.security;

import jadex.bridge.ClassInfo;

/**
 * 
 */
public class ParameterInfo
{
	/** The name. */
	protected String name;
	
	/** The type. */
	protected Class<?> type;
	
	/** The current value. */
	protected Object value;
	
	/**
	 * 
	 */
	public ParameterInfo()
	{
	}
	
	/**
	 * 
	 */
	public ParameterInfo(String name, Class<?> type, Object value)
	{
		this.name = name;
		this.type = type;
		this.value = value;
	}

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
	 *  Get the type.
	 *  @return The type.
	 */
	public Class<?> getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class<?> type)
	{
		this.type = type;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

}
