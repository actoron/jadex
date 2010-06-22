package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * All SMAs are ready
 */
public class SmaReadyPlan extends Plan
{

	public void body()
	{
		final Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());
		try
		{
			startAtomic();
			IMessageEvent me = (IMessageEvent) getReason();
			String smaName = (String) me.getParameter("content").getValue();

			getBeliefbase().getBeliefSet("smaReady").addFact(smaName);

			smaLogger.info("sma ready [" + me.getParameter("sender").getValue().toString() + "]");
			if (getBeliefbase().getBeliefSet("smaReady").size() == getBeliefbase().getBeliefSet("smas").size()
				&& !((Boolean) getBeliefbase().getBelief("executionPhase").getFact()))
			{
				smaLogger.info("all sma ready");
				getBeliefbase().getBelief("executionPhase").setFact(new Boolean(true));

				dispatchTopLevelGoal(createGoal("startWorkflow"));
				endAtomic();
				aborted();
			} else
			{
				endAtomic();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
