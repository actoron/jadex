package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rules.Variable;

/**
 *  A computable value composed of primary value and zero to many suffixes.
 */
public class PrimaryExpression	extends Expression
{
	//-------- attributes --------
	
	/** The prefix value expression. */
	protected Expression	prefix;
	
	/** The suffixes. */
	protected Suffix[]	suffixes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new primary expression.
	 */
	public PrimaryExpression(Expression prefix, Suffix[] suffixes)
	{
		this.prefix	= prefix;
		this.suffixes	= suffixes;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		boolean	ret	= prefix.containsVariable(var);
		for(int i=0; !ret && suffixes!=null && i<suffixes.length ; i++)
		{
			ret	= suffixes[i].containsVariable(var);
		}
		return ret;
	}
	
	/**
	 *  Get the prefix value expression.
	 */
	public Expression	getPrefix()
	{
		return this.prefix;
	}
	
	/**
	 *  Get the suffixes.
	 */
	public Suffix[]	getSuffixes()
	{
		return suffixes;
	}
	
	/**
	 *  Get a string representation of this primary expression.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(prefix.toString());
		for(int s=0; s<suffixes.length; s++)
		{
			ret.append(suffixes[s].toString());
		}
		return ret.toString();
	}

	/**
	 *  Test if this primary expression is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof PrimaryExpression
			&& ((PrimaryExpression)o).getPrefix().equals(getPrefix())
			&& SUtil.arrayEquals(((PrimaryExpression)o).getSuffixes(), getSuffixes());
	}
	
	/**
	 *  Get the hash code of this primary expression.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getPrefix().hashCode();
		ret = 31*ret + SUtil.arrayHashCode(getSuffixes());
		return ret;
	}
}
