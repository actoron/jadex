package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.AgentActionList;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.AgentActionList.ActionEntry;
import jadex.bridge.IAgentIdentifier;

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
			IAgentIdentifier actor1 = (IAgentIdentifier)entry1.parameters.get(ISpaceAction.ACTOR_ID);
			ISpaceObject avatar1 = space.getOwnedObjects(actor1)[0];
			IAgentIdentifier actor2 = (IAgentIdentifier)entry2.parameters.get(ISpaceAction.ACTOR_ID);
			ISpaceObject avatar2 = space.getOwnedObjects(actor2)[0];

			if(ret>0 && avatar1.getType().equals("hunter") && avatar2.getType().equals("prey")
				|| ret<0 && avatar1.getType().equals("prey") && avatar2.getType().equals("hunter"))
			{
				ret	= -ret;
			}
		}
		
		return ret;
	}
}