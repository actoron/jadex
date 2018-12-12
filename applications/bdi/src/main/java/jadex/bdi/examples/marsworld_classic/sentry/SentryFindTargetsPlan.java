package jadex.bdi.examples.marsworld_classic.sentry;

import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Look if a target is near to my position.
 */
public class SentryFindTargetsPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("Created: "+this);
		
		while(true)
		{
			Environment env = (Environment)getBeliefbase().getBelief("move.environment").getFact();
			Location myloc = (Location)getBeliefbase().getBelief("move.my_location").getFact();
			Double myvis = (Double)getBeliefbase().getBelief("move.my_vision").getFact();

			Target[] ts = env.getTargetsNear(myloc, myvis.doubleValue());
			//if(ts.length>0)
			//	System.out.println("Sees: "+SUtil.arrayToString(ts));
			for(int i=0; i<ts.length; i++)
			{
				if(!getBeliefbase().getBeliefSet("my_targets").containsFact(ts[i])
				 && !getBeliefbase().getBeliefSet("analysed_targets").containsFact(ts[i]))
				{
					//System.out.println("Found a new target: "+ts[i]);
					getBeliefbase().getBeliefSet("my_targets").addFact(ts[i]);
				}
			}

			waitFor(20);
		}
	}
}
