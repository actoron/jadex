package deco4mas.examples.agentNegotiation.sa.coordination.capability.serviceSupplier;

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
		try
		{
			final Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());
			saLogger.info("Sign end at request by sma");
			getBeliefbase().getBelief("signed").setFact(Boolean.FALSE);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
