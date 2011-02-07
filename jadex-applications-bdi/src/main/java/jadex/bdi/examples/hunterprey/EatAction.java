package jadex.bdi.examples.hunterprey;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Grid2D;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.IResultListener;
import jadex.commons.service.SServiceProvider;

import java.util.Map;

/**
 *  Action for eating food or another creature.
 */
public class EatAction extends SimplePropertyObject implements ISpaceAction
{
	//-------- constants --------
	
	/** The property for the points of a creature. */
	public static final	String	PROPERTY_POINTS	= "points";
	
	//-------- IAgentAction interface --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
//		System.out.println("eat action: "+parameters);
		
		Grid2D grid = (Grid2D)space;
		IComponentIdentifier owner = (IComponentIdentifier)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject avatar = grid.getAvatar(owner);
		final ISpaceObject target = (ISpaceObject)parameters.get(ISpaceAction.OBJECT_ID);
		
		if(null==space.getSpaceObject(target.getId()))
		{
			throw new RuntimeException("No such object in space: "+target);
		}
		
		if(!avatar.getProperty(Space2D.PROPERTY_POSITION).equals(target.getProperty(Space2D.PROPERTY_POSITION)))
		{
			throw new RuntimeException("Can only eat objects at same position.");
		}
		
		Integer	points	= (Integer)avatar.getProperty(PROPERTY_POINTS);
		if(avatar.getType().equals("prey") && target.getType().equals("food"))
		{
			points	= points!=null ? new Integer(points.intValue()+1) : new Integer(1);
		}
		else if(avatar.getType().equals("hunter") && target.getType().equals("prey"))
		{
			points	= points!=null ? new Integer(points.intValue()+5) : new Integer(5);
		}
		else
		{
			throw new RuntimeException("Objects of type '"+avatar.getType()+"' cannot eat objects of type '"+target.getType()+"'.");
		}
		
		space.destroySpaceObject(target.getId());
		
		// Todo: Use listener model for self destroying of agent!?
		if(target.getProperty(ISpaceObject.PROPERTY_OWNER)!=null)
		{
//			System.err.println("Destroying: "+target.getProperty(ISpaceObject.PROPERTY_OWNER));
			SServiceProvider.getServiceUpwards(space.getContext().getServiceProvider(), IComponentManagementService.class).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					cms.destroyComponent((IComponentIdentifier)target.getProperty(ISpaceObject.PROPERTY_OWNER));
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}
		
		avatar.setProperty(PROPERTY_POINTS, points);
//		System.out.println("Object eaten: "+target);
		
		return null;
	}
}
