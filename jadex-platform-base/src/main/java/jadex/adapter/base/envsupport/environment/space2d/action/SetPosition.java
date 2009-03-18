package jadex.adapter.base.envsupport.environment.space2d.action;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector2;

public class SetPosition extends AbstractSpace2dAction
{
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Long objectId = (Long) parameters.get(OBJECT_ID);
		IVector2 position = (IVector2) parameters.get(POSITION_ID);
		ISpaceObject obj = space.getSpaceObject(objectId);
		obj.setProperty("position", position);
		return null;
	}
	
	public Object getId()
	{
		return SET_POSITION;
	}

}
