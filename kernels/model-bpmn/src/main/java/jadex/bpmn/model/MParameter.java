package jadex.bpmn.model;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;


/**
 *  A parameter model element.
 */
public class MParameter extends MProperty
{
	//-------- constants --------
	
	/** The constant for direction in. */
	public static final String DIRECTION_IN = "in";
	
	/** The constant for direction out. */
	public static final String DIRECTION_OUT = "out";

	/** The constant for direction inout. */
	public static final String DIRECTION_INOUT = "inout";

	//-------- attributes --------
	
	/** The direction. */
	protected String direction;
	
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
	public MParameter(String direction, ClassInfo clazz, String name, 
		UnparsedExpression initialval)
	{
		super(clazz, name, initialval);
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
	 *  Test if parameter is out (i.e. out or inout).
	 *  @return True, if is a out parameter.
	 */
	public boolean isOut()
	{
		return direction.equals(DIRECTION_OUT) || direction.equals(DIRECTION_INOUT);
	}
	
	/**
	 *  Test if parameter is in (i.e. in or inout).
	 *  @return True, if is a in parameter.
	 */
	public boolean isIn()
	{
		return direction.equals(DIRECTION_IN) || direction.equals(DIRECTION_INOUT);
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
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(ClassInfo clazz)
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
	public UnparsedExpression getInitialValue()
	{
		return this.initialval;
	}

	/**
	 *  Set the initial value.
	 *  @param initialval The initial value to set.
	 */
	public void setInitialValue(UnparsedExpression initialval)
	{
		this.initialval = initialval;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "MParameter(clazz=" + this.clazz + ", direction="
			+ this.direction + ", initialval=" + this.initialval
			+ ", name=" + this.name + ")";
	}	
}
