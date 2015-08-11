package jadex.bdi.examples.hunterprey_classic.creature.preys.basicbehaviour;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.Location;
import jadex.bdi.examples.hunterprey_classic.Obstacle;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdi.examples.hunterprey_classic.WorldObject;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *  Plan to wander around in the environment and avoid obstacles.
 */
public class WanderAroundPlan	extends Plan
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
		int	dir	= rand.nextInt(4);
		
		while(true)
		{
			// Look whats around.
			Creature me = ((Creature)getBeliefbase().getBelief("my_self").getFact());
			Vision vision = ((Vision)getBeliefbase().getBelief("vision").getFact());
			
			WorldObject[]	objects	= vision.getObjects();
			Location target = me.createLocation(me.getLocation(), Creature.alldirs[dir]);
			
			List pod = new ArrayList();
			pod.add(Integer.valueOf(0));	
			pod.add(Integer.valueOf(1));			
			pod.add(Integer.valueOf(2));			
			pod.add(Integer.valueOf(3));
			for(int i=1; i<4 && me.getObject(target, objects) instanceof Obstacle; i++)
			{
			    //System.out.println("prob at: "+me.getLocation()+" "+Creature.alldirs[dir]);
				pod.remove(Integer.valueOf(dir));
				dir = ((Integer)pod.get(rand.nextInt(4-i))).intValue();
				target = me.createLocation(me.getLocation(), Creature.alldirs[dir]);
			}
			if(me.getObject(target, objects) instanceof Obstacle)
			{
				System.out.println("Surrounded by walls :-( "+me.getName()+" "+me.getLocation());
			}
			else
			{
				IGoal move = createGoal("move");
				move.getParameter("direction").setValue(Creature.alldirs[dir]);
				dispatchSubgoalAndWait(move);
			}
		}
	}
}


