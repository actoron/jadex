package jadex.bdi.examples.cleanerworld;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
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
		boolean ret = false;
				
		Space2D env = (Space2D)space;
		
		IComponentIdentifier owner = (IComponentIdentifier)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject waste = (ISpaceObject)parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject avatar = env.getAvatar(owner);

//		if(so.getProperty("garbage")!=null)
//			System.out.println("pickup failed: "+so);
		
		assert avatar.getProperty("waste")==null: avatar;
		
		if(env.getDistance((IVector2)waste.getProperty(Space2D.PROPERTY_POSITION), (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION)).greater(TOLERANCE))
			throw new RuntimeException("Not near enough to waste: "+waste+" "+avatar);
			
//		System.out.println("pickup waste action: "+so+" "+so.getProperty(Grid2D.POSITION)+" "+waste);
//		if(Math.random()>0.5)
		{
//			System.out.println("pickup: "+waste);
			avatar.setProperty("waste", waste);
			env.setPosition(waste.getId(), null);
			ret = true;
			//pcs.firePropertyChange("worldObjects", garb, null);
//				System.out.println("Agent picked up: "+owner+" "+so.getProperty(Space2D.POSITION));
		}
//			else
//			{
//			System.out.println("Agent picked up failed: "+name+" "+getPosition(name));
//			}

//		System.out.println("pickup waste action "+parameters);

		return ret? Boolean.TRUE: Boolean.FALSE;
	}
}
