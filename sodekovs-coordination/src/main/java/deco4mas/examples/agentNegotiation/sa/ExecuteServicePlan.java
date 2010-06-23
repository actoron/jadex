package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Execute a service
 */
public class ExecuteServicePlan extends Plan
{
	public void body()
	{
		IGoal request = (IGoal) getReason();
		Logger workflowLogger = AgentLogger.getTimeEvent((String) request.getParameter("action").getValue());
		Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

		ServiceType myService = (ServiceType) getBeliefbase().getBelief("providedService").getFact();
		AgentType agentType = (AgentType) getBeliefbase().getBelief("agentType").getFact();

		Double duration = myService.getMedDuration() * (0.5 + agentType.getCostCharacter());
		workflowLogger.info(myService.getName() + " execute by " + this.getComponentName() + " for " + duration);
		saLogger.info("execute servicerequest by " + (String) request.getParameter("action").getValue() + " for " + duration);

		waitFor(duration.longValue());
		System.out.println(getComponentName() + ": " + duration + " EXECUTED!");
		workflowLogger.info("execution by " + this.getComponentName() + " finished");
		saLogger.info("executtion finished");
		// System.out.println();

		if ((Boolean) getBeliefbase().getBelief("blackout").getFact())
		{
			getParameter("result").setValue(Boolean.FALSE);
		} else
		{
			getParameter("result").setValue(Boolean.TRUE);
		}
		// getParameter("result").setValue(Boolean.TRUE);
	}
}
