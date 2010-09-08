package deco4mas.examples.agentNegotiation.sa.application;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.ExecutedService;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceAgentType;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Execute a service request
 */
public class ExecuteServicePlan extends Plan
{
	public void body()
	{
		try
		{
			// request
			IGoal request = (IGoal) getReason();
			ServiceType myService = (ServiceType) getBeliefbase().getBelief("providedService").getFact();
			ServiceAgentType agentType = (ServiceAgentType) getBeliefbase().getBelief("serviceAgentType").getFact();

			// get Logger
			Logger workflowLogger = AgentLogger.getTimeEvent((String) request.getParameter("action").getValue());
			Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// execute correct?
			Boolean executionCorrect = false;

			// blackout?
			if (!(Boolean) getBeliefbase().getBelief("blackout").getFact())
			{
				// LOG
				Double duration = myService.getMedDuration() * (0.5 + agentType.getDurationCharacter());
				workflowLogger.info(myService.getName() + " execute by " + this.getComponentName() + " for " + duration);
				saLogger.info("execute servicerequest by " + (String) request.getParameter("action").getValue() + " for " + duration);

				// EXECUTE
				waitFor(duration.longValue());

				// blackout during execution?
				if (!(Boolean) getBeliefbase().getBelief("blackout").getFact())
				{
					// Correct and LOG
					executionCorrect = true;
					System.out.println(getComponentName() + ": " + duration + " CORRECT EXECUTED!");
					workflowLogger.info("execution by " + this.getComponentName() + " finished correct");
					saLogger.info("executtion finished correct!");
				} else
				{
					// LOG
					System.out.println(getComponentName() + ": " + duration + " FALSE EXECUTED!");
					workflowLogger.info("execution by " + this.getComponentName() + " finished false");
					saLogger.info("executtion finished false!");
				}

			} else
			{
				// LOG
				System.out.println(getComponentName() + " MISSED EXECUTION");
				workflowLogger.info("execution missed by " + this.getComponentName());
				saLogger.info("MISSED EXECUTION");
			}

			ExecutedService execution = null;
			// set result
			if (executionCorrect)
			{
				getParameter("result").setValue(Boolean.TRUE);
				execution = new ExecutedService((IComponentIdentifier) request.getParameter("initiator").getValue(), this
					.getComponentIdentifier(), myService, true, this.getTime());
			} else
			{
				// getParameter("result").setValue(Boolean.FALSE);
				// no result is set, timeout at sma should occur

				execution = new ExecutedService((IComponentIdentifier) request.getParameter("initiator").getValue(), this
					.getComponentIdentifier(), myService, false, this.getTime());
			}
			getBeliefbase().getBeliefSet("executedServices").addFact(execution);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
