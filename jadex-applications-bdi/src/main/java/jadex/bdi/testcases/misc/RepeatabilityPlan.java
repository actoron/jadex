package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Change some beliefs and belief sets in an atomic block,
 *  to trigger several conditions.
 */
public class RepeatabilityPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		startAtomic();
		getBeliefbase().getBelief("a").setFact("test");
		getBeliefbase().getBelief("b").setFact("test");
		getBeliefbase().getBeliefSet("c").addFact("test");
		getBeliefbase().getBeliefSet("d").addFact("test");
		endAtomic();
	}
}
