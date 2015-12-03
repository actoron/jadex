package jadex.bdiv3.examples.cleanerworld.cleaner;

import java.util.Set;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.examples.cleanerworld.world.MapPoint;
import jadex.bdiv3.runtime.IPlan;



/**
 *  Memorize the visited positions.
 */
@Plan
public class MemorizePositionsPlan 
{
	//-------- attributes --------

	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	/** The forget factor. */
	protected double forget;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public MemorizePositionsPlan()
	{
//		getLogger().info("Created: "+this);

		this.forget = 0.01;
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		Location my_location = capa.getMyLocation();
		double	my_vision	= capa.getMyVision();
		Set<MapPoint> mps = capa.getVisitedPositions();
		for(MapPoint mp: mps)
		{
			if(my_location.isNear(mp.getLocation(), my_vision))
			{
				mp.setQuantity(mp.getQuantity()+1);
				mp.setSeen(1);
			}
			else
			{
				double oldseen = mp.getSeen();
				double newseen = oldseen - oldseen*forget;
				mp.setSeen(newseen);
			}
		}
	}
}
