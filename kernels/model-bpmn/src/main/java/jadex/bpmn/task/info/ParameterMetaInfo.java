package jadex.bpmn.task.info;

import jadex.bridge.ClassInfo;



/**
 *  Meta information for a parameter.
 */
public class ParameterMetaInfo extends PropertyMetaInfo
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
	public ParameterMetaInfo(String direction, Class<?> clazz, String name, String initialval, String description)
	{
		super(clazz, name, initialval, description);
		this.direction = direction;
	}
	
	/**
	 *  Create a new parameter meta info.
	 */
	public ParameterMetaInfo(String direction, ClassInfo clinfo, String name, String initialval, String description)
	{
		super(clinfo, name, initialval, description);
		this.direction = direction;
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
	 *  Sets the direction.
	 *
	 *  @param direction The direction.
	 */
	public void setDirection(String direction)
	{
		this.direction = direction;
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
