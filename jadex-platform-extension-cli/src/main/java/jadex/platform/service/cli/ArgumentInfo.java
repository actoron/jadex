package jadex.platform.service.cli;

import jadex.commons.transformation.IStringObjectConverter;

/**
 *  The argument info provides info about an arguments including
 *  its name, type, defaultvalue, description and a converter.
 */
public class ArgumentInfo
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type. */
	protected Class<?> type;
	
	/** The default value. */
	protected Object defaultvalue;
	
	/** The description. */
	protected String description;
	
	/** The converter. */
	protected IStringObjectConverter converter;

	//-------- constructors --------
	
	/**
	 *  Create a new argument info.
	 */
	public ArgumentInfo()
	{
	}

	/**
	 *  Create a new argument info.
	 */
	public ArgumentInfo(String name, Class<?> type, Object defval, String description, IStringObjectConverter converter)
	{
		this.name = name;
		this.type = type;
		this.defaultvalue = defval;
		this.description = description;
		this.converter = converter;
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
	public void setType(Class< ? > type)
	{
		this.type = type;
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
	 *  Get the converter.
	 *  @return The converter.
	 */
	public IStringObjectConverter getConverter()
	{
		return converter;
	}

	/**
	 *  Set the converter.
	 *  @param converter The converter to set.
	 */
	public void setConverter(IStringObjectConverter converter)
	{
		this.converter = converter;
	}

	/**
	 *  Get the defaultValue.
	 *  @return The defaultValue.
	 */
	public Object getDefaultValue()
	{
		return defaultvalue;
	}

	/**
	 *  Set the defaultValue.
	 *  @param defaultValue The defaultValue to set.
	 */
	public void setDefaultValue(Object defaultValue)
	{
		this.defaultvalue = defaultValue;
	}
	
	/**
	 *  Get the usage text.
	 *  @return The usage text.
	 */
	public String getUsageText()
	{
		StringBuffer ret = new StringBuffer();
		
		ret.append(name==null? "(no name)": name).append(" [").append(type.getSimpleName()).append("]: ");
		if(defaultvalue!=null)
		{
			ret.append(" default value=").append(defaultvalue.toString());
		}
		if(description!=null)
		{
			ret.append(" ").append(description);
		}
		
		return ret.toString();
	}
}
