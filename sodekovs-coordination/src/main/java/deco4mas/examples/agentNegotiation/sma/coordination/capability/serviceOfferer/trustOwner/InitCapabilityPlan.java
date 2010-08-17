package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer.trustOwner;

import jadex.bdi.runtime.Plan;
import java.util.HashMap;
import java.util.Map;
import deco4mas.examples.agentNegotiation.common.trustInformation.TrustEvent;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ServiceAgentHistory;

/**
 * Init capability beliefs
 */
public class InitCapabilityPlan extends Plan
{
	final ParameterLogger trustLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("TrustChange_" + this.getComponentName());

	public void body()
	{
		try
		{
			// history
			ServiceAgentHistory history = new ServiceAgentHistory(this.getComponentIdentifier(), ClockTime.getStartTime(getClock()));
			getBeliefbase().getBelief("history").setFact(history);

			// trustFunc
			Map<TrustEvent, Double> eventWeight = new HashMap<TrustEvent, Double>();
			eventWeight.put(TrustEvent.SuccessfullRequest, 0.5);
			eventWeight.put(TrustEvent.FailedRequest, -10.0);
			eventWeight.put(TrustEvent.CancelContract, -1.0);
			HistorytimeTrustFunction trustFunction = new HistorytimeTrustFunction(this.getComponentIdentifier(), history, eventWeight);
			getBeliefbase().getBelief("trustFunction").setFact(trustFunction);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
