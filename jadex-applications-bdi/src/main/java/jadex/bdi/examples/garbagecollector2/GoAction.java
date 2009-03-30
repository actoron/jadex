package jadex.bdi.examples.garbagecollector2;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ObjectEvent;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.environment.space2d.action.AbstractSpace2dAction;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;

/**
 *  The go action for moving one field in one of four directions.
 */
public class GoAction implements ISpaceAction
{
	//-------- constants --------

	/** The directions. */
	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";

	public static final String DIRECTION = "direction";
	
//	public static final String POSITION_CHANGED = "position_changed";

	
	//-------- methods --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		System.out.println("go waste action: "+parameters);
		
		String dir = (String)parameters.get(DIRECTION);
		Object oid = (Long)parameters.get(ISpaceObject.OBJECT_ID);
		ISpaceObject obj = space.getSpaceObject(oid);
		IVector2 pos = (IVector2)obj.getProperty(Space2D.POSITION);
		
		if(dir.equals(UP))
		{
			pos.add(new Vector2Int(0, 1));
		}
		else if(dir.equals(DOWN))
		{
			pos.add(new Vector2Int(0, -1));
		}
		else if(dir.equals(LEFT))
		{
			pos.add(new Vector2Int(-1, 0));
		}
		else if(dir.equals(RIGHT))
		{
			pos.add(new Vector2Int(1, 0));
		}
		
		obj.setProperty(Space2D.POSITION, pos);
		
		System.out.println("Go action: "+obj.getProperty(ISpaceObject.ACTOR_ID)+" "+pos);
		
//		obj.fireObjectEvent(new ObjectEvent(POSITION_CHANGED));
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "go";
	}
}
