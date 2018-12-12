package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVObjectType;


/**
 *  Demand the existence of an object and bind an instance to a variable.
 */
public class ExistentialDeclaration	extends	Expression
{
	//-------- attributes --------
	
	/** The object type. */
	protected OAVObjectType	type;
	
	/** The variable. */
	protected Variable	variable;
	
	//-------- constructors --------
	
	/**
	 *  Create a new existential declaration.
	 *  @param type	The object type.
	 *  @param variable	The variable.
	 */
	public ExistentialDeclaration(OAVObjectType type, Variable variable)
	{
		this.type	= type;
		this.variable	= variable;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return var.equals(variable);
	}
	
	/**
	 *  Get the variable.
	 */
	public Variable	getVariable()
	{
		return this.variable;
	}
	
	/**
	 *  Get the object type.
	 */
	public OAVObjectType	getType()
	{
		return this.type;
	}
	
	/**
	 *  Get a string representation of this existential declaration.
	 */
	public String	toString()
	{
		return type.getName()+" "+variable.getName();
	}

	/**
	 *  Test if this object is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof ExistentialDeclaration
			&& ((ExistentialDeclaration)o).getType().equals(getType())
			&& ((ExistentialDeclaration)o).getVariable().equals(getVariable());
	}
	
	/**
	 *  Get the hash code.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getType().hashCode();
		ret	= ret*31 + getVariable().hashCode();
		return ret;
	}
}
