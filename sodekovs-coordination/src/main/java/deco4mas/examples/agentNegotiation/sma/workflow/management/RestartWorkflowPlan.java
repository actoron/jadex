package deco4mas.examples.agentNegotiation.sma.workflow.management;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Creates a workflow
 */
public class RestartWorkflowPlan extends Plan
{
	public void body()
	{
		try
		{
			// LOG
			final Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			NeededService[] services = (NeededService[]) getBeliefbase().getBeliefSet("neededServices").getFacts();
			for (NeededService service : services)
			{
				smaLogger.info("Send SignEnd to " + service.getSa().getLocalName());
				IMessageEvent me = createMessageEvent("informMessage");

				List cis = new LinkedList();
				cis.add(service.getSa());
				me.getParameter("receivers").setValue(cis);
				me.getParameter("content").setValue("sign end");
				sendMessage(me);
			}

			Long startTime = ClockTime.getStartTime(getClock());

			// restart until 100000 ZE (~sec)
			if ((getTime() - startTime) <= 100000)
			{
				// LOG
				smaLogger.info("start new workflow");
				getBeliefbase().getBelief("workflow").setFact(null);
				getBeliefbase().getBelief("executionPhase").setFact(new Boolean(false));
				getBeliefbase().getBeliefSet("neededServices").removeFacts();

				// restart
				IGoal restart = createGoal("preInstantiateWorkflow");
				dispatchTopLevelGoal(restart);
			} else
			{
				ValueLogger.log();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
