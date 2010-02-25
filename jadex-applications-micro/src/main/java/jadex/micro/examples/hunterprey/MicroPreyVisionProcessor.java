package jadex.micro.examples.hunterprey;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.IPerceptProcessor;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.IMicroExternalAccess;

/**
 *  Dumb prey vision processer.
 *  Updates the agent's "nearest_food" belief.
 */
public class MicroPreyVisionProcessor	extends	SimplePropertyObject	implements IPerceptProcessor
{
	/**
	 *  Process a new percept.
	 *  @param space The space.
	 *  @param type The type.
	 *  @param percept The percept.
	 *  @param agent The agent identifier.
	 *  @param agent The avatar of the agent (if any).
	 */
	public void processPercept(final IEnvironmentSpace space, final String type, final Object percept, final IComponentIdentifier agent, final ISpaceObject avatar)
	{
		IComponentManagementService ces = (IComponentManagementService)space.getContext().getServiceContainer().getService(IComponentManagementService.class);
		ces.getExternalAccess(agent, new IResultListener()
		{
			public void exceptionOccurred(Object source, Exception exception)
			{
				// May happen when agent has been killed concurrently.
				exception.printStackTrace();
			}
			public void resultAvailable(Object source, Object result)
			{
				final Space2D	space2d	= (Space2D)space;
				final IMicroExternalAccess	exta	= (IMicroExternalAccess)result;
				exta.invokeLater(new Runnable()
				{
					public void run()
					{
						MicroPreyAgent	mp	= (MicroPreyAgent)exta.getAgent();
						ISpaceObject	nearfood	= mp.getNearestFood();
						
						// Remember new food only if nearer than other known food (if any).
						if(type.equals("food_seen"))
						{
							if(nearfood==null
								|| space2d.getDistance((IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION),
										(IVector2)nearfood.getProperty(Space2D.PROPERTY_POSITION))
								.greater(
									space2d.getDistance((IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION),
										(IVector2)((ISpaceObject)percept).getProperty(Space2D.PROPERTY_POSITION))))
							{
								mp.setNearestFood((ISpaceObject)percept);
							}
						}
						// Remove disappeared food from belief.
						else if(percept.equals(nearfood) && (type.equals("food_out_of_sight") || type.equals("food_eaten")))
						{
							mp.setNearestFood(null);
						}
					}
				});
			}
		});
	}
}