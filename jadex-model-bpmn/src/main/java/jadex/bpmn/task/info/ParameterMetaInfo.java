package jadex.bpmn.task.info;

import jadex.bridge.ClassInfo;


/**
 *  Meta information for a parameter.
 */
public class ParameterMetaInfo
{
	//-------- constants --------
	
	/** The constant for direction in. */
	public static String DIRECTION_IN = "in";
	
	/** The constant for direction out. */
	public static String DIRECTION_OUT = "out";

	/** The constant for direction inout. */
	public static String DIRECTION_INOUT = "inout";
	
	//-------- attributes --------
	
	/** The direction. */
	protected String direction;
	
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
	public ParameterMetaInfo()
	{
	}
	
	/**
	 *  Create a new parameter meta info.
	 */
	public ParameterMetaInfo(String direction, Class clazz, String name, String initialval, String description)
	{
		this.direction = direction;
		this.clazz = new ClassInfo(clazz);
		this.name = name;
		this.initialval = initialval;
		this.description = description;
	}
		
	//-------- methods --------
	
	/**
	 *  Get the direction.
	 *  @return The direction.
	 */
	public String getDirection()
	{
		return this.direction;
	}

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
	 *  Sets the direction.
	 *
	 *  @param direction The direction.
	 */
	public void setDirection(String direction)
	{
		this.direction = direction;
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
		return "ParameterMetaInfo(clazz=" + this.clazz + ", direction="
			+ this.direction + ", initialval=" + this.initialval
			+ ", name=" + this.name + ", description=" + this.description +")";
	}	
}
