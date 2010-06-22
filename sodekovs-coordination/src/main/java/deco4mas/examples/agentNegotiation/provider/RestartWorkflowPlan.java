package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Creates a workflow
 */
public class RestartWorkflowPlan extends Plan
{
	// static Map<IComponentIdentifier, Integer> restarttimers = new
	// HashMap<IComponentIdentifier, Integer>();
	private Long startTime = ClockTime.getStartTime(getClock());

	public void body()
	{
		final Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());
		try
		{
			// Integer timer = restarttimers.get(this.getComponentIdentifier());
			// if (timer == null) timer = 1;

			if ((getTime() - startTime) <= 300000)
			{
				smaLogger.info("start new workflow");
				getBeliefbase().getBelief("workflow").setFact(null);
				getBeliefbase().getBelief("executionPhase").setFact(new Boolean(false));
				getBeliefbase().getBeliefSet("smaReady").removeFacts();

				IGoal restart = createGoal("preInstantiateWorkflow");
				dispatchTopLevelGoal(restart);
			} else
			{
				ValueLogger.log();
			}
			// timer++;
			// restarttimers.put(this.getComponentIdentifier(), timer);
		} catch (Exception e)
		{
			System.out.println(this.getType());
			e.printStackTrace();
			fail(e);
		}
	}
}
