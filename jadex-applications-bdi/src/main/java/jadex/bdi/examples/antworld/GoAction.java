package jadex.bdi.examples.antworld;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.commons.SimplePropertyObject;

import java.util.Map;

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
//		System.out.println("go action: "+parameters);
		
		String dir = (String)parameters.get(DIRECTION);
		Object oid = parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject obj = space.getSpaceObject(oid);
		IVector2 pos = (IVector2)obj.getProperty(Space2D.POSITION);
		IVector2 size = ((Space2D)space).getAreaSize();
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
		
//		System.out.println("NextDesired position: " + pos.toString());
		checkPositionForGravitation(pos, space, obj);
		
		((Space2D)space).setPosition(oid, pos);
		
//		System.out.println("Go action: "+obj.getProperty(IAgentAction.ACTOR_ID)+" "+pos);
		
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
	
	/**
	 * Checks whether the position has a gravitation.
	 * @param pos
	 * @param space
	 * @param obj
	 */
	private void checkPositionForGravitation(IVector2 pos, IEnvironmentSpace space, ISpaceObject obj){
		ISpaceObject[] objects = space.getSpaceObjectsByType("gravitationField");
		
		for (int i = 0; i < objects.length; i++) {
			// System.out.println(objects[i].getId());
			Vector2Int gravitationPos = (Vector2Int) objects[i].getProperty(Space2D.POSITION);
			if(gravitationPos.equals(pos)){
				System.out.println("Desired Position has gravitation: " + pos.toString());
				obj.setProperty(AntVisionGenerator.GRAVITATION_FELT, new Boolean(true));
//				obj.setProperty(GravitationListener.FEELS_GRAVITATION, true);
//				ObjectEvent gravitationEvent = new ObjectEvent("FEEL_GRAVITATION");
//				gravitationEvent.setParameter("GRAV_POS", gravitationPos);
//				obj.fireObjectEvent(gravitationEvent);
				break;
			}
						
		}
	}
}
