package jadex.bdiv3.examples.garbagecollector;

import java.util.Map;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 *  The go action for moving one field in one of four directions.
 */
public class GoAction extends SimplePropertyObject implements ISpaceAction
{
	//-------- constants --------

	/** The directions. */
	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";

	public static final String DIRECTION = "direction";
	
	
	//-------- methods --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map<String, Object> parameters, IEnvironmentSpace space)
	{
//		System.out.println("go action: "+parameters);
		
		String dir = (String)parameters.get(DIRECTION);
		Object oid = parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject obj = space.getSpaceObject(oid);
		IVector2 pos = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		
//		IVector2 size = ((Space2D)space).getAreaSize();
//		int sizex = size.getXAsInteger();
//		int sizey = size.getYAsInteger();
		int px = pos.getXAsInteger();
		int py = pos.getYAsInteger();
		if(dir.equals(UP))
		{
//			pos = new Vector2Int(px, (py-1+sizey)%sizey);
			pos = new Vector2Int(px, py-1);
		}
		else if(dir.equals(DOWN))
		{
//			pos = new Vector2Int(px, (py+1)%sizey);
			pos = new Vector2Int(px, py+1);
		}
		else if(dir.equals(LEFT))
		{
//			pos = new Vector2Int((px-1+sizex)%sizex, py);
			pos = new Vector2Int(px-1, py);
		}
		else if(dir.equals(RIGHT))
		{
//			pos = new Vector2Int((px+1)%sizex, py);
			pos = new Vector2Int(px+1, py);
		}
		
		((Space2D)space).setPosition(oid, pos);
		
		obj.setProperty("lastmove", dir);
		
//		System.out.println("Go action: "+obj.getProperty(ISpaceAction.ACTOR_ID)+" "+pos);
		
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
