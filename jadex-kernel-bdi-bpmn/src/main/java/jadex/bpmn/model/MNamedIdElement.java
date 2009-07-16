package jadex.bpmn.model;

import java.util.HashMap;
import java.util.Map;

/**
 *  Base class for named id elements.
 */
public class MNamedIdElement extends MIdElement
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;

	/** The description. */
	protected String description;
	
	/** The properties. */
	protected Map properties;
	
	//-------- methods ---------
	
	/**
	 *  Get the full description of the model element.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Set the description.
	 *  @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
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
	 *  Get a declared value from the model.
	 *  @param name The name.
	 */
	public void setPropertyValue(String name, Object value)
	{
		if(properties==null)
			properties = new HashMap();
		properties.put(name, value);
	}
	
	/**
	 *  Get a property value from the model.
	 *  @param name The name.
	 */
	public Object getPropertyValue(String name)
	{
		Object ret = null;
		if(properties!=null)
			ret = properties.get(name);
		return ret;
	}
}
