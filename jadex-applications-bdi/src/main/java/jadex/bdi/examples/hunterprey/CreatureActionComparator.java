package jadex.bdi.examples.hunterprey;

import jadex.application.space.envsupport.environment.AgentActionList;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.AgentActionList.ActionEntry;
import jadex.bridge.IComponentIdentifier;

import java.util.Comparator;

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
		AgentActionList.ActionEntry	entry1	= (ActionEntry)obj1;
		AgentActionList.ActionEntry	entry2	= (ActionEntry)obj2;
		
		int	ret	= entry1.compareTo(entry2);
		
		if(ret!=0)
		{
			IComponentIdentifier actor1 = (IComponentIdentifier)entry1.parameters.get(ISpaceAction.ACTOR_ID);
			ISpaceObject avatar1 = space.getAvatar(actor1);
			IComponentIdentifier actor2 = (IComponentIdentifier)entry2.parameters.get(ISpaceAction.ACTOR_ID);
			ISpaceObject avatar2 = space.getAvatar(actor2);

			if(ret>0 && avatar1.getType().equals("hunter") && avatar2.getType().equals("prey")
				|| ret<0 && avatar1.getType().equals("prey") && avatar2.getType().equals("hunter"))
			{
				ret	= -ret;
			}
		}
		
		return ret;
	}
}