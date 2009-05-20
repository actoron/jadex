package jadex.bdi.examples.hunterprey_env.dumbhunter;

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
 *  Dumb hunter vision processer.
 *  Updates the agent's "nearest_prey" belief.
 */
public class DumbHunterVisionProcessor implements IPerceptProcessor
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
		if(((ISpaceObject)percept).getType().equals("prey"))
		{
			IAMS ams = (IAMS)((IApplicationContext)space.getContext()).getPlatform().getService(IAMS.class);
			ams.getExternalAccess(agent, new IResultListener()
			{
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
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
								IBelief	nearpreybel	= exta.getBeliefbase().getBelief("nearest_prey");
								ISpaceObject	nearprey	= (ISpaceObject)nearpreybel.getFact();
								
								// Remember new prey only if nearer than other known prey (if any).
								if(type.equals(CreatureVisionGenerator.OBJECT_APPEARED) || type.equals(CreatureVisionGenerator.OBJECT_MOVED))
								{
									if(nearprey==null
										|| space2d.getDistance((IVector2)myself.getProperty(Space2D.POSITION),
												(IVector2)nearprey.getProperty(Space2D.POSITION))
										.greater(
											space2d.getDistance((IVector2)myself.getProperty(Space2D.POSITION),
												(IVector2)((ISpaceObject)percept).getProperty(Space2D.POSITION))))
									{
										nearpreybel.setFact(percept);
									}
								}
								// Remove disappeared prey from belief.
								else if(percept.equals(nearprey) && type.equals(CreatureVisionGenerator.OBJECT_DISAPPEARED))
								{
									nearpreybel.setFact(null);
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