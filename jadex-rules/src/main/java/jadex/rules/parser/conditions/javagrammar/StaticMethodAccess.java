package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SUtil;
import jadex.rules.state.OAVJavaType;


/**
 *  Invoke a static method.
 */
public class StaticMethodAccess	extends	Expression
{
	//-------- attributes --------
	
	/** The object type. */
	protected OAVJavaType	type;
	
	/** The name of the method. */
	protected String	name;
	
	/** The parameter value expressions. */
	protected Expression[]	parametervalues;
	
	//-------- constructors --------
	
	/**
	 *  Create a new method access.
	 *  @param type	The object type.
	 *  @param name	The method name.
	 *  @param parametervalues	Expressions for the parameter values (if any).
	 */
	public StaticMethodAccess(OAVJavaType type, String name, Expression[] parametervalues)
	{
		this.type	= type;
		this.name	= name;
		this.parametervalues	= parametervalues;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the object type.
	 */
	public OAVJavaType	getType()
	{
		return this.type;
	}
	
	/**
	 *  Get the name of the method.
	 */
	public String	getName()
	{
		return this.name;
	}
	
	/**
	 *  Get the parameter values expressions (if any).
	 */
	public Expression[]	getParameterValues()
	{
		return this.parametervalues;
	}
	
	/**
	 *  Get a string representation of this method access.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(type.getName());
		ret.append(".");
		ret.append(name);
		ret.append("(");
		for(int p=0; parametervalues!=null && p<parametervalues.length; p++)
		{
			if(p!=0)
				ret.append(", ");
			ret.append(parametervalues[p].toString());
		}
		ret.append(")");
		return ret.toString();
	}

	/**
	 *  Test if this method access is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof StaticMethodAccess
			&& ((StaticMethodAccess)o).getType().equals(getType())
			&& ((StaticMethodAccess)o).getName().equals(getName())
			&& SUtil.arrayEquals(((StaticMethodAccess)o).getParameterValues(), getParameterValues());
	}
	
	/**
	 *  Get the hash code of this unary expression.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getType().hashCode();
		ret	= 31*ret + getName().hashCode();
		ret = 31*ret + (getParameterValues()!=null ? SUtil.arrayHashCode(getParameterValues()): 0);
		return ret;
	}
}
