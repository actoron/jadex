package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.examples.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.examples.cleanerworld.world.MapPoint;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


/**
 *  Walk to the least visited positions.
 *  Uses a relative measure to go to seldom seen positions.
 */
public class LeastSeenWalkPlan 
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected IPlan rplan;
	
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
	@PlanBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		// Select randomly one of the least seen locations.
//		List<MapPoint>	mps = (List<MapPoint>)getExpression("query_min_seen").execute();
		List<MapPoint>	mps = getMinSeen();
		MapPoint mp = (MapPoint)mps.get(0);
		int cnt	= 1;
		for( ; cnt<mps.size(); cnt++)
		{
			MapPoint mp2 = (MapPoint)mps.get(cnt);
			if(mp.getSeen()!=mp2.getSeen())
				break;
		}
		mp	= (MapPoint)mps.get((int)(Math.random()*cnt));
//		MapPoint[]	mps = (MapPoint[])getBeliefbase().getBeliefSet("visited_positions").getFacts();
//		MapPoint mp = mps[(int)(Math.random()*mps.length)];

		Location dest = mp.getLocation();
		
		IFuture<AchieveMoveTo> fut = rplan.dispatchSubgoal(capa.new AchieveMoveTo(dest));
		fut.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
		{
			public void customResultAvailable(AchieveMoveTo amt)
			{
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected List<MapPoint> getMinSeen()
	{
		Set<MapPoint> locs = capa.getVisitedPositions();
		List<MapPoint> ret = new ArrayList<MapPoint>(locs);
		Collections.sort(ret, new Comparator<MapPoint>()
		{
			public int compare(MapPoint o1, MapPoint o2)
			{
				return o1.getSeen()>o2.getSeen()? 1: o1.getSeen()==o2.getSeen()? o1.hashCode()>o2.hashCode()? 1: -1: -1;
			}
		});
		return ret;
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
