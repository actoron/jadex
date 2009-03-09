package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.MAgentType;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.commons.SReflect;

/**
 *  A positions represents an instance of a role in a group instance.
 */
public class MPosition
{
	/** The role type. */
	protected String	role;
	
	/** The agent type. */
	protected String	agenttype;

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
	public String	getAgentType()
	{
		return this.agenttype;
	}

	/**
	 *  Set the agent type.
	 *  @param agenttype The agent type to set.
	 */
	public void setAgentType(String	agenttype)
	{
		this.agenttype = agenttype;
	}
	
	/**
	 *  Get the agent type.
	 *  @return The agent type.
	 */
	public MAgentType	getMAgentType(MApplicationType apptype)
	{
		return apptype.getMAgentType(agenttype);
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
		sbuf.append(getAgentType());
		sbuf.append(")");
		return sbuf.toString();
	}
}
