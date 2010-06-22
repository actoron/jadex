package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.deco.AssignRequest;
import deco4mas.examples.agentNegotiation.deco.RequestInformation;
import deco4mas.examples.agentNegotiation.deco.ServiceType;
import deco4mas.examples.agentNegotiation.deco.negMedium.NegSpaceMechanism;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.sma.strategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.strategy.SimpleSelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.strategy.WeightFactorUtilityFunction;

/**
 * Assign a Sa with Deco4mas
 */
public class AssignSaDecoPlan extends Plan
{
	public void body()
	{
		Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());
		IComponentIdentifier currentSa = (IComponentIdentifier) getBeliefbase().getBelief("currentSa").getFact();
		if (getBeliefbase().getBelief("history").getFact() == null)
			getBeliefbase().getBelief("history").setFact(
				new ServiceAgentHistory(this.getComponentIdentifier(), ClockTime.getStartTime(getClock())));
		if (currentSa != null)
		{
			smaLogger.info("Send SignEnd to " + currentSa.getLocalName());
			IMessageEvent me = createMessageEvent("informMessage");

			List cis = new LinkedList();
			cis.add(currentSa);
			me.getParameter("receivers").setValue(cis);
			me.getParameter("content").setValue("sign end");
			sendMessage(me);
		}
		getBeliefbase().getBelief("currentSa").setFact(null);
		System.out.println(getComponentName() + ": Assign a Sa with deco");
		smaLogger.info("assign a new sa");

		// Internal Event for assign Sa
		IInternalEvent assignSaEvent = createInternalEvent("assignSa");

		ServiceType myService = (ServiceType) getBeliefbase().getBelief("allocatedService").getFact();

		ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
		// trustFunc
		Map<TrustEvent, Double> eventWeight = new HashMap<TrustEvent, Double>();
		eventWeight.put(TrustEvent.SuccessfullRequest, 1.0);
		eventWeight.put(TrustEvent.FailedRequest, -6.0);
		eventWeight.put(TrustEvent.CancelArrangement, -1.0);

		HistorytimeTrustFunction trustFunction = new HistorytimeTrustFunction(this.getComponentIdentifier(), history, eventWeight);
		// utilityFunc
		WeightFactorUtilityFunction utilityFunction = new WeightFactorUtilityFunction(this.getComponentIdentifier(), trustFunction);
		utilityFunction.addFactor("cost", 0.5, myService.getMaxCost(), myService.getMinCost(), false);
		utilityFunction.addFactor("duration", 0.3, myService.getMaxDuration(), myService.getMinDuration(), false);
		utilityFunction.addFactor("trust", 0.3, 100.0, 0.0, true);
		smaLogger.info("weight for utility C10, D5, T25 (hack!)");
		// Selector
		SimpleSelectionStrategy selector = new SimpleSelectionStrategy();
		// extra info
		Map<String, Object> information = new HashMap<String, Object>();
		information.put("deadline", 500L);
		smaLogger.info("deadline 500(hack!)");
		RequestInformation info = new RequestInformation(information);
		// request
		AssignRequest request = new AssignRequest(this.getComponentIdentifier(), myService, utilityFunction, selector,
			NegSpaceMechanism.NAME, info);

		assignSaEvent.getParameter("request").setValue(request);
		assignSaEvent.getParameter("task").setValue("assignSaRequest");
		dispatchInternalEvent(assignSaEvent);

	}
}
