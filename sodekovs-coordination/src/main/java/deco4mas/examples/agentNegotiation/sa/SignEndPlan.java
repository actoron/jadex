package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * sign end
 */
public class SignEndPlan extends Plan
{

	public void body()
	{
		final Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());
		saLogger.info("Sign end at request by sma");
		getBeliefbase().getBelief("signed").setFact(Boolean.FALSE);
	}
}
