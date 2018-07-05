package jadex.micro.examples.hunterprey;

import java.util.Map;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;

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
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
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
			points	= points!=null ? Integer.valueOf(points.intValue()+1) : Integer.valueOf(1);
		}
		else if(avatar.getType().equals("hunter") && target.getType().equals("prey"))
		{
			points	= points!=null ? Integer.valueOf(points.intValue()+5) : Integer.valueOf(5);
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
			space.getExternalAccess().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					cms.destroyComponent(((IComponentDescription)target.getProperty(ISpaceObject.PROPERTY_OWNER)).getName());
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
