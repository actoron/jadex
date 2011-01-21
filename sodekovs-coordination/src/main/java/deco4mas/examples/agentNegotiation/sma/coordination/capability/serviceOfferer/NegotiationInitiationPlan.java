package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.RequestedService;
import deco4mas.examples.agentNegotiation.common.dataObjects.RequiredService;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.DirectNegotiationInitatorInformation;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.NegotiationInformation;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ITrustFunction;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.SimpleSelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.WeightFactorUtilityFunction;

/**
 * Assign a Sa with Deco4mas
 */
public class NegotiationInitiationPlan extends Plan
{
	private static Integer id = 0;

	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// get service
			RequiredService neededService = (RequiredService) ((IGoal) getReason()).getParameter("requiredService").getValue();

			// LOG
			System.out.println(getComponentName() + "[InitNegoitiation]: " + neededService);
			smaLogger.info("[InitNegoitiation]: " + neededService);

			// utilityFunc
			WeightFactorUtilityFunction utilityFunction = new WeightFactorUtilityFunction(getComponentIdentifier());
			utilityFunction.setTrustFunction((ITrustFunction) getBeliefbase().getBelief("trustFunction").getFact());

			// add costs
			Double costWeight = (Double) getBeliefbase().getBelief("CostWeight").getFact();
			Double durationWeight = (Double) getBeliefbase().getBelief("DurationWeight").getFact();
			Double trustWeight = (Double) getBeliefbase().getBelief("TrustWeight").getFact();
			ServiceType service = (ServiceType) neededService.getServiceType();
			utilityFunction.addFactor("cost", costWeight, service.getMaxCost(), service.getMinCost(), false);
			utilityFunction.addFactor("duration", durationWeight, service.getMaxDuration(), service.getMinDuration(), false);
			utilityFunction.addFactor("trust", trustWeight, 100.0, 0.0, true);

			smaLogger.info("weight for utility " + costWeight + ", " + durationWeight + ", " + trustWeight);		
			System.out.println("TTTTTTTTTTTTTTTTTTT" + costWeight + " - " + durationWeight + "  " + trustWeight);

			// Selector
			SimpleSelectionStrategy selector = new SimpleSelectionStrategy();

			// request
			NegotiationInformation info = null;
			for (String mediumName : (String[]) getBeliefbase().getBeliefSet("negTypes").getFacts())
			{
				Map<String, Object> information = new HashMap<String, Object>();
				if (mediumName.equals("by_neg"))
				{
					// extra info
					information.put("deadline", 2000L);
					smaLogger.info("deadline 2000L");
				}

				info = new DirectNegotiationInitatorInformation(id, this.getComponentIdentifier(), service, utilityFunction, selector,
					information);
			}

			getBeliefbase().getBeliefSet("requestedServices").addFact(
				new RequestedService(this.getComponentIdentifier(), info.getServiceType()));
			
			// Internal Event for assign Sa
			IInternalEvent assignSaEvent = createInternalEvent("initiateNegotiation");
			assignSaEvent.getParameter("information").setValue(info);
			dispatchInternalEvent(assignSaEvent);
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
