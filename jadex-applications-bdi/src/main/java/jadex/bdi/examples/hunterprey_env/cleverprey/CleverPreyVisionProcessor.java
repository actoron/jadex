package jadex.bdi.examples.hunterprey_env.cleverprey;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.examples.hunterprey_env.CreatureVisionGenerator;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;
import jadex.commons.concurrent.IResultListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  Clever prey vision processor.
 *  Updates the agent's "food" belief set.
 */
public class CleverPreyVisionProcessor implements IPerceptProcessor
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
		// Add newly seen food.
		if(((ISpaceObject)percept).getType().equals("food")
			&& type.equals(CreatureVisionGenerator.OBJECT_APPEARED))
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
					final IExternalAccess	exta	= (IExternalAccess)result;
					exta.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								// Add, if not already known.
								// Todo: object updates (position) recognized, even if not in vision!?
								// -> only post object copies in percepts!?
								IBeliefSet	foodbelset	= exta.getBeliefbase().getBeliefSet("food");
								if(!foodbelset.containsFact(percept))
									foodbelset.addFact(percept);
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
		// Todo: remove on object destroyed.
		
		// Remove disappeared known food, when creature moves.
		else if(percept.equals(((AbstractEnvironmentSpace)space).getOwnedObjects(agent)[0])
			&& type.equals(CreatureVisionGenerator.OBJECT_MOVED))
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
					final IExternalAccess	exta	= (IExternalAccess)result;
					exta.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								// Remove known food, which is in vision range but not seen.
								int vision	= 2; // Todo: set in creature.
								Space2D	space2d	= (Space2D)space;
								IVector2	mypos	= (IVector2)((ISpaceObject)percept).getProperty(Space2D.POSITION);
								IBeliefSet	foodbelset	= exta.getBeliefbase().getBeliefSet("food");
								ISpaceObject[]	known	= (ISpaceObject[])foodbelset.getFacts();
								Set	seen	= new HashSet(Arrays.asList(space2d.getNearObjects(mypos, new Vector1Int(vision))));
								for(int i=0; i<known.length; i++)
								{
									if(!seen.contains(known[i]) && space2d.getDistance(mypos, (IVector2)known[i].getProperty(Space2D.POSITION)).getAsInteger()<=vision)
									{
										System.out.println("Removing disappeared food: "+percept+", "+known[i]);
										foodbelset.removeFact(known[i]);
									}
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