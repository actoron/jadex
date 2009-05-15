package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.AgentActionList;
import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.AgentActionList.ActionEntry;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *  Action for creating obstacles at random locations.
 */
public class CreateObstaclesAction extends SimplePropertyObject implements ISpaceAction
{
	//-------- constants --------
	
	/** The count parameter. */
	public static final String	PARAMETER_COUNT	= "count";
	
	//-------- IAgentAction interface --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, final IEnvironmentSpace space)
	{
//		System.out.println("create obstacles action: "+parameters);
		
		int	count	= ((Integer)parameters.get(PARAMETER_COUNT)).intValue();
		Grid2D grid = (Grid2D)space;
		
		for(int i=0; i<count; i++)
		{
			IVector2 pos = grid.getEmptyGridPosition();
			if(pos!=null)
			{
				Map props = new HashMap();
				props.put(Space2D.POSITION, pos);
				grid.createSpaceObject("obstacle", props, null, null);
//				System.out.println("Created obstacle: "+obj);
			}
		}
		
		// Hack!!! Where to add action ordering in xml (environment executor!?)
//		((AbstractEnvironmentSpace)space).getAgentActionList().setOrdering(new Comparator()
//		{
//			public int compare(Object obj1, Object obj2)
//			{
//				AgentActionList.ActionEntry	entry1	= (ActionEntry)obj1;
//				AgentActionList.ActionEntry	entry2	= (ActionEntry)obj2;
//				
//				int	ret	= entry1.compareTo(entry2);
//				
//				if(ret!=0)
//				{
//					IAgentIdentifier actor1 = (IAgentIdentifier)entry1.parameters.get(IAgentAction.ACTOR_ID);
//					ISpaceObject avatar1 = space.getOwnedObjects(actor1)[0];
//					IAgentIdentifier actor2 = (IAgentIdentifier)entry2.parameters.get(IAgentAction.ACTOR_ID);
//					ISpaceObject avatar2 = space.getOwnedObjects(actor2)[0];
//	
//					if(ret>0 && avatar1.getType().equals("hunter") && avatar2.getType().equals("prey")
//						|| ret<0 && avatar1.getType().equals("prey") && avatar2.getType().equals("hunter"))
//					{
//						ret	= -ret;
//					}
//				}
//				
//				return ret;
//			}
//		});
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "create_obstacles";
	}
}
