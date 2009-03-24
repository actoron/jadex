package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SUtil;


/**
 *  A literal is a constant primary value.
 */
public class Literal
{
	//-------- attributes --------
	
	/** The value. */
	protected Object	value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new literal.
	 */
	public Literal(Object value)
	{
		this.value	= value;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value.
	 */
	public Object	getValue()
	{
		return this.value;
	}
	
	/**
	 *  Get a string representation of this value.
	 */
	public String	toString()
	{
		return value!=null ? value.toString() : "null";
	}

	/**
	 *  Test if this value is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof Literal
			&& SUtil.equals(((Literal)o).getValue(), getValue());
	}
	
	/**
	 *  Get the hash code of this variable.
	 */
	public int	hashCode()
	{
		return 31 + (getValue()!=null ? getValue().hashCode() : 0);
	}
}
