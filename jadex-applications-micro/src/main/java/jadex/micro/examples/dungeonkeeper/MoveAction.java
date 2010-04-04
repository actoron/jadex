package jadex.micro.examples.dungeonkeeper;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Grid2D;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.environment.space2d.action.GetPosition;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Int;
import jadex.application.space.envsupport.math.Vector2Int;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  The move action.
 */
public class MoveAction extends SimplePropertyObject implements ISpaceAction
{
	public static Set allowedfields;
	
	static
	{
		allowedfields = new HashSet();
		allowedfields.add(InitMapProcess.DIRT_PATH);
		allowedfields.add(InitMapProcess.CLAIMED_PATH);
		allowedfields.add(InitMapProcess.WATER);
		allowedfields.add(InitMapProcess.LAVA);
		allowedfields.add(InitMapProcess.DUNGEONHEART);
	}
	
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Grid2D grid = (Grid2D)space; 

		Object id = parameters.get(ISpaceAction.OBJECT_ID);
		IVector2 pos = (IVector2)parameters.get(GetPosition.PARAMETER_POSITION);
		
		ISpaceObject field = (ISpaceObject)grid.getNearObjects(pos, Vector1Int.ZERO, "field").iterator().next();
		String type = (String)field.getProperty("type");
		if(allowedfields.contains(type))
		{
			grid.setPosition(id, pos);
		}
		else
		{
			throw new RuntimeException("Not allowed to go to: "+pos+" is "+type);
		}
		return null;
	}
}