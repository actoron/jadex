package jadex.platform.service.cli;

import jadex.commons.transformation.IObjectStringConverter;

/**
 *  Information about a result.
 *  Includes type, description and converter (object -> string).
 */
public class ResultInfo
{
	//-------- attributes --------
	
	/** The type. */
	protected Class<?> type;
	
	/** The description. */
	protected String description;
	
	/** The converter. */
	protected IObjectStringConverter converter;

	//-------- constructors --------
	
	/**
	 *  Create a new argument info.
	 */
	public ResultInfo()
	{
	}

	/**
	 *  Create a new result info.
	 */
	public ResultInfo(Class<?> type, String description, IObjectStringConverter converter)
	{
		this.type = type;
		this.description = description;
		this.converter = converter;
	}

	//-------- methods --------
	
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
	public IObjectStringConverter getConverter()
	{
		return converter;
	}

	/**
	 *  Set the converter.
	 *  @param converter The converter to set.
	 */
	public void setConverter(IObjectStringConverter converter)
	{
		this.converter = converter;
	}
}
