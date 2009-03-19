package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SUtil;


/**
 *  Invoke a method on an object.
 */
public class MethodAccess	extends	Suffix
{
	//-------- attributes --------
	
	/** The name of the method. */
	protected String	name;
	
	/** The parameter values. */
	protected UnaryExpression[]	parametervalues;
	
	//-------- constructors --------
	
	/**
	 *  Create a new method access.
	 *  @param name	The method name.
	 *  @param parametervalues	Expressions for the parameter values (if any).
	 */
	public MethodAccess(String name, UnaryExpression[] parametervalues)
	{
		this.name	= name;
		this.parametervalues	= parametervalues;
	}
	
	//-------- methods --------
	
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
	public UnaryExpression[]	getParameterValues()
	{
		return this.parametervalues;
	}
	
	/**
	 *  Get a string representation of this method access.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
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
		return o instanceof MethodAccess
			&& ((MethodAccess)o).getName().equals(getName())
			&& SUtil.arrayEquals(((MethodAccess)o).getParameterValues(), getParameterValues());
	}
	
	/**
	 *  Get the hash code of this unary expression.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getName().hashCode();
		ret = 31*ret + (getParameterValues()!=null ? SUtil.arrayHashCode(getParameterValues()): 0);
		return ret;
	}
}
