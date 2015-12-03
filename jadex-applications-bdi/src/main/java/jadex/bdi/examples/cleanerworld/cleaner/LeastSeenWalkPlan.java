package jadex.bdi.examples.cleanerworld.cleaner;

import java.util.List;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;


/**
 *  Walk to the least visited positions.
 *  Uses a relative measure to go to seldom seen positions.
 */
public class LeastSeenWalkPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Use shortest not seen point
		Space2D env = (Space2D)getBeliefbase().getBelief("environment").getFact();
		IVector2 mypos = (IVector2)getBeliefbase().getBelief("my_location").getFact();

		List<MapPoint> mps = (List<MapPoint>)getExpression("query_min_seen").execute();
		MapPoint mp = (MapPoint)mps.get(0);
		IVector1 dist = env.getDistance(mypos, mp.getLocation());
		int cnt	= 1;
		
		for( ; cnt<mps.size(); cnt++)
		{
			MapPoint mp2 = (MapPoint)mps.get(cnt);
			if(mp.getSeen()!=mp2.getSeen())
				break;
			IVector1 dist2 = env.getDistance(mypos, mp2.getLocation());
			if(dist2.less(dist))
			{
				mp = mp2;
				dist = dist2;
			}
		}
		
//		mp	= (MapPoint)mps.get((int)(Math.random()*cnt));
//		MapPoint[]	mps = (MapPoint[])getBeliefbase().getBeliefSet("visited_positions").getFacts();
//		MapPoint mp = mps[(int)(Math.random()*mps.length)];

		IVector2 dest = mp.getLocation();
		IGoal moveto = createGoal("achievemoveto");
		moveto.getParameter("location").setValue(dest);
//		System.out.println("Created: "+dest+" "+this);
		dispatchSubgoalAndWait(moveto);
//		System.out.println("Reached: "+dest+" "+this);
	}
	
//	public void aborted()
//	{
//		System.out.println("Aborted: "+this);
//	}
//	
//	public void failed()
//	{
//		System.out.println("Failed: "+this);
//	}
}
