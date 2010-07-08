package deco4mas.examples.agentNegotiation.sma.coordination.capability.trustOwner;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.Execution;
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
			Execution exe = ((Execution) event.getParameter("execution").getValue());

			// LOG
			System.out.println(getComponentName() + ": Trust for " + exe.getSa() + " changed (" + exe.getEvent() + ")");
			smaLogger.info("Adapt Trust for " + ((Execution) event.getParameter("execution").getValue()).getSa() + "(" + exe.getEvent()
				+ ")");

			// more trust to Sa
			ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
			smaLogger.info("history: " + exe.getSa() + " (" + exe.getEvent() + ")");
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
