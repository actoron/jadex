package jadex.adapter.base.appdescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 *  Structuring type representation.
 */
public class StructuringType
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The structuring type (e.g. continuous, grid). */
	protected String type;
	
	/** The properties. */
	// todo: introduce more specific properties for subtypes
	protected Map properties;

	//-------- constructors --------

	/**
	 *  Create a new structuring type.
	 */
	public StructuringType()
	{
		this.properties = new HashMap();
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
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
		return this.type;
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
	 *  Add a property.
	 *  @param prop A property. 
	 */
	public void addProperty(String name, String value)
	{
		this.properties.put(name, value);
	}

	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return this.properties;
	}
	
}