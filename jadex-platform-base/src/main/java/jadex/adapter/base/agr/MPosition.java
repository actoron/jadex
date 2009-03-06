package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.MAgentType;

/**
 * 
 */
public class MPosition
{
	/** The role. */
	protected MRoleType role;
	
	/** The agent type. */
	protected MAgentType agenttype;

	//-------- methods --------
	
	/**
	 * @return the role
	 */
	public MRoleType getMRoleType()
	{
		return this.role;
	}

	/**
	 * @param role the role to set
	 */
	public void setMRoleType(MRoleType role)
	{
		this.role = role;
	}

	/**
	 * @return the agenttype
	 */
	public MAgentType getMAgentType()
	{
		return this.agenttype;
	}

	/**
	 * @param agenttype the agenttype to set
	 */
	public void setMAgentType(MAgentType agenttype)
	{
		this.agenttype = agenttype;
	}
	
}
