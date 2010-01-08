package jadex.micro.examples.hunterprey_classic;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.CurrentVision;
import jadex.bdi.examples.hunterprey_classic.Food;
import jadex.bdi.examples.hunterprey_classic.Location;
import jadex.bdi.examples.hunterprey_classic.Prey;
import jadex.bdi.examples.hunterprey_classic.RequestEat;
import jadex.bdi.examples.hunterprey_classic.RequestMove;
import jadex.bdi.examples.hunterprey_classic.RequestVision;
import jadex.bdi.examples.hunterprey_classic.WorldObject;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.MessageType;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.MicroAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *  Simple agent participating in (bdi-based) hunter prey.
 */
public class MicroPreyAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The creature's self representation. */
	protected Creature	myself;
	
	/** Random number generator. */
	protected Random	rand;

	/** The environment agent. */
	protected IComponentIdentifier	environment;
	
	//-------- MicroAgent methods --------

	/**
	 *  Execute a step.
	 */
	public void executeBody()
	{
		// Todo: getAgentName()
		myself	= new Prey(getAgentIdentifier().getLocalName(),
			getAgentIdentifier(), new Location(10,10));

		this.rand	= new Random(hashCode());
		
		if(environment==null)
		{
			register();
		}
	}
	
	/**
	 *  React on messages from environment.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
//		System.out.println("message arrived: "+msg);
		Object content	= msg.get(SFipa.CONTENT);
		if(content instanceof CurrentVision)
		{
			// The following is almost exact copy from DumbPreyPlan
			myself	= ((CurrentVision)content).getCreature();
			WorldObject[]	objects	= ((CurrentVision)content).getVision().getObjects();
			myself.sortByDistance(objects);
	    	String[] posdirs = myself.getPossibleDirections(objects);

			// Find nearest interesting objects.
			int	distance	= Integer.MAX_VALUE;
			ArrayList	interesting	= new ArrayList();
			for(int i=0; i<objects.length; i++)
			{
				if(objects[i] instanceof Food)
				{
					int	dist	= myself.getDistance(objects[i]);
					if(dist>distance)
						break;
					interesting.add(objects[i]);
					distance	= dist;
				}
			}

			// Take appropriate action (move or eat).
			if(interesting.size()>0 && Math.random()>0.1)
			{
				WorldObject	obj	= (WorldObject)interesting.get(rand.nextInt(interesting.size()));
				// Move towards nearest object.
				String[] dirs	= myself.getDirections(obj);
	        	String[] posmoves = (String[])SUtil.cutArrays(dirs, posdirs);

				if(myself.getDistance(obj)==0)
				{
					requestAction(new RequestEat(myself, obj));
				}
				else if(posmoves.length>0)
				{
					// Move towards object.
					requestAction(new RequestMove(myself,
						posmoves[rand.nextInt(posmoves.length)]));
				}
				else
				{
					// Move randomly.
					requestAction(new RequestMove(myself,
						posdirs[rand.nextInt(posdirs.length)]));
				}
			}
			else
			{
				// Move randomly.
				requestAction(new RequestMove(myself,
					posdirs[rand.nextInt(posdirs.length)]));
			}
		}
	}

	//-------- helper methods --------
	
	/**
	 *  Search for an environment and register creature. 
	 */
	protected void register()
	{
		// Create a service description to search for.
		IDF df = (IDF)getServiceContainer().getService(IDF.class);
		IDFServiceDescription sd = df.createDFServiceDescription(null, "hunter-prey environment", null);
		IDFAgentDescription ad = df.createDFAgentDescription(null, sd);
		ISearchConstraints	cons = df.createSearchConstraints(-1, 0);
		
		// Search for the environment agent
		df.search(ad, cons, createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IDFAgentDescription[] tas = (IDFAgentDescription[])result;
				if(tas.length!=0)
				{
					// Found.
					environment	= tas[0].getName();
					if(tas.length>1)
						System.out.println("More than environment agent found.");
						// Todo: getLogger()
						// getLogger().warning("More than environment agent found.");
					else
						System.out.println("Environment agent found: "+environment);
				}
				else
				{
					// Not found.
					throw new RuntimeException("Environment not found.");
				}

				// Register creature.
				requestAction(new RequestVision(myself));
			}
			public void exceptionOccurred(Object source, Exception e)
			{
				e.printStackTrace();
			}
		}));
	}

	/**
	 *  Request an action from the environment
	 */
	protected void requestAction(Object action)
	{
		Map	msg	= new HashMap();
		msg.put(SFipa.PERFORMATIVE, SFipa.REQUEST);
		msg.put(SFipa.PROTOCOL, SFipa.PROTOCOL_REQUEST);
		msg.put(SFipa.CONVERSATION_ID, SUtil.createUniqueId(myself.getName()));
		msg.put(SFipa.CONTENT, action);
		msg.put(SFipa.LANGUAGE, SFipa.NUGGETS_XML);
		msg.put(SFipa.RECEIVERS, Collections.singletonList(environment));
		msg.put(SFipa.SENDER, getAgentIdentifier());
		// Todo: message service shouldn't allow sending anonymous messages (i.e. w/o sender)
		sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE);
	}	
}
