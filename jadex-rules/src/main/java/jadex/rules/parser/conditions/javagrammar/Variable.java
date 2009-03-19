package jadex.rules.parser.conditions.javagrammar;


/**
 *  A variable represents a primary value.
 */
public class Variable	extends	Primary
{
	//-------- attributes --------
	
	/** The name of the variable. */
	protected String	name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new variable.
	 */
	public Variable(String name)
	{
		this.name	= name;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name of the variable.
	 */
	public String	getName()
	{
		return this.name;
	}
	
	/**
	 *  Get a string representation of this variable.
	 */
	public String	toString()
	{
		return name;
	}

	/**
	 *  Test if this variable is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof Variable
			&& ((Variable)o).getName().equals(getName());
	}
	
	/**
	 *  Get the hash code of this variable.
	 */
	public int	hashCode()
	{
		return 31 + getName().hashCode();
	}
}
