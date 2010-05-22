package deco4mas.examples.agentNegotiation.sa;

import java.util.Random;
import jadex.bdi.runtime.Plan;

/**
 * Sometimes produce Blackouts
 * agentType.getBlackoutCharakter definite frequenz and duration (max ca. 50% blackout time, min 0%)
 */
public class BlackoutPlan extends Plan
{
	private Random rnd = new Random();
	
	public void body()
	{
		waitFor(5000);
		AgentType agentType = (AgentType) getBeliefbase().getBelief("agentType").getFact();
		Double blackoutCharakter = agentType.getBlackoutCharacter();

		if (rnd.nextDouble()>(0.5 + (1.0-blackoutCharakter) * 0.5))
		{
			getBeliefbase().getBelief("blackout").setFact(Boolean.TRUE);
			
			Double waitTime = 20000 * blackoutCharakter;
			System.out.println(this.getComponentName() + ": Blackout for " + waitTime);
			waitFor(waitTime.longValue());
			System.out.println(this.getComponentName() + ": Blackout end!");
			getBeliefbase().getBelief("blackout").setFact(Boolean.FALSE);
		}
		body();
	}
}
