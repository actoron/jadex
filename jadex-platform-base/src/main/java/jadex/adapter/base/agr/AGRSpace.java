package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.contextservice.ISpace;
import jadex.bridge.IAgentIdentifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  An AGR (agent-group-role) space.
 */
public class AGRSpace implements ISpace
{
	//-------- attributes --------
	
	/** The application context. */
	protected ApplicationContext	context;
	
	/** The name of the space. */
	protected String	name;
	
	/** The groups. */
	protected Map groups;
	
	//-------- constructors --------
	
	/**
	 *  Create a new AGR space.
	 */
	public AGRSpace(String name, ApplicationContext context)
	{
		this.name	= name;
		this.context	= context;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the space name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Add a group to the space.
	 *  @param group	The group to add. 
	 */
	public synchronized	void addGroup(Group group)
	{
		if(groups==null)
			groups	= new HashMap();
		
		groups.put(group.getName(), group);
	}
	
	/**
	 *  Get a group by name.
	 *  @param name	The name of the group.
	 *  @return	The group (if any).
	 */
	public synchronized Group	getGroup(String name)
	{
		return groups!=null ? (Group)groups.get(name) : null;
	}	

	/**
	 *  Called from application context, when an agent was added.
	 *  Also called once for all agents in the context, when a space
	 *  is newly added to the context.
	 *  @param aid	The id of the added agent.
	 */
	public synchronized void	agentAdded(IAgentIdentifier aid)
	{
		if(groups!=null)
		{
			String	type	= context.getAgentType(aid);
			for(Iterator it=groups.values().iterator(); it.hasNext(); )
			{
				Group	group	= (Group)it.next();
				String[]	roles	= group.getRolesForType(type);
				for(int r=0; roles!=null && r<roles.length; r++)
				{
					group.assignRole(aid, roles[r]);
				}
			}
		}
	}

	/**
	 *  Called from application context, when an agent was removed.
	 *  @param aid	The id of the removed agent.
	 */
	public void	agentRemoved(IAgentIdentifier aid)
	{
	}
}
