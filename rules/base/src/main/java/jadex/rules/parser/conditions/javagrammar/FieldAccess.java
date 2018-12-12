package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;


/**
 *  Access a field of a value.
 */
public class FieldAccess	extends	Suffix
{
	//-------- attributes --------
	
	/** The name of the field. */
	protected String	name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new field access.
	 *  @param name	The field name.
	 */
	public FieldAccess(String name)
	{
		this.name	= name;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the suffix.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return false;
	}
	
	/**
	 *  Get the name of the field.
	 */
	public String	getName()
	{
		return this.name;
	}
	
	/**
	 *  Get a string representation of this field access.
	 */
	public String	toString()
	{
		return "."+name;
	}

	/**
	 *  Test if this field access is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof FieldAccess
			&& ((FieldAccess)o).getName().equals(getName());
	}
	
	/**
	 *  Get the hash code of this field access.
	 */
	public int	hashCode()
	{
		return 31 + getName().hashCode();
	}
}
