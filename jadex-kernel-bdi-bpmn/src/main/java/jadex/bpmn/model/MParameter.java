package jadex.bpmn.model;

import jadex.javaparser.IParsedExpression;

/**
 *  A parameter model element.
 */
public class MParameter
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
	protected IParsedExpression initialval;

	//-------- constructors --------
	
	/**
	 *  Create a new parameter.
	 */
	public MParameter()
	{
	}
	
	/**
	 *  Create a new parameter.
	 */
	public MParameter(String direction, Class clazz, String name, 
		IParsedExpression initialval)
	{
		this.direction = direction;
		this.clazz = clazz;
		this.name = name;
		this.initialval = initialval;
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
	 *  Set the direction.
	 *  @param direction The direction to set.
	 */
	public void setDirection(String direction)
	{
		if(!direction.equals(DIRECTION_IN) && !direction.equals(DIRECTION_OUT) 
			&& !direction.equals(DIRECTION_INOUT))
		{
			throw new RuntimeException("Unknown direction: "+direction);
		}
		this.direction = direction;
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
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(Class clazz)
	{
		this.clazz = clazz;
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
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the initialval.
	 *  @return The initialval.
	 */
	public IParsedExpression getInitialval()
	{
		return this.initialval;
	}

	/**
	 *  Set the initial value.
	 *  @param initialval The initial value to set.
	 */
	public void setInitialValue(IParsedExpression initialval)
	{
		this.initialval = initialval;
	}	
	
}
