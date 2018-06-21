package jadex.bdi.examples.hunterprey_classic.creature.hunters.dumbhunter;

import java.util.ArrayList;
import java.util.Random;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.Prey;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdi.examples.hunterprey_classic.WorldObject;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;


/**
 *  Plan to move around in the environment and try to catch prey.
 */
public class DumbHunterPlan	extends Plan
{
	//-------- attributes --------

	/** Random number generator. */
	protected Random	rand	= new Random(hashCode());

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		waitForFactChanged("vision");
	    //waitForCondition(getCondition("has_vision"));
		//int	dir	= 0;

		while(true)
		{
			// Look whats around.
			Creature me = ((Creature)getBeliefbase().getBelief("my_self").getFact());
			Vision vision = ((Vision)getBeliefbase().getBelief("vision").getFact());
	
			WorldObject[]	objects	= vision.getObjects();
			me.sortByDistance(objects);
	    	String[] posdirs = me.getPossibleDirections(objects);

			// Find nearest interesting objects.
			int	distance	= Integer.MAX_VALUE;
			ArrayList	interesting	= new ArrayList();
			for(int i=0; i<objects.length; i++)
			{
				if(objects[i] instanceof Prey)
				{
					int	dist	= me.getDistance(objects[i]);
					if(dist>distance)
						break;
					interesting.add(objects[i]);
					distance	= dist;
				}
			}

			// Take appropriate action (move or eat).
			if(interesting.size()>0)
			{
				WorldObject	obj	= (WorldObject)interesting.get(rand.nextInt(interesting.size()));
				// Move towards nearest object.
				String[] dirs	= me.getDirections(obj);
	        	String[] posmoves = (String[])SUtil.cutArrays(dirs, posdirs);

				if(me.getDistance(obj)==0)
				{
					eat(obj);
				}
				else if(posmoves.length>0)
				{
					// Move towards object.
					move(posmoves[rand.nextInt(posmoves.length)]);
				}
				else
				{
					// Move randomly.
					move(posdirs[rand.nextInt(posdirs.length)]);
				}
			}
			else
			{
				// Move randomly.
				move(posdirs[rand.nextInt(posdirs.length)]);
			}
		}
	}

	/**
	 *  Move towards a direction.
	 */
	protected void move(String direction)
	{
		try
		{
			//System.out.println(getAgentName()+" wants to move: "+direction);
			IGoal move = createGoal("move");
			move.getParameter("direction").setValue(direction);
			dispatchSubgoalAndWait(move);
		}
		catch(GoalFailureException e)
		{
			getLogger().warning("Move goal failed");
		}
	}

	/**
	 *  Eat an object.
	 */
	protected void eat(WorldObject object)
	{
		try
		{
			IGoal eat = createGoal("eat");
			eat.getParameter("object").setValue(object);
			dispatchSubgoalAndWait(eat);
		}
		catch(GoalFailureException e)
		{
			getLogger().warning("Move goal failed");
		}
	}

}


