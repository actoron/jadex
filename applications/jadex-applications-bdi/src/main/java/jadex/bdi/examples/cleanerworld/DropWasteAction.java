package jadex.bdi.examples.cleanerworld;

import java.util.HashSet;
import java.util.Map;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;

/**
 *  Action for dropping a waste.
 */
public class DropWasteAction extends SimplePropertyObject implements ISpaceAction
{
	protected static final IVector1 TOLERANCE = new Vector1Double(0.05);
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map<String, Object> parameters, IEnvironmentSpace space)
	{	
		Space2D env = (Space2D)space;
		
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject wastebin = (ISpaceObject)parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject waste = (ISpaceObject)parameters.get("waste");
		ISpaceObject avatar = env.getAvatar(owner);

		assert avatar.getProperty("waste")!=null: avatar;
		
		if(env.getDistance((IVector2)wastebin.getProperty(Space2D.PROPERTY_POSITION), (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION)).greater(TOLERANCE))
			throw new RuntimeException("Not near enough to wastebin: "+wastebin+" "+avatar);
			
//		System.out.println("drop: "+waste);
		if(((Boolean)wastebin.getProperty("full")).booleanValue())
			throw new RuntimeException("Wastebin already full: "+wastebin+" "+avatar);

		HashSet<Object> wasteids = (HashSet<Object>)wastebin.getProperty("wasteids");
		wasteids.add(waste.getId());
		int wastes = ((Integer)wastebin.getProperty("wastes")).intValue();
		wastebin.setProperty("wastes", Integer.valueOf(wastes+1));
		env.destroySpaceObject(waste.getId());
		avatar.setProperty("waste", null);

//		System.out.println("drop waste action finished: "+parameters);

		return null;
	}
}
