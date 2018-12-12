package jadex.extension.agr;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.SReflect;

/**
 *  An AGR group type.
 */
public class MGroupType
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The roles. */
	protected List	roles;

	//-------- methods --------
	
	/**
	 *  Set the name of the group type.
	 *  @param name	The name of the group type.
	 */
	public void	setName(String name)
	{
		this.name	= name;
	}
	
	/**
	 *  Get the name of the group type.
	 *  @return The name of the group type.
	 */
	public String	getName()
	{
		return this.name;
	}
	
	/**
	 *  Get the roles of this group type.
	 *  @return An array of roles (if any).
	 */
	public MRoleType[] getMRoleTypes()
	{
		return roles==null ? null :
			(MRoleType[])roles.toArray(new MRoleType[roles.size()]);
	}

	/**
	 *  Add a role to this group type.
	 *  @param role	The role to add. 
	 */
	public void addMRoleType(MRoleType role)
	{
		if(roles==null)
			roles	= new ArrayList();
		roles.add(role);
	}

	/**
	 *  Remove a role from this group type.
	 *  @param role	The role to remove. 
	 */
	public void removeMRoleType(MRoleType role)
	{
		if(roles!=null)
		{
			roles.remove(role);
			if(roles.isEmpty())
				roles	= null;
		}
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
		if(roles!=null)
		{
			sbuf.append(", roles=");
			sbuf.append(roles);
		}
		sbuf.append(")");
		return sbuf.toString();
	}
}
