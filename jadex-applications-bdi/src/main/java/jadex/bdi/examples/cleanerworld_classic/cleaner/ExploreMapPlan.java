package jadex.bdi.examples.cleanerworld_classic.cleaner;

import jadex.bdi.examples.cleanerworld_classic.Location;
import jadex.bdi.examples.cleanerworld_classic.MapPoint;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

import java.util.List;
import java.util.Random;


/**
 *  Plan to explore the map by going to the seldom visited positions.
 *  Uses the absolute quantity to go to positions that are not yet
 *  explored.
 */
public class ExploreMapPlan extends Plan
{
	/** Random number generator. */
	protected Random	rnd	= new Random();

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Select randomly one of the seldom visited locations.
		List<MapPoint> mps = (List<MapPoint>)getExpression("query_min_quantity").execute();
		MapPoint mp = mps.get(0);
		int cnt	= 1;
		for( ; cnt<mps.size(); cnt++)
		{
			MapPoint mp2 = (MapPoint)mps.get(cnt);
			if(mp.getSeen()!=mp2.getSeen())
				break;
		}
		mp	= (MapPoint)mps.get(rnd.nextInt(cnt));
//		MapPoint[]	mps = (MapPoint[])getBeliefbase().getBeliefSet("visited_positions").getFacts();
//		MapPoint mp = mps[(int)(Math.random()*mps.length)];

		Location dest = mp.getLocation();
		IGoal moveto = createGoal("achievemoveto");
		moveto.getParameter("location").setValue(dest);		
//		System.out.println("Created: "+dest+" "+this);
		dispatchSubgoalAndWait(moveto);
//		System.out.println("Reached: "+dest+" "+this);
	}
}
