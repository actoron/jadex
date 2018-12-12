package jadex.bdi.examples.hunterprey_classic.creature.preys.basicbehaviour;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.Hunter;
import jadex.bdi.examples.hunterprey_classic.Obstacle;
import jadex.bdi.examples.hunterprey_classic.RequestMove;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdi.examples.hunterprey_classic.WorldObject;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Try to run away from a hunter.
 */
public class EscapePreyPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Creature me = ((Creature)getBeliefbase().getBelief("my_self").getFact());
		Vision vision = (Vision)getBeliefbase().getBelief("vision").getFact();
		WorldObject[] obs = vision.getObjects();
		HashMap points = new HashMap();
		points.put(RequestMove.DIRECTION_UP, Integer.valueOf(0));
		points.put(RequestMove.DIRECTION_DOWN, Integer.valueOf(0));
		points.put(RequestMove.DIRECTION_RIGHT, Integer.valueOf(0));
		points.put(RequestMove.DIRECTION_LEFT, Integer.valueOf(0));

		for(int i=0; i<obs.length; i++)
		{
			if(obs[i] instanceof Hunter)
			{
				String[] dirs = me.getDirections(obs[i]);
				for(int j=0; j<dirs.length; j++)
				{
					int actual = ((Integer)points.get(dirs[j])).intValue();
					if(actual!=Integer.MAX_VALUE)
						points.put(dirs[j], Integer.valueOf(((Integer)points.get(dirs[j])).intValue()+1));
				}
			}
			if(obs[i] instanceof Obstacle)
			{
				if(me.getDistance(obs[i])==1)
				{
					String[] dirs = me.getDirections(obs[i]);
					points.put(dirs[0], Integer.valueOf(Integer.MAX_VALUE));
				}
			}
		}

		Object[] sortpoints = points.entrySet().toArray(new Map.Entry[points.size()]);

		Arrays.sort(sortpoints, new Comparator()
		{
			public int	compare(Object o1, Object o2)
			{
				return ((Integer)((Map.Entry)o1).getValue()).intValue()
						- ((Integer)((Map.Entry)o2).getValue()).intValue();
			}
		});

		// todo: make random selection of equal directions!
		//System.out.println("+++ "+SUtil.arrayToString(sortpoints));

		IGoal move = createGoal("move");
		move.getParameter("direction").setValue(((Map.Entry)sortpoints[0]).getKey());
		dispatchSubgoalAndWait(move);

	}

}
