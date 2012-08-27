package jadex.platform.service.cli;

import jadex.commons.transformation.IStringObjectConverter;

/**
 * 
 */
public class ArgumentInfo
{
	/** The type. */
	protected Class<?> type;
	
	/** The description. */
	protected String description;
	
	/** The converter. */
	protected IStringObjectConverter converter;

	/**
	 *  Create a new argument info.
	 */
	public ArgumentInfo()
	{
	}

	/**
	 *  Create a new argument info.
	 */
	public ArgumentInfo(Class<?> type, String description, IStringObjectConverter converter)
	{
		this.type = type;
		this.description = description;
		this.converter = converter;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class< ? > getType()
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
}
