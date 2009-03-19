package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SUtil;

/**
 *  A computable value composed of primary value and zero to many suffixes.
 */
public class UnaryExpression
{
	//-------- attributes --------
	
	/** The primary value. */
	protected Primary	primary;
	
	/** The suffixes, if any. */
	protected Suffix[]	suffixes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new unary expression.
	 */
	public UnaryExpression(Primary primary, Suffix[] suffixes)
	{
		this.primary	= primary;
		this.suffixes	= suffixes;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the primary value.
	 */
	public Primary	getPrimary()
	{
		return this.primary;
	}
	
	/**
	 *  Get the suffixes (if any).
	 */
	public Suffix[]	getSuffixes()
	{
		return suffixes;
	}
	
	/**
	 *  Get a string representation of this unary expression.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(primary.toString());
		for(int s=0; suffixes!=null && s<suffixes.length; s++)
		{
			ret.append(suffixes[s].toString());
		}
		return ret.toString();
	}

	/**
	 *  Test if this unary expression is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof UnaryExpression
			&& ((UnaryExpression)o).getPrimary().equals(getPrimary())
			&& SUtil.arrayEquals(((UnaryExpression)o).getSuffixes(), getSuffixes());
	}
	
	/**
	 *  Get the hash code of this unary expression.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getPrimary().hashCode();
		ret = 31*ret + (getSuffixes()!=null ? SUtil.arrayHashCode(getSuffixes()): 0);
		return ret;
	}
}
