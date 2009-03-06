package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.contextservice.ISpace;

import java.util.ArrayList;
import java.util.List;

/**
 * 
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
	public ISpace createSpace()
	{
		return new AGRSpace();
	}
}
