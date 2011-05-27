package jadex.micro.examples.dungeonkeeper;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.action.GetPosition;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;

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