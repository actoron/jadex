package deco4mas.examples.agentNegotiation.sa;

import java.util.Random;
import jadex.bdi.runtime.Plan;

/**
 * Sometimes produce Blackouts
 */
public class BlackoutPlan extends Plan
{
	public void body()
	{
		Random rnd = new Random();
		if (rnd.nextDouble()>0.95)
		{
			getBeliefbase().getBelief("blackout").setFact(Boolean.TRUE);
			waitFor(15000);
			getBeliefbase().getBelief("blackout").setFact(Boolean.FALSE);
		}
		waitFor(4000);
		body();
	}
}
