package jadex.bdiv3.example.cleanerworld.cleaner;



/**
 *  Plan to explore the map by going to the seldom visited positions.
 *  Uses the absolute quantity to go to positions that are not yet
 *  explored.
 */
public class ExploreMapPlan 
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ExploreMapPlan()
	{
//		getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		// Select randomly one of the seldom visited locations.
//		List	mps = (List)getExpression("query_min_quantity").execute();
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
}
