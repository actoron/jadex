package jadex.application.space.agr;

import jadex.application.model.MComponentType;
import jadex.application.model.MApplicationType;
import jadex.commons.SReflect;

/**
 *  A positions represents an instance of a role in a group instance.
 */
public class MPosition
{
	/** The role type. */
	protected String	role;
	
	/** The agent type. */
	protected String	componenttype;

	//-------- methods --------
	
	/**
	 *  Get the role type.
	 *  @return	The role type.
	 */
	public String	getRoleType()
	{
		return this.role;
	}

	/**
	 *  Set the role type.
	 *  @param role The role type to set.
	 */
	public void setRoleType(String	role)
	{
		this.role = role;
	}

	/**
	 *  Get the agent type.
	 *  @return The agent type.
	 */
	public String	getComponentType()
	{
		return this.componenttype;
	}

	/**
	 *  Set the agent type.
	 *  @param agenttype The agent type to set.
	 */
	public void setComponentType(String	agenttype)
	{
		this.componenttype = agenttype;
	}
	
	/**
	 *  Get the agent type.
	 *  @return The agent type.
	 */
	public MComponentType	getMComponentType(MApplicationType apptype)
	{
		return apptype.getMComponentType(componenttype);
	}
	
	/**
	 *  Get a string representation of this AGR position.
	 *  @return A string representation of this AGR position.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(roletype=");
		sbuf.append(getRoleType());
		sbuf.append(", agenttype=");
		sbuf.append(getComponentType());
		sbuf.append(")");
		return sbuf.toString();
	}
}
