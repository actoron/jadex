package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer;

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
import deco4mas.examples.agentNegotiation.sma.application.RequiredService;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.SimpleSelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.WeightFactorUtilityFunction;

/**
 * Assign a Sa with Deco4mas
 */
public class SearchSaPlan extends Plan
{
	// TODO Entfernen
	static private WeightFactorUtilityFunction utilityFunction;

	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// get service and currentSa
			RequiredService neededService = (RequiredService) ((IGoal) getReason()).getParameter("service").getValue();

			// LOG
			System.out.println(getComponentName() + ": Assign a Sa with deco");
			smaLogger.info("assign a new sa for " + neededService.getServiceType().getName());

			if (utilityFunction == null)
				utilityFunction = ((WeightFactorUtilityFunction) getBeliefbase().getBelief("utilityFunction").getFact());
			// utilityFunc
			// WeightFactorUtilityFunction utilityFunction =
			// ((WeightFactorUtilityFunction)getBeliefbase().getBelief("utilityFunction").getFact());

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
			information.put("deadline", 300L);
			smaLogger.info("deadline 300L");
			RequestInformation info = new RequestInformation(information);

			// request
			AssignRequest request = new AssignRequest(this.getComponentIdentifier(), service, utilityFunction, selector,
				NegSpaceMechanism.NAME, neededService.getId(), info);

			// Internal Event for assign Sa
			IInternalEvent assignSaEvent = createInternalEvent("searchSa");
			assignSaEvent.getParameter("request").setValue(request);
			assignSaEvent.getParameter("task").setValue("searchSa");
			dispatchInternalEvent(assignSaEvent);
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
