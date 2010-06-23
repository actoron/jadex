package deco4mas.examples.agentNegotiation.sma.behaviour;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.AssignRequest;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.RequestInformation;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.decoMAS.medium.NegSpaceMechanism;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.ITrustFunction;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.SimpleSelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.WeightFactorUtilityFunction;
import deco4mas.examples.agentNegotiation.sma.workflow.management.NeededService;

/**
 * Assign a Sa with Deco4mas
 */
public class AssignSaPlan extends Plan
{
	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// get service and currentSa
			NeededService neededService = (NeededService) ((IGoal) getReason()).getParameter("service").getValue();

			// LOG
			System.out.println(getComponentName() + ": Assign a Sa with deco");
			smaLogger.info("assign a new sa for " + neededService.getServiceType().getName());

			// utilityFunc
			WeightFactorUtilityFunction utilityFunction = new WeightFactorUtilityFunction(this.getComponentIdentifier(),
				(ITrustFunction) getBeliefbase().getBelief("trustFunction").getFact());
			Double costWeight = 0.3;
			Double durationWeight = 0.2;
			Double trustWeight = 0.5;
			ServiceType service = (ServiceType) neededService.getServiceType();
			utilityFunction.addFactor("cost", costWeight, service.getMaxCost(), service.getMinCost(), false);
			utilityFunction.addFactor("duration", durationWeight, service.getMaxDuration(), service.getMinDuration(), false);
			utilityFunction.addFactor("trust", trustWeight, 100.0, 0.0, true);
			smaLogger.info("weight for utility " + costWeight + ", " + durationWeight + ", " + trustWeight);

			// Selector
			SimpleSelectionStrategy selector = new SimpleSelectionStrategy();

			// extra info
			Map<String, Object> information = new HashMap<String, Object>();
			information.put("deadline", 500L);
			smaLogger.info("deadline 500L");
			RequestInformation info = new RequestInformation(information);

			// request
			AssignRequest request = new AssignRequest(this.getComponentIdentifier(), service, utilityFunction, selector,
				NegSpaceMechanism.NAME, info);

			// Internal Event for assign Sa
			IInternalEvent assignSaEvent = createInternalEvent("assignSa");
			assignSaEvent.getParameter("request").setValue(request);
			assignSaEvent.getParameter("task").setValue("assignSaRequest");
			dispatchInternalEvent(assignSaEvent);
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
