package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;

/**
 *  Memorize the visited positions.
 */
public class MemorizePositionsPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		double forget = 0.01;
		
		while(true)
		{
			IVector2 my_location	= (IVector2)getBeliefbase().getBelief("my_location").getFact();
			MapPoint[] mps = (MapPoint[])getBeliefbase().getBeliefSet("visited_positions").getFacts();
			double	my_vision	= ((Double)getBeliefbase().getBelief("my_vision").getFact()).doubleValue();
			IVector1 dist = new Vector1Double(my_vision);

			for(int i=0; i<mps.length; i++)
			{
				if(dist.greater(my_location.getDistance(mps[i].getLocation())))
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
			waitForTick();
		}
	}
}
