package jadex.bpmn.runtime.task;

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
	protected Class clazz;
	
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
	public ParameterMetaInfo(String direction, Class clazz, String name, String initialval, String description)
	{
		this.direction = direction;
		this.clazz = clazz;
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
	public Class getClazz()
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
