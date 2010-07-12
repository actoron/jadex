package deco4mas.examples.agentNegotiation.sma.coordination.capability.trustOwner;

import jadex.bdi.runtime.Plan;
import java.util.HashMap;
import java.util.Map;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.TrustEvent;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ServiceAgentHistory;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.WeightFactorUtilityFunction;

/**
 * init agent beliefs
 */
public class InitAgentPlan extends Plan
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
			eventWeight.put(TrustEvent.SuccessfullRequest, 1.0);
			eventWeight.put(TrustEvent.FailedRequest, -8.0);
			eventWeight.put(TrustEvent.CancelArrangement, -1.0);
			HistorytimeTrustFunction trustFunction = new HistorytimeTrustFunction(this.getComponentIdentifier(), history, eventWeight);
			((WeightFactorUtilityFunction) getBeliefbase().getBelief("utilityFunction").getFact()).setTrustFunction(trustFunction);
			getBeliefbase().getBelief("trustFunction").setFact(trustFunction);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
