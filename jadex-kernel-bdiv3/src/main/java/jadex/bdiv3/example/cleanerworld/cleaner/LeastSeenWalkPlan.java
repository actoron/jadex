package jadex.bdiv3.example.cleanerworld.cleaner;

import jadex.bdiv3.example.cleanerworld.world.Location;
import jadex.bdiv3.example.cleanerworld.world.MapPoint;

import java.util.List;


/**
 *  Walk to the least visited positions.
 *  Uses a relative measure to go to seldom seen positions.
 */
public class LeastSeenWalkPlan 
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public LeastSeenWalkPlan()
	{
//		getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Select randomly one of the least seen locations.
//		List	mps = (List)getExpression("query_min_seen").execute();
//		MapPoint mp = (MapPoint)mps.get(0);
//		int cnt	= 1;
//		for( ; cnt<mps.size(); cnt++)
//		{
//			MapPoint mp2 = (MapPoint)mps.get(cnt);
//			if(mp.getSeen()!=mp2.getSeen())
//				break;
//		}
//		mp	= (MapPoint)mps.get((int)(Math.random()*cnt));
////		MapPoint[]	mps = (MapPoint[])getBeliefbase().getBeliefSet("visited_positions").getFacts();
////		MapPoint mp = mps[(int)(Math.random()*mps.length)];
//
//		Location dest = mp.getLocation();
//		IGoal moveto = createGoal("achievemoveto");
//		moveto.getParameter("location").setValue(dest);
////		System.out.println("Created: "+dest+" "+this);
//		dispatchSubgoalAndWait(moveto);
////		System.out.println("Reached: "+dest+" "+this);
	}
	
//	public void aborted()
//	{
//		System.out.println("Aborted: "+this);
//	}
	
//	public void failed()
//	{
//		System.out.println("Failed: "+this);
//	}
}
