package jadex.rules.rulesystem.rules;

import jadex.commons.SUtil;

/**
 *  A value source representing a constant value.
 */
public class Constant
{
	//-------- attributes --------
	
	/** The value. */
	protected Object	value;
	
	//-------- constructors --------
	
	/**
	 *  Create a constant.
	 *  @param value	The value.
	 */
	public Constant(Object value)
	{
		this.value	= value;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value. 
	 *  @return The value.
	 */
	public Object	getValue()
	{
		return value;
	}
	
	/**
	 *  Test if this constant equals another object.
	 *  @param obj	The other object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Constant && SUtil.equals(getValue(), ((Constant)obj).getValue());
	}
	
	/**
	 *  Get the hash code of this constant.
	 *  @return	The hash code.
	 */
	public int hashCode()
	{
		return 31 + (getValue()!=null ? getValue().hashCode() : 0);
	}

	/**
	 *  Get a stering representation.
	 */
	public String	toString()
	{
		return "Constant("+value+")"; 
	}
}
