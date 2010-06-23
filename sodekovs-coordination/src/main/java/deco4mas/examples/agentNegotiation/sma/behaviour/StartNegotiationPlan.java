package deco4mas.examples.agentNegotiation.sma.behaviour;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.history.ServiceAgentHistory;
import deco4mas.examples.agentNegotiation.sma.negotiationStrategy.history.TrustEvent;
import deco4mas.examples.agentNegotiation.sma.workflow.management.NeededService;

/**
 * Assign a Sa with Deco4mas
 */
public class StartNegotiationPlan extends Plan
{
	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// history
			if (getBeliefbase().getBelief("history").getFact() == null)
				getBeliefbase().getBelief("history").setFact(
					new ServiceAgentHistory(this.getComponentIdentifier(), ClockTime.getStartTime(getClock())));
			ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();

			// trustFunc
			if (getBeliefbase().getBelief("trustFunction").getFact() == null)
			{
				Map<TrustEvent, Double> eventWeight = new HashMap<TrustEvent, Double>();
				eventWeight.put(TrustEvent.SuccessfullRequest, 1.0);
				eventWeight.put(TrustEvent.FailedRequest, -6.0);
				eventWeight.put(TrustEvent.CancelArrangement, -1.0);
				getBeliefbase().getBelief("trustFunction").setFact(
					new HistorytimeTrustFunction(this.getComponentIdentifier(), history, eventWeight));
			}

			smaLogger.info("Start assign all needed services");
			// assign all needed services
			NeededService[] services = (NeededService[]) getBeliefbase().getBeliefSet("neededServices").getFacts();
			for (NeededService service : services)
			{
				IGoal assign = createGoal("assignSa");
				assign.getParameter("service").setValue(service);
				dispatchSubgoalAndWait(assign);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
