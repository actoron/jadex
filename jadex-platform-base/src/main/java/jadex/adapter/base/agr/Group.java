package jadex.adapter.base.agr;

import jadex.bridge.IAgentIdentifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  An AGR group hold information about agent instances
 *  and their positions (i.e. role instances).
 */
public class Group
{
	//-------- attributes --------
	
	/** The group name. */
	protected String	name; 
	
	/** The positions (role name -> Set{aids}). */
	protected Map	positions;
	
	/** The roles (agent type name -> Set{role names}). */
	protected Map	roles;
	
	//-------- constructors --------
	
	/**
	 *  Create a new group.
	 *  @param type	The group type.
	 */
	public Group(String name)
	{
		this.name	= name;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name of the group.
	 *  @return The group name.
	 */
	public String	getName()
	{
		return name;
	}
	
	/**
	 *  Get the role names for an agent type name.
	 *  @param typename	The agent type name.
	 *  @return The role names (if any).
	 */
	public synchronized String[]	getRolesForType(String typename)
	{
		Set	ret	= roles!=null ? (Set)roles.get(typename) : null;
		return ret!=null ? (String[])ret.toArray(new String[ret.size()]) : null;
	}
	
	/**
	 *  Assign an agent to a role. 
	 *  @param aid	The agent id.
	 *  @param rolename	The role name.
	 */
	public synchronized void	assignRole(IAgentIdentifier aid, String rolename)
	{
		if(positions==null)
			positions	= new HashMap();
		
		Set	rpos	= (Set)positions.get(rolename);
		
		if(rpos==null)
		{
			rpos	= new HashSet();
			positions.put(rolename, rpos);
		}
		
		rpos.add(aid);
	}

	
	/**
	 *  Remove an agent from a role. 
	 *  @param aid	The agent id.
	 *  @param rolename	The role name.
	 */
	public synchronized void	unassignRole(IAgentIdentifier aid, String rolename)
	{
		if(positions!=null)
		{
			Set	rpos	= (Set)positions.get(rolename);
			if(rpos!=null)
			{
				rpos.remove(aid);

				if(rpos.isEmpty())
				{
					positions.remove(rolename);
					if(positions.isEmpty())
						positions	= null;
				}
			}
		}		
	}
	
	/**
	 *  Get the agents with a given role.
	 *  @param rolename	The role name.
	 */
	public synchronized IAgentIdentifier[]	getAgentsForRole(String rolename)
	{
		Set	ret	= positions!=null ? (Set)positions.get(rolename) : null;
		return ret!=null ? (IAgentIdentifier[])ret.toArray(new IAgentIdentifier[ret.size()]) : null;
	}
}
