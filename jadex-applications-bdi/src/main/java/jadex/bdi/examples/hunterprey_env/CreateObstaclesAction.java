package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.commons.SimplePropertyObject;

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
	public Object perform(Map parameters, IEnvironmentSpace space)
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
				grid.createSpaceObject("obstacle", null, props, null, null);
//				System.out.println("Created obstacle: "+obj);
			}
		}
		
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
