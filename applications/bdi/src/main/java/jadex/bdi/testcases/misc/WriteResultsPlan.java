package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan writes a result and kills the agent.
 */
public class WriteResultsPlan extends Plan
{
	/**
	 *  The plan code.
	 */
	public void body()
	{
		String arg = (String)getBeliefbase().getBelief("arg").getFact();
		getBeliefbase().getBelief("arg").setFact(arg+"--written");
		killAgent();
	}
}