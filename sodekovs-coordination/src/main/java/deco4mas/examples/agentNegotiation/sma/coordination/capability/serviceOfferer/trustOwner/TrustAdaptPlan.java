package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer.trustOwner;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.trustInformation.TrustExecutionInformation;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ServiceAgentHistory;

/**
 * Assign a Sa with Deco4mas
 */
public class TrustAdaptPlan extends Plan
{
	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			IInternalEvent event = (IInternalEvent) getReason();
			TrustExecutionInformation exe = ((TrustExecutionInformation) event.getParameter("information").getValue());

			// LOG
			System.out.println(getComponentName() + ": Trust for " + exe);
			smaLogger.info("Adapt Trust: " + exe);

			// more trust to Sa
			ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
			smaLogger.info("history: "+ exe);
			history.addEvent(exe.getSa().getLocalName(), getClock().getTime(), exe.getEvent());
			ValueLogger.addValue(exe.getEvent() + "_" + exe.getSa(), 1.0);
			((HistorytimeTrustFunction) getBeliefbase().getBelief("trustFunction").getFact()).logTrust(getTime());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
