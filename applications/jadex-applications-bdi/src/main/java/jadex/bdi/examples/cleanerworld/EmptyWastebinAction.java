package jadex.bdi.examples.cleanerworld;

import java.util.HashSet;
import java.util.Map;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public class EmptyWastebinAction  extends SimplePropertyObject implements ISpaceAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		ISpaceObject wastebin = (ISpaceObject)parameters.get(ISpaceAction.OBJECT_ID);
		wastebin.setProperty("wastes", Integer.valueOf(0));
		wastebin.setProperty("wasteids", new HashSet<Object>());
		return Boolean.TRUE;
	}
}
