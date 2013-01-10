package jadex.bdiv3.example.cleanerworld.cleaner;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.example.cleanerworld.cleaner.CleanerBDI.AchieveMoveTo;
import jadex.bdiv3.example.cleanerworld.world.MapPoint;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;



/**
 *  Plan to explore the map by going to the seldom visited positions.
 *  Uses the absolute quantity to go to positions that are not yet
 *  explored.
 */
public class ExploreMapPlan 
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected RPlan rplan;
	
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
	@PlanBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();

		// Select randomly one of the seldom visited locations.
//		List	mps = (List)getExpression("query_min_quantity").execute();
		List<MapPoint> mps = getMinQuantity();
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

		rplan.dispatchSubgoal(capa.new AchieveMoveTo(mp.getLocation()))
			.addResultListener(new ExceptionDelegationResultListener<CleanerBDI.AchieveMoveTo, Void>(ret)
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
	protected List<MapPoint> getMinQuantity()
	{
		Set<MapPoint> locs = capa.getVisitedPositions();
		List<MapPoint> ret = new ArrayList<MapPoint>(locs);
		Collections.sort(ret, new Comparator<MapPoint>()
		{
			public int compare(MapPoint o1, MapPoint o2)
			{
				return o1.getQuantity()-o2.getQuantity();
			}
		});
		return ret;
	}

}
