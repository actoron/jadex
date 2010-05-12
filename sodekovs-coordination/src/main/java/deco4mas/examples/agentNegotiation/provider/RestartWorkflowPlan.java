package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 * Creates a workflow
 */
public class RestartWorkflowPlan extends Plan
{
	static Integer restarttimer = new Integer(0);

	public void body()
	{

		try
		{
			if (restarttimer < 10)
			{
				getBeliefbase().getBelief("workflow").setFact(null);
				getBeliefbase().getBelief("executionPhase").setFact(new Boolean(false));
				getBeliefbase().getBeliefSet("smaReady").removeFacts();
				
				IGoal restart = createGoal("preInstantiateWorkflow");
				dispatchTopLevelGoal(restart);
			}
			restarttimer++;
		} catch (Exception e)
		{
			System.out.println(this.getType());
			System.out.println(e.getMessage());
			fail(e);
		}
	}
}
