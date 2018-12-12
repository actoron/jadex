package jadex.bpmn.task.info;

import jadex.bridge.ClassInfo;

/**
 *  Meta information for a property.
 */
public class PropertyMetaInfo
{
	//-------- attributes --------
	
	/** The clazz. */
	protected ClassInfo clazz;
	
	/** The name. */
	protected String name;
	
	/** The initial value. */
	protected String initialval;
	
	/** The parameter description. */
	protected String description;

	//-------- constructors --------
	
	/**
	 *  Create a new parameter meta info.
	 */
	public PropertyMetaInfo()
	{
	}
	
	/**
	 *  Create a new parameter meta info.
	 */
	public PropertyMetaInfo(Class<?> clazz, String name, String initialval, String description)
	{
		this(new ClassInfo(clazz), name, initialval, description);
	}

	/**
	 *  Create a new parameter meta info.
	 */
	public PropertyMetaInfo(ClassInfo clinfo, String name, String initialval, String description)
	{
		this.clazz = clinfo;
		this.name = name;
		this.initialval = initialval;
		this.description = description;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public ClassInfo getClazz()
	{
		return this.clazz;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Get the initialval.
	 *  @return The initialval.
	 */
	public String getInitialValue()
	{
		return this.initialval;
	}

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 *  Sets the clazz.
	 *
	 *  @param clazz The clazz.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Sets the name.
	 *
	 *  @param name The name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Sets the initialval.
	 *
	 *  @param initialval The initialval.
	 */
	public void setInitialValue(String initialval)
	{
		this.initialval = initialval;
	}

	/**
	 *  Sets the description.
	 *
	 *  @param description The description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "PropertyMetaInfo(clazz=" + this.clazz + ", initialval=" + this.initialval
			+ ", name=" + this.name + ", description=" + this.description +")";
	}	
}
