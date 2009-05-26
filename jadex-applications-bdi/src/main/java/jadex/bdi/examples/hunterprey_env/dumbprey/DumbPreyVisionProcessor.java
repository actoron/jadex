package jadex.bdi.examples.hunterprey_env.dumbprey;

import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.examples.hunterprey_env.CreatureVisionGenerator;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;
import jadex.commons.concurrent.IResultListener;

/**
 *  Dumb prey vision processer.
 *  Updates the agent's "nearest_food" belief.
 */
public class DumbPreyVisionProcessor implements IPerceptProcessor
{
	/**
	 *  Process a new percept.
	 *  @param space The space.
	 *  @param type The type.
	 *  @param percept The percept.
	 *  @param agent The agent identifier.
	 */
	public void processPercept(final ISpace space, final String type, final Object percept, final IAgentIdentifier agent)
	{
		if(((ISpaceObject)percept).getType().equals("food"))
		{
			IAMS ams = (IAMS)((IApplicationContext)space.getContext()).getPlatform().getService(IAMS.class);
			ams.getExternalAccess(agent, new IResultListener()
			{
				public void exceptionOccurred(Exception exception)
				{
					// May happen when agent has been killed concurrently.
//					exception.printStackTrace();
				}
				public void resultAvailable(Object result)
				{
					final Space2D	space2d	= (Space2D)space;
					final IExternalAccess	exta	= (IExternalAccess)result;
					exta.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								ISpaceObject	myself	= space2d.getOwnedObjects(agent)[0];
								IBelief	nearfoodbel	= exta.getBeliefbase().getBelief("nearest_food");
								ISpaceObject	nearfood	= (ISpaceObject)nearfoodbel.getFact();
								
								// Remember new food only if nearer than other known food (if any).
								if(type.equals(CreatureVisionGenerator.OBJECT_APPEARED) || type.equals(CreatureVisionGenerator.OBJECT_MOVED))
								{
									if(nearfood==null
										|| space2d.getDistance((IVector2)myself.getProperty(Space2D.POSITION),
												(IVector2)nearfood.getProperty(Space2D.POSITION))
										.greater(
											space2d.getDistance((IVector2)myself.getProperty(Space2D.POSITION),
												(IVector2)((ISpaceObject)percept).getProperty(Space2D.POSITION))))
									{
										nearfoodbel.setFact(percept);
									}
								}
								// Remove disappeared food from belief.
								else if(percept.equals(nearfood) && type.equals(CreatureVisionGenerator.OBJECT_DISAPPEARED))
								{
									nearfoodbel.setFact(null);
								}
							}
							catch(Exception e)
							{
								// Todo: fix agent init.
								// Exception might be thrown, when agent not yet initialized
								// -> AgentRules.findValue() fails due to missing initparents,
								// when belief is initialized on demand.
							}
						}
					});
				}
			});
		}
	}
}