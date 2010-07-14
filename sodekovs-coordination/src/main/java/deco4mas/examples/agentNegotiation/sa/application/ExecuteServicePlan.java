package deco4mas.examples.agentNegotiation.sa.application;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.Execution;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.TrustEvent;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.sa.masterSa.AgentType;

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
			AgentType agentType = (AgentType) getBeliefbase().getBelief("agentType").getFact();

			// get Logger
			Logger workflowLogger = AgentLogger.getTimeEvent((String) request.getParameter("action").getValue());
			Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// execute correct?
			Boolean executionCorrect = false;

			// blackout?
			if (!(Boolean) getBeliefbase().getBelief("blackout").getFact())
			{
				// LOG
				Double duration = myService.getMedDuration() * (0.5 + agentType.getCostCharacter());
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
					System.out.println(getComponentName() + ": " + duration + " false executed!");
					workflowLogger.info("execution by " + this.getComponentName() + " finished false");
					saLogger.info("executtion finished false!");
				}

			} else
			{
				// LOG
				System.out.println(getComponentName() + " missed request");
				workflowLogger.info("execution missed by " + this.getComponentName());
				saLogger.info("executtion missed!");
			}
			
			Execution execution = null;
			// set result
			if (executionCorrect)
			{
				getParameter("result").setValue(Boolean.TRUE);
				execution = new Execution(this.getComponentIdentifier(), (IComponentIdentifier) request.getParameter("initiator")
					.getValue(), myService, TrustEvent.SuccessfullRequest, this.getTime());
			} else
			{
				// getParameter("result").setValue(Boolean.FALSE);
				// no result is set, timeout at sma should occur
				execution = new Execution(this.getComponentIdentifier(), (IComponentIdentifier) request.getParameter("initiator")
					.getValue(), myService, TrustEvent.FailedRequest, this.getTime());
			}

			// Contract contract = (Contract)
			// getBeliefbase().getBelief("contract").getFact();
			// contract.setExecution(execution);
			// getBeliefbase().getBelief("contract").modified();
			
			IInternalEvent event = createInternalEvent("executionOccur");
			event.getParameter("execution").setValue(execution);
			event.getParameter("task").setValue("executionOccur");
			dispatchInternalEvent(event);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
