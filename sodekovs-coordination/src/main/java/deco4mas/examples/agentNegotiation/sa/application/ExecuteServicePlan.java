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

		IInternalEvent executionOccur = createInternalEvent("executionOccur");
		executionOccur.getParameter("task").setValue("executionOccur");
		Execution execution = null;

		if ((Boolean) getBeliefbase().getBelief("blackout").getFact())
		{
			getParameter("result").setValue(Boolean.FALSE);
			execution = new Execution(this.getComponentIdentifier(), (IComponentIdentifier) request.getParameter("initiator").getValue(),
				myService, TrustEvent.FailedRequest, this.getTime());

		} else
		{
			getParameter("result").setValue(Boolean.TRUE);
			execution = new Execution(this.getComponentIdentifier(), (IComponentIdentifier) request.getParameter("initiator").getValue(),
				myService, TrustEvent.SuccessfullRequest, this.getTime());

		}
		executionOccur.getParameter("execution").setValue(execution);
		dispatchInternalEvent(executionOccur);
		// getParameter("result").setValue(Boolean.TRUE);
	}
}
