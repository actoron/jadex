package deco4mas.examples.agentNegotiation.sa.application;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Decite if service is acceptable
 */
public class ServiceDecitePlan extends Plan
{
	public void body()
	{
		IGoal request = (IGoal) getReason();
		Logger workflowLogger = AgentLogger.getTimeEvent((String) request.getParameter("action").getValue());
		Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

		if ((Boolean) getBeliefbase().getBelief("blackout").getFact())
		{
			workflowLogger.info(this.getComponentName() + " blackout");
			saLogger.info("missed request");
			getParameter("accept").setValue(Boolean.FALSE);
		} else
		{
			getParameter("accept").setValue(Boolean.TRUE);
		}
	}
}
