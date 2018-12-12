package jadex.bpmn.model;


/**
 *  Base class for named id elements.
 */
public class MNamedIdElement extends MAnnotationElement
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;

	/** The description. */
	protected String description;
	
	/** The properties. */
//	protected Map properties;
	
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
//	public void setPropertyValue(String name, Object value)
//	{
//		if(properties==null)
//			properties = new LinkedHashMap();
//		properties.put(name, value);
//	}
	
	/**
	 *  Get a property value from the model.
	 *  @param name The name.
	 */
//	public Object getPropertyValue(String name)
//	{
//		Object ret = null;
//		if(properties!=null)
//			ret = properties.get(name);
//		return ret;
//	}
	
	/**
	 *  Get a property value from the model.
	 *  @param name The name.
	 */
//	public Object getParsedPropertyValue(String name)
//	{
//		Object val	= getPropertyValue(name);
//		if (val instanceof IParsedExpression)
//		{
//			val = ((IParsedExpression) val).getValue(null);
//		}
//		else if(val instanceof UnparsedExpression)
//		{
//			//	System.out.println("here: "+ret);
//			val = ((IParsedExpression) ((UnparsedExpression)val).getParsed()).getValue(null);
//		}
//		return val;
//	}
	
	/**
	 *  Test, if a property is declared.
	 *  @param name	The property name.
	 *  @return True, if the property is declared.
	 */
//	public boolean	hasPropertyValue(String name)
//	{
//		return properties!=null && properties.containsKey(name);
//	}
	
	/**
	 *  Get all property names.
	 *  @return All property names.
	 */
//	public String[] getPropertyNames()
//	{
//		return properties!=null? (String[])properties.keySet().toArray(new String[properties.size()]): SUtil.EMPTY_STRING_ARRAY;
//	}
	
	/**
	 *  Removes a property.
	 *  
	 *  @param name Name of the property.
	 */
//	public void removeProperty(String name)
//	{
//		properties.remove(name);
//	}
}
