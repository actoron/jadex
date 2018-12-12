package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Create pieces of garbage in the environment.
 */
public class CreatePlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("env").getFact();

		int garb_cnt = 0;
		while(true)
		{
			// Add a piece of waste randomly.
			waitFor(1000);
//			Position	pos	= env.getFreePosition();
			Position	pos	= env.getRandomPosition();
			if(pos!=null)
			{
				env.addWorldObject(Environment.GARBAGE, "garbage#"+garb_cnt++, pos);
			}
		}
	}
}
