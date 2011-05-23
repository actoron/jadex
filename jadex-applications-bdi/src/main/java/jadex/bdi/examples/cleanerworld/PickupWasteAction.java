package jadex.bdi.examples.cleanerworld;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector1;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Double;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Map;

/**
 *  Action for picking up waste.
 */
public class PickupWasteAction extends SimplePropertyObject implements ISpaceAction
{
	protected static final IVector1 TOLERANCE = new Vector1Double(0.05);
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{	
		Space2D env = (Space2D)space;
		
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject waste = (ISpaceObject)parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject avatar = env.getAvatar(owner);

//		if(so.getProperty("garbage")!=null)
//			System.out.println("pickup failed: "+so);
		
		assert avatar.getProperty("waste")==null: avatar+", "+avatar.getProperty("waste")+", "+waste;
		
		if(env.getDistance((IVector2)waste.getProperty(Space2D.PROPERTY_POSITION), (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION)).greater(TOLERANCE))
			throw new RuntimeException("Not near enough to waste: "+waste+" "+avatar);
			
//		System.out.println("pickup waste action: "+so+" "+so.getProperty(Grid2D.POSITION)+" "+waste);
		avatar.setProperty("waste", waste);
		env.setPosition(waste.getId(), null);
//		System.out.println("pickup waste action "+parameters);

		return null;
	}
}
