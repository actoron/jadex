package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.appdescriptor.MAgentType;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.adapter.base.contextservice.ISpace;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.List;

/**
 *  An instance of an AGR space. 
 */
public class MAGRSpaceInstance extends MSpaceInstance
{
	/** The groups. */
	protected List groups;
	
	//-------- methods --------
	
	/**
	 *  Get the groups of this space.
	 *  @return An array of groups (if any).
	 */
	public MGroupInstance[] getMGroupInstances()
	{
		return groups==null? null:
			(MGroupInstance[])groups.toArray(new MGroupInstance[groups.size()]);
	}

	/**
	 *  Add a group to this space.
	 *  @param group The group to add. 
	 */
	public void addMGroupInstance(MGroupInstance group)
	{
		if(groups==null)
			groups	= new ArrayList();
		groups.add(group);
	}
	
	/**
	 *  Get a group per name.
	 *  @param name The name.
	 *  @return The group.
	 */
	public MGroupInstance getMGroupInstance(String name)
	{
		MGroupInstance	ret	= null;
		for(int i=0; ret==null && i<groups.size(); i++)
		{
			MGroupInstance	gi	= (MGroupInstance)groups.get(i);
			if(gi.getName().equals(name))
				ret	= gi;
		}
		return ret;
	}
	
	/**
	 *  Create a space.
	 */
	public ISpace createSpace(ApplicationContext app)
	{
		MAGRSpaceType	type	= (MAGRSpaceType)getType(app.getApplicationType());
//		AGRSpace	ret	= new AGRSpace(type);
//		for(int g=0; groups!=null && g<groups.size(); g++)
//		{
//			MGroupInstance	mgroupi	= (MGroupInstance)groups.get(g);
//			MGroupType	mgroupt	=  mgroupi.getGroupType((MAGRSpaceType)type);
//			Group	group	= new Group(mgroupi.getName(), mgroupt);
//			ret.addGroup(group);
//			
//			MPosition[]	positions	= mgroupi.getMPositions();
//			for(int p=0; positions!=null && p<positions.length; p++)
//			{
//				MAgentType	at	= positions[p].getMAgentType(app.getApplicationType());
//				MRoleType	rt	= positions[p].getRoleType((MAGRSpaceType)type);
//				IAgentIdentifier[]	agents	= app.getAgents(at);
//				// todo: position.getNumber()
//				for(int a=0; agents!=null && a<agents.length; a++)
//				{
//					group.addPosition(agents[a], rt);
//				}
//			}
//		}
		
		return null;
	}

	
	/**
	 *  Get a string representation of this AGR space instance.
	 *  @return A string representation of this AGR space instance.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		if(groups!=null)
		{
			sbuf.append(", groups=");
			sbuf.append(groups);
		}
		sbuf.append(")");
		return sbuf.toString();
	}
}
