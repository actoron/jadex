package jadex.extension.agr;

import jadex.commons.SReflect;

/**
 *  An AGR role.
 */
public class MRoleType
{
	//-------- attributes --------
	
	/** The name. */
	protected String	name;

	/** The minimum number of agents required for this role. */
	protected int	min;
	
	/** The maximum number of agents allowed for this role (-1 for no restriction). */
	protected int	max;
	
	//-------- constructors --------
	
	/**
	 *  Create a new role.
	 */
	public MRoleType()
	{
		this.max	= -1;
	}
	
	//-------- methods --------
	
	/**
	 *  Set the name of the role.
	 *  @param name	The name of the role.
	 */
	public void	setName(String name)
	{
		this.name	= name;
	}
	
	/**
	 *  Get the name of the role.
	 *  @return The name of the role.
	 */
	public String	getName()
	{
		return this.name;
	}
	
	/**
	 *  Set the minimum number of agents required for this role.
	 *  @param min	The minimum number of agents required for this role.
	 */
	public void	setMin(int min)
	{
		this.min	= min;
	}
	
	/**
	 *  Get the minimum number of agents required for this role.
	 *  @return The minimum number of agents required for this role.
	 */
	public int	getMin()
	{
		return this.min;
	}
	
	/**
	 *  Set the maximum number of agents allowed for this role (-1 for no restriction).
	 *  @param max	The maximum number of agents allowed for this role (-1 for no restriction).
	 */
	public void	setMax(int max)
	{
		this.max	= max;
	}
	
	/**
	 *  Get the maximum number of agents allowed for this role (-1 for no restriction).
	 *  @return The maximum number of agents allowed for this role (-1 for no restriction).
	 */
	public int	getMax()
	{
		return this.max;
	}

	/**
	 *  Get a string representation of this group type.
	 *  @return A string representation of this group type.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(name);
		sbuf.append(", min=");
		sbuf.append(min);
		sbuf.append(", max=");
		sbuf.append(max);
		sbuf.append(")");
		return sbuf.toString();
	}
}
