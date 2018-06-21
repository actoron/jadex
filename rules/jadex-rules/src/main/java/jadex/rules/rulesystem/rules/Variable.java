package jadex.rules.rulesystem.rules;

import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

/**
 *  Class for a typed variable.
 */
public class Variable
{
	//-------- constants --------
	
	/** The predefined state variable. */
	public static final Variable STATE = new Variable("$state", OAVJavaType.java_object_type);
	
	//-------- attributes --------
	
	/** The variable name. */
	protected String name;
	
	/** The object type. */
	protected OAVObjectType type;
	
	/** Flag if it is a multi variable. */
	protected boolean multi;
	
	/** Flag indicating a temporary variable (not used in rhs). */
	protected boolean temporary;
	
	//-------- constructors --------
	
	/**
	 *  Create a new variable.
	 */
	public Variable(String name, OAVObjectType type)
	{
		this(name, type, false, false);
	}
	
	/**
	 *  Create a new variable.
	 */
	public Variable(String name, OAVObjectType type, boolean multi, boolean temporary)
	{
//		if(name==null || type==null)
//			throw new IllegalArgumentException("Name or type must not null");
		this.name = name;
		this.type = type;
		this.multi = multi;
		this.temporary = temporary;
	}
	
	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public OAVObjectType getType()
	{
		return type;
	}
	
	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(OAVObjectType type)
	{
		this.type = type;
	}

	/**
	 *  Test if it is a multi variable.
	 *  A multi variable can hold 0 - * values
	 *  @return True if multi variable.
	 */
	public boolean isMulti()
	{
		return multi;
	}
	
	/**
	 *  Test if variable is temporary.
	 *  @return True, if temporary.
	 */
	public boolean isTemporary()
	{
		return this.temporary;
	}

	/**
	 *  Get the hash code.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		int result = 31 + name.hashCode();
//		result = 31*result + type.hashCode();
		return result;
	}

	/**
	 *  Test for equality.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Variable 
			&& ((Variable)obj).getName().equals(name);
//			&& ((Variable)obj).getType().equals(type);
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		//return "Variable(name="+name+")";//, type="+type+")";
		//return (!isMulti()? "Variable(": "Multivariable(")+name+")";
//		return (!isMulti()? "Variable(": "Multivariable(")+name+": "+type!=null? type.getName(): "null"+")";
		return (!isMulti()? "Variable(": "Multivariable(")+name+")";
	}
}
