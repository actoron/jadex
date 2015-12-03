package jadex.bdi.examples.hunterprey;

import java.util.Comparator;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.extension.envsupport.environment.ComponentActionList;
import jadex.extension.envsupport.environment.ComponentActionList.ActionEntry;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Sort creature actions to execute hunters before preys.
 */
public class CreatureActionComparator implements Comparator
{
	//-------- attributes --------
	
	/** The space. */
	protected IEnvironmentSpace	space;

	//-------- constructors -------- 
	
	/**
	 *  Create a new creature action comparator.
	 */
	public CreatureActionComparator(IEnvironmentSpace space)
	{
		this.space = space;
	}

	//-------- Comparator interface --------
	
	/**
	 *  Return a negative number when the first action should be executed before the second.
	 */
	public int compare(Object obj1, Object obj2)
	{
		ComponentActionList.ActionEntry	entry1	= (ActionEntry)obj1;
		ComponentActionList.ActionEntry	entry2	= (ActionEntry)obj2;
		
		int	ret	= entry1.compareTo(entry2);
		
		if(ret!=0)
		{
			IComponentDescription actor1 = (IComponentDescription)entry1.parameters.get(ISpaceAction.ACTOR_ID);
			ISpaceObject avatar1 = space.getAvatar(actor1);
			IComponentDescription actor2 = (IComponentDescription)entry2.parameters.get(ISpaceAction.ACTOR_ID);
			ISpaceObject avatar2 = space.getAvatar(actor2);

			if(avatar1!=null && avatar2!=null && (ret>0 && avatar1.getType().equals("hunter") && avatar2.getType().equals("prey")
				|| ret<0 && avatar1.getType().equals("prey") && avatar2.getType().equals("hunter")))
			{
				ret	= -ret;
			}
		}
		
		return ret;
	}
}