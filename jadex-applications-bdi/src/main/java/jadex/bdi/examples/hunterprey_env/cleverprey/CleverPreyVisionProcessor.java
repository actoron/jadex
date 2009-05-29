package jadex.bdi.examples.hunterprey_env.cleverprey;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.IPerceptProcessor;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.adapter.base.fipa.IAMS;
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
//		System.out.println("Percept: "+type+", "+percept+", "+agent.getLocalName());
		
		// Add newly seen food / remove eaten food.
		if(type.equals("food_seen") || type.equals("food_eaten") || type.equals("food_out_of_sight"))
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
								IBeliefSet	seen_food	= exta.getBeliefbase().getBeliefSet("seen_food");
								IBeliefSet	known_food	= exta.getBeliefbase().getBeliefSet("known_food");

								// Add seen food, if not already known.
								// Todo: object updates (position) recognized, even if not in vision!?
								// -> only post object copies in percepts!?
								if(type.equals("food_seen"))
								{
									if(!seen_food.containsFact(percept))
										seen_food.addFact(percept);
									if(!known_food.containsFact(percept))
										known_food.addFact(percept);
								}

								// Remove eaten food, if known.
								else if(type.equals("food_eaten"))
								{
									if(seen_food.containsFact(percept))
										seen_food.removeFact(percept);
									if(known_food.containsFact(percept))
										known_food.removeFact(percept);
								}

								// Remove seen food, when out of sight known.
								else if(type.equals("food_out_of_sight"))
								{
									if(seen_food.containsFact(percept))
										seen_food.removeFact(percept);
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

		// Remove disappeared known food, when creature moves.
		else if(percept.equals(((AbstractEnvironmentSpace)space).getOwnedObjects(agent)[0])
			&& type.equals("prey_moved"))
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
								IBeliefSet	seen_food	= exta.getBeliefbase().getBeliefSet("seen_food");
								IBeliefSet	known_food	= exta.getBeliefbase().getBeliefSet("known_food");
								ISpaceObject[]	known	= (ISpaceObject[])known_food.getFacts();
								Set	seen	= new HashSet(Arrays.asList(space2d.getNearObjects(mypos, new Vector1Int(vision), null)));
								for(int i=0; i<known.length; i++)
								{
									if(!seen.contains(known[i]) && space2d.getDistance(mypos, (IVector2)known[i].getProperty(Space2D.POSITION)).getAsInteger()<=vision)
									{
										System.out.println("Removing disappeared food: "+percept+", "+known[i]);
										known_food.removeFact(known[i]);
										if(seen_food.containsFact(known[i]))
											seen_food.removeFact(known[i]);
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