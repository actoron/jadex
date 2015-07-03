package jadex.bdi.examples.cleanerworld_classic.cleaner;

import jadex.bdi.examples.cleanerworld_classic.Location;
import jadex.bdi.examples.cleanerworld_classic.MapPoint;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Memorize the visited positions.
 */
public class MemorizePositionsPlan extends Plan
{
	//-------- attributes --------

	/** The forget factor. */
	protected double forget;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public MemorizePositionsPlan()
	{
		this.forget = 0.01;
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		while(true)
		{
			Location	my_location	= (Location)getBeliefbase().getBelief("my_location").getFact();
			double	my_vision	= ((Double)getBeliefbase().getBelief("my_vision").getFact()).doubleValue();
			MapPoint[] mps = (MapPoint[])getBeliefbase().getBeliefSet("visited_positions").getFacts();
			for(int i=0; i<mps.length; i++)
			{
				if(my_location.isNear(mps[i].getLocation(), my_vision))
				{
					mps[i].setQuantity(mps[i].getQuantity()+1);
					mps[i].setSeen(1);
				}
				else
				{
					double oldseen = mps[i].getSeen();
					double newseen = oldseen - oldseen*forget;
					mps[i].setSeen(newseen);
				}
			}

			//System.out.println("inc: "+SUtil.arrayToString(mps));
			waitFor(300);
		}
	}
}
