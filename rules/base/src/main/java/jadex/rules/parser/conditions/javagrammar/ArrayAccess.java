package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;


/**
 *  Access an element of an array
 */
public class ArrayAccess	extends	Suffix
{
	//-------- attributes --------
	
	/** The element index. */
	protected Expression	index;
	
	//-------- constructors --------
	
	/**
	 *  Create a new array access.
	 *  @param index	The element index.
	 */
	public ArrayAccess(Expression index)
	{
		this.index	= index;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the suffix.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return index.containsVariable(var);
	}
	
	/**
	 *  Get the index.
	 */
	public Expression	getIndex()
	{
		return this.index;
	}
	
	/**
	 *  Get a string representation of this array access.
	 */
	public String	toString()
	{
		return "["+index+"]";
	}

	/**
	 *  Test if this array access is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof ArrayAccess
			&& ((ArrayAccess)o).getIndex().equals(getIndex());
	}
	
	/**
	 *  Get the hash code of array field access.
	 */
	public int	hashCode()
	{
		return 31 + getIndex().hashCode();
	}
}
