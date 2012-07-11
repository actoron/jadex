package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IParameter;

/**
 *  A parameter element.
 */
public class Parameter implements IParameter
{
	/** The name. */
	protected String name;
	
	/** The type. */
	protected String type;
	
	/** The initial value. */
	protected String value;
	
	/** The set attribute. */
	protected boolean set;
	
	/**
	 *  Create a new parameter.
	 */
	public Parameter()
	{
		this.name = IParameter.DEFAULT_NAME;
		this.type = "java.lang.Object";
		this.value = "";
		this.set = false;
	}
	
	/**
	 *  Create a new parameter.
	 */
	public Parameter(String name, String type, String value, boolean set)
	{
		this.name = name;
		this.type = type;
		this.value = value;
		this.set = set;
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
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 *  Get the set.
	 *  @return The set.
	 */
	public boolean isSet()
	{
		return set;
	}

	/**
	 *  Set the set.
	 *  @param set The set to set.
	 */
	public void setSet(boolean set)
	{
		this.set = set;
	}
		
}
